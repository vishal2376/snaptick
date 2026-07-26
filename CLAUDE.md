# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this
repository.

## Project

Snaptick — offline-first Android task planner (Kotlin + Jetpack Compose). Single-module Gradle
project (`:app`), `minSdk=26`, `compileSdk=36`, `targetSdk=36`. Current version: **4.2** (`versionCode 14`).

## Common commands

Run from repo root using the Gradle wrapper.

- Build debug APK: `./gradlew assembleDebug`
- Build release APK: `./gradlew assembleRelease` (release signing config + R8 shrink/obfuscate;
  signed locally via `keystore.properties`)
- Install debug on connected device: `./gradlew installDebug`
- Unit tests: `./gradlew test` (single variant: `./gradlew testDebugUnitTest`)
- Single unit test class:
  `./gradlew :app:testDebugUnitTest --tests "com.vishal2376.snaptick.ExampleUnitTest"`
- Instrumented tests: `./gradlew connectedDebugAndroidTest`
- Lint: `./gradlew lint` (reports in `app/build/reports/lint-results-*.html`)
- Clean: `./gradlew clean`

Debug build has `applicationIdSuffix=.debug`, so debug + release can coexist on one device.

The project's Gradle (8.11.1) + AGP (8.10.1) targets JDK 17. If the system default is JDK 19+, invoke Gradle
with `JAVA_HOME=/path/to/jdk17 ./gradlew …`.

## Architecture

MVI per screen. Each screen has its own Hilt ViewModel that exposes an immutable
`StateFlow<…State>`, a `SharedFlow<…Event>` for one-off side effects (toasts, navigation), and
accepts user intents via `onAction(action: …Action)`. Composables are stateless and receive
`state + events + onAction` from `AppNavigation` (which wires each VM via `hiltViewModel()`). Hilt
wires everything; Room is the sole source of truth.

### ViewModels (one per feature, not one per screen)

- `presentation/main/viewmodel/MainViewModel.kt` — app-wide state (theme, language, sleep time,
  streak, sort, swipe, 24h format, first-time opened, build version, calendar sync target, ICS
  import preview, onboarding state). Owns backup/restore (two-stage: `PreviewBackup` →
  `pendingRestore` dialog → `ConfirmRestore`), ICS import (size-capped + streaming preview),
  calendar sync toggle. Emits `MainEvent.ShowToast` / `OpenMail` / `BackupPreviewReady` /
  `IcsParsedReady` / etc. Locale propagation lives here too: `settingsStore.languageKey.collect`
  calls `updateLocale(context, lang)` so language changes apply across the whole app, not just Home.

- `presentation/task_list/viewmodel/TaskListViewModel.kt` — shared across `home_screen`,
  `completed_task_screen`, `this_week_task_screen`, `calender_screen`, `free_time_screen`. Exposes
  `todayTasks: Flow<List<Task>>` (via `repository.getTodayTasksWithCompletions()`) and
  `allTasks: Flow<List<Task>>`. Handles `TaskListAction` (toggle completion, swipe, delete, undo).
  Each screen gets its own VM instance per nav back-stack entry; state is consistent because it
  streams from the DB. Reminder scheduling is owned by `TaskRepository`, not this VM.

- `presentation/add_edit_screen/viewmodel/AddEditViewModel.kt` — form state + persistence for Add
  and Edit. Reads `id` nav arg via `SavedStateHandle`; if > 0 loads existing task, else starts
  blank. `AddEditState` is a full form-state data class with an `isLoaded` flag — wheel pickers are
  gated on `isLoaded` so they don't snapshot stale defaults before the task arrives.
  `UpdateStartTime` shifts `endTime` by maintaining duration; `UpdateEndTime` recomputes `duration`
  from the new gap so the duration chip falls back to "Custom" automatically when the gap doesn't
  match `30/60/90`. `UpdateDurationMinutes` resets `pomodoroTimer = -1` directly (no event-loop side
  channel). Emits `TaskSaved/TaskUpdated/TaskDeleted` which the screen uses to pop the backstack.

- `presentation/pomodoro_screen/viewmodel/PomodoroViewModel.kt` — timer state (timeLeft, isPaused,
  isCompleted). Ticker runs via
  `viewModelScope.launch { _state.map { stopped }.distinctUntilChanged().collectLatest { while (active) delay(1s); update } }`.
  **Persistence is incremental**: the timer writes its remaining time to `task.pomodoroTimer` every
  10 ticks (≈10s) via the repository. `onCleared` only cancels the ticker; no `GlobalScope.launch`
  post-cancel write. Worst case loses ≤10s of progress on hard kill instead of the entire session.
  Resume clamps `pomodoroTimer.toLong().coerceIn(0, total)` so a stale saved value can't render
  negative progress. Emits `ResumingPreviousSession`, `TimerCompleted`, `TaskMarkedCompleted`.

No god `TaskViewModel` — the old monolithic one was split. Per-screen folders each contain `state/`,
`action/`, `events/`, `viewmodel/`, `components/`.

### Layers

- `data/local/` — Room `TaskDatabase` (v4, tables `task_table` + `task_completions`), `TaskDao`,
  `TaskCompletionDao`, migrations `MIGRATION_1_2`, `MIGRATION_2_3`, `MIGRATION_3_4`. *
  *`fallbackToDestructiveMigration()` is intentionally NOT enabled** — every schema bump must ship a
  real `Migration`. DB has dual access: Hilt-provided singleton in `di/AppModule.kt` **and**
  `TaskDatabase.getInstance(context)` for widget Glance actions that run outside DI scope. Both must
  stay in sync (same name `local_db`, same migrations) or widget writes will diverge from app
  writes.

- `data/repositories/TaskRepository.kt` — wraps DAOs + `ReminderScheduler` + `CalendarPusher`. Every
  mutating call (`insert/update/delete/deleteAll`, `markCompletedForDate`, `unmarkCompletedForDate`)
  cancels then re-arms the per-task reminder, pushes to the device calendar (no-op when calendar
  sync is disabled), and enqueues `WidgetUpdateWorker.enqueueWorker(context)` so the widget reflects
  DB changes. Restore goes through `restoreFromBackup(BackupData)` which wraps the wipe+insert in
  `database.withTransaction { ... }` so a mid-restore failure rolls back to pre-restore state.

    - **Today expansion** (`getTodayTasksWithCompletions()`): combines the one-off list (
      `dao.getTasksByDate(today)`) with active repeat templates (`dao.getActiveRepeats(today)` =
      `WHERE isRepeated = 1 AND date <= today`), filters via `Task.shouldOccurOn(today)`, dedupes by
      id, and merges `task_completions` rows so a per-day completed repeat shows as
      `isCompleted = true`. Repeats are virtualized at query time — there is no daily rollover
      worker.
    - **Backup snapshot** (`snapshotBackup()`): emits a `BackupData` carrying `version`, all tasks,
      and all completion rows.

- `data/calendar/` — `CalendarRepository` (read writable calendars, permission check),
  `CalendarPusher` (insert/update/delete CalendarContract events keyed by `Task.calendarEventId`),
  `CalendarImporter` + `ics/IcsParser` (size-capped, streaming `.ics` import with per-line +
  per-event limits). Calendar sync is opt-in; toggling off walks every previously-pushed task and
  clears their device-calendar event.

- `domain/model/` — `Task` entity (Room `@Entity` + domain model share one class), `BackupData`,
  `BackupCompletion`, `BACKUP_VERSION = 1`. `Task.repeatWeekdays` is a comma-separated string of
  weekday indices (Mon=0…Sun=6) parsed via `getRepeatWeekList()`. `Task.shouldOccurOn(date)` is the
  **single source of truth** for "does this task occur on this date" — used by the repository,
  calendar filter, and ThisWeek expansion. Display formatting is **not** on `Task` — use
  `presentation/common/utils/TaskFormatter.kt` (`formatTaskTime`, `formatDuration`,
  `formatDurationTimestamp`, `formatWeekDays`).

- `domain/converters/` — Room `TypeConverter`s for `LocalDate` and `LocalTime` (ISO-8601 strings).

- `presentation/common/utils/` — `Formatters.kt` (all `DateTimeFormatter` constants) and
  `TaskFormatter.kt` (pure task display helpers). Every date/time display site goes through these.

- `presentation/common/animation/SnaptickMotion.kt` — central tween/spring constants (entry
  duration, max staggered items, page transitions). New animations should pull from here, not from
  inline magic numbers.

- `presentation/` — one package per screen. Navigation in
  `presentation/navigation/{AppNavigation,Routes}.kt`. `AppNavigation` collects each screen's VM
  state/events and passes `state + events + onAction` into the composable. The repeat-weekday filter
  no longer lives here — repository owns it.

- `presentation/onboarding/` — first-run flow (`Welcome → ThemePreview → RestoreAndSync`) gated by
  `MainState.onboardingCompleted`. Skips reflexively when the user has already completed it;
  intentionally re-shows on a fresh install or "Clear data".

- `ui/theme/` — `LightColorScheme`, `DarkColorScheme`, `AmoledDarkColorScheme`. Widget reuses these
  via `ColorProviders` so themes match. Default theme on first install is **Amoled** (matches the
  splash screen default — see `SplashThemeMirror`).

- `util/` — `SettingsStore` (DataStore Preferences), `BackupManager` (Gson JSON export/import via
  SAF; strict (non-lenient) `JsonReader` + per-string size cap on restore), `NotificationHelper`,
  `LocaleHelper` (15+ locales), `AudioUtil`, `ReminderScheduler` (`@Singleton`, AlarmManager
  `setExactAndAllowWhileIdle` with `setAndAllowWhileIdle` fallback), `SplashThemeMirror` (writes the
  chosen theme into a fast-readable file the splash screen consults), `Constants` (`SECONDS_IN_DAY`,
  `MIN_ALLOWED_DURATION`, `EMAIL`, etc.).

- `worker/` — `RescheduleAllRemindersWorker` (boot/upgrade/time-change rearm),
  `RescheduleSingleReminderWorker` (per-task next-fire arm after a reminder fires).

- `receiver/` — `SystemEventReceiver` (BOOT_COMPLETED / LOCKED_BOOT_COMPLETED /
  MY_PACKAGE_REPLACED / TIME_CHANGED / TIMEZONE_CHANGED → `RescheduleAllRemindersWorker`),
  `ReminderReceiver` (AlarmManager fire → `NotificationHelper.showNotification` + enqueue
  `RescheduleSingleReminderWorker` for the next occurrence).

### Reminders & repeats

- `Task.shouldOccurOn(date)` is the canonical "does this task occur on this date" check.
- Repeating tasks have a single template row with `isRepeated = 1` + `repeatWeekdays`. The
  template's `date` is the creation date (lower bound for "active from"); it is never rolled
  forward. Today's view virtualizes occurrences at query time.
- Per-occurrence completion lives in `task_completions(uuid, date)` (added in `MIGRATION_3_4`).
  One-off tasks still use `Task.isCompleted`.
- Reminder is armed for the **next** occurrence only. After the alarm fires, `ReminderReceiver`
  enqueues `RescheduleSingleReminderWorker` to arm the following occurrence. Active alarm count is
  O(active tasks), well under AlarmManager's per-app cap.
- Marking a repeating task complete-for-today via `markCompletedForDate(uuid, today)` cancels then
  re-schedules the reminder with `skipToday = true`, so an alarm queued for later today won't fire
  after the user has already checked it off.
- `nextFireMillis` walks `0..6` days forward from `now` looking for the next weekday match. Pass
  `from = LocalDateTime.of(today, LocalTime.MAX)` to push the search past today.

### Widget (Glance)

Separate subtree under `widget/` with its own DI module (`widget/di/WidgetModule.kt`), state,
actions, and worker. Keep it isolated from `presentation/` to avoid pulling Compose-UI deps into the
widget runtime.

- `TaskAppWidget` (`GlanceAppWidget`) — renders `widget/presentation/SnaptickTaskWidget.kt` inside a
  theme wrapper that mirrors app theme + dynamic colors (Android 12+).
- `WidgetStateDefinition` — custom `GlanceStateDefinition<WidgetState>` persisting `tasks`,
  `is24HourFormat`, `theme`, `useDynamicTheme`.
- `WidgetUpdateWorker` (Hilt worker) — single source that (1) reads today's incomplete tasks from
  `TaskRepository`, (2) reads settings from `SettingsStore`, (3) writes `WidgetState`, (4) calls
  `TaskAppWidget().updateAll()`. Two modes: one-time (on CRUD) and periodic daily at midnight (on
  widget enable). Debug `Log.d` calls are gated behind `BuildConfig.DEBUG`.
- `WidgetReceiver` enables/disables the periodic worker on widget lifecycle.
- Actions `RefreshWidgetAction`, `ToggleTaskAction` run in Glance action context — they use
  `TaskDatabase.getInstance()` directly, not Hilt.
- Taps launch `MainActivity` with `EXTRA_NAVIGATE_TO` → `AppNavigation` uses it as
  `startDestination` (e.g., open `AddTaskScreen` directly from widget). The accepted route allowlist
  is enforced server-side in `AppNavigation` so a hostile broadcaster can't drop the app onto an
  unintended screen.

### App startup

`SnaptickApplication` (`@HiltAndroidApp`, `Configuration.Provider`):

1. Initializes ACRA (crash reports emailed to `Constants.EMAIL` when configured; refuses to init
   when no destination is set, so contributor builds never silently mail crashes to a hardcoded
   address).
2. Provides `HiltWorkerFactory` to WorkManager.
3. Enqueues a one-shot `RescheduleAllRemindersWorker` on every cold start as a safety net for
   AlarmManager state loss (force-stop, "Clear data" of the system Settings provider).
   Reboot/upgrade/time-change rearm is handled by `SystemEventReceiver`.

`MainActivity`:

1. Reads splash theme via `SplashThemeMirror` so the splash matches the user's chosen theme.
2. Collects `MainState` via `collectAsStateWithLifecycle()` and gates onboarding on
   `mainState.onboardingCompleted`.
3. Hosts SAF launchers for backup/restore/ICS import.

Do **not** rely on the default WorkManager initializer — it's disabled via `tools:node="remove"` on
`InitializationProvider` in the manifest. Hilt-injected workers need `HiltWorkerFactory`, so new
workers must be `@HiltWorker` + `@AssistedInject`.

### Build config notes

- KSP for Room, `kapt` for Hilt. Don't mix.
- Room schemas exported to `app/schemas/` (set via `ksp { arg("room.schemaLocation", …) }`) — commit
  new schema JSON when bumping DB version and add a `Migration`. Required reading for migrations:
  `data/local/Migration.kt` (per-version comments explain the column-rename pattern, calendar sync
  column add, and `task_completions` table add).
- Compose compiler `1.5.11` is pinned against Kotlin `1.9.23`; bumping Kotlin requires bumping
  `kotlinCompilerExtensionVersion` in lockstep.
- `buildConfig = true` is required for `BuildConfig.DEBUG` gating in `WidgetUpdateWorker`.
- `androidx.hilt:hilt-navigation-compose` is required so `AppNavigation` can wire per-screen VMs via
  `hiltViewModel()`.
- `jitpack.io` repo is enabled (for `WheelPickerCompose`).
  `repositoriesMode = FAIL_ON_PROJECT_REPOS`, so any new repo must be added in
  `settings.gradle.kts`.
- Release build is signed manually, not via CI. Needs `keystore.properties` (gitignored) present
  locally, or falls back to debug-signing for sideloaded checks only. There is no release GitHub
  Actions workflow; releases are built and uploaded to GitHub Releases by hand.

### Backup format

`BackupData(version, tasks, completions)`:

- `version: Int` — current `BACKUP_VERSION = 1`. Restore rejects any other value with a clear
  toast (forward-compat hatch).
- `tasks: List<Task>` — all rows. Restore filters out tasks with title/uuid/repeatWeekdays > 4 KiB,
  or unparseable dates/times, dropping with a count surfaced in the preview dialog.
- `completions: List<BackupCompletion>` — `task_completions` rows preserved across backup/restore so
  per-occurrence history survives.

Restore is two-stage:

1. `MainAction.PreviewBackup(uri)` parses + validates + stages onto `MainState.pendingRestore`. NO
   db writes.
2. `MainAction.ConfirmRestore` calls `repository.restoreFromBackup(pending.data)` which wraps wipe +
   insert in `database.withTransaction` so partial restore on failure rolls back.

## Translations

Strings in `app/src/main/res/values-*/strings.xml`. Crowdin config at `crowdin.yml`. Don't edit
translated files by hand — changes flow through Crowdin.

## Project-wide rules

- No em-dash in code, comments, commits, or user-visible strings (project rule, hard).
- No inline fully-qualified paths — always add an import and use the short name.
- Single-line tagged commit messages: `feat:` / `fix:` / `chore:` / `doc:` / `test:` / `misc:`. No
  Co-Authored-By trailer.
- Magic time numbers go in `Constants` (e.g. `SECONDS_IN_DAY`).
- Default to writing no comments. Only add one when the *why* is non-obvious (constraint, invariant,
  workaround for a specific bug). Never narrate what the code does.
