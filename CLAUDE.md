# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Snaptick — offline Android task planner (Kotlin + Jetpack Compose). Single-module Gradle project (`:app`), `minSdk=26`, `compileSdk=34`.

## Common commands

Run from repo root using the Gradle wrapper.

- Build debug APK: `./gradlew assembleDebug`
- Build release APK: `./gradlew assembleRelease` (signed with debug keystore per `app/build.gradle.kts`)
- Install debug on connected device: `./gradlew installDebug`
- Unit tests: `./gradlew test` (single variant: `./gradlew testDebugUnitTest`)
- Single unit test class: `./gradlew :app:testDebugUnitTest --tests "com.vishal2376.snaptick.ExampleUnitTest"`
- Instrumented tests: `./gradlew connectedDebugAndroidTest`
- Lint: `./gradlew lint` (reports in `app/build/reports/lint-results-*.html`)
- Clean: `./gradlew clean`

Debug build has `applicationIdSuffix=.debug`, so debug + release can coexist on one device.

The project's Gradle + AGP (8.2) targets JDK 17. If the system default is JDK 19+, invoke Gradle with `JAVA_HOME=/path/to/jdk17 ./gradlew …`.

## Architecture

MVI per screen. Each screen has its own Hilt ViewModel that exposes an immutable `StateFlow<…State>`, a `SharedFlow<…Event>` for one-off side effects (toasts, navigation triggers), and accepts user intents via `onAction(action: …Action)`. Composables are stateless and receive `state + events + onAction` from `AppNavigation` (which wires each VM via `hiltViewModel()`). Hilt wires everything; Room is the sole source of truth.

### ViewModels (one per feature, not one per screen)

- `presentation/main/viewmodel/MainViewModel.kt` — app-wide state (theme, language, sleep time, streak, sort, swipe, 24h format, first-time opened, build version). Owns backup/restore flow via `BackupManager`. Emits `MainEvent.ShowToast` / `OpenMail`. Handles `MainAction.*`.
- `presentation/task_list/viewmodel/TaskListViewModel.kt` — shared across `home_screen`, `completed_task_screen`, `this_week_task_screen`, `calender_screen`, `free_time_screen`. Exposes `todayTasks` + `allTasks` as `Flow<List<Task>>` directly from the repo. Handles `TaskListAction` (toggle completion, swipe, delete, undo). Each screen gets its own VM instance per nav back-stack entry; state is consistent because it streams from the DB.
- `presentation/add_edit_screen/viewmodel/AddEditViewModel.kt` — form state + persistence for Add and Edit. Reads `id` nav arg via `SavedStateHandle`; if > 0 loads existing task, else starts blank. `AddEditState` is a full form-state data class. `AddEditAction` covers field updates + `SaveTask`/`UpdateTask`/`DeleteTask`. Emits `TaskSaved`/`TaskUpdated`/`TaskDeleted` events which the screen uses to pop the backstack.
- `presentation/pomodoro_screen/viewmodel/PomodoroViewModel.kt` — timer state (timeLeft, isPaused, isCompleted). Ticker runs in `viewModelScope.launch { while (true) { delay(1s); decrement } }`. On `onCleared()` persists remaining time to `task.pomodoroTimer` via `GlobalScope.launch(Dispatchers.IO)` (viewModelScope is already cancelled). Emits `ResumingPreviousSession`, `TimerCompleted`, `TaskMarkedCompleted`.

No god `TaskViewModel` — the old monolithic one has been split and deleted. Per-screen folders each contain `state/`, `action/`, `events/`, `viewmodel/`, `components/`.

### Layers

- `data/local/` — Room `TaskDatabase` (v2, table `task_table`), `TaskDao`, `MIGRATION_1_2`. DB has dual access: Hilt-provided singleton in `di/AppModule.kt` **and** `TaskDatabase.getInstance(context)` for widget Glance actions that run outside DI scope. Both must stay in sync (same name `local_db`, same migrations) or widget writes will diverge from app writes.
- `data/repositories/TaskRepository.kt` — wraps DAO. Every mutating call (`insert/update/delete/deleteAll`) calls `WidgetUpdateWorker.enqueueWorker(context)` directly so the widget reflects DB changes. `getAllTasks()` also triggers widget sync on each Flow emission.
- `domain/model/` — `Task` entity (Room @Entity + domain model share one class), `BackupData`. `Task.repeatWeekdays` is a comma-separated string of weekday indices (Mon=0…Sun=6) parsed via `getRepeatWeekList()`. Display formatting is **not** on `Task` — use `presentation/common/utils/TaskFormatter.kt` (`formatTaskTime`, `formatDuration`, `formatDurationTimestamp`, `formatWeekDays`).
- `presentation/common/utils/` — `Formatters.kt` (all `DateTimeFormatter` constants) and `TaskFormatter.kt` (pure task display helpers). Every date/time display site goes through these.
- `presentation/` — one package per screen. Navigation in `presentation/navigation/{AppNavigation,Routes}.kt`. `AppNavigation` collects each screen's VM state/events and passes `state + events + onAction` into the composable.
- `ui/theme/` — `LightColorScheme`, `DarkColorScheme`, `AmoledDarkColorScheme`. Widget reuses these via `ColorProviders` so themes match.
- `util/` — `SettingsStore` (DataStore Preferences), `BackupManager` (Gson JSON export/import via SAF; errors surfaced via `Log.e`), `NotificationHelper`, `LocaleHelper` (15+ locales), `AudioUtil`, `TaskReminderScheduler` (`@Singleton`, schedules/cancels per-task `NotificationWorker`).
- `worker/` — `RepeatTaskWorker` (daily at midnight; re-creates repeated tasks for today), `NotificationWorker` (per-task reminder).

### Widget (Glance)

Separate subtree under `widget/` with its own DI module (`widget/di/WidgetModule.kt`), state, actions, and worker. Keep it isolated from `presentation/` to avoid pulling Compose-UI deps into the widget runtime.

- `TaskAppWidget` (`GlanceAppWidget`) — renders `widget/presentation/SnaptickTaskWidget.kt` inside a theme wrapper that mirrors app theme + dynamic colors (Android 12+).
- `WidgetStateDefinition` — custom `GlanceStateDefinition<WidgetState>` persisting `tasks`, `is24HourFormat`, `theme`, `useDynamicTheme`.
- `WidgetUpdateWorker` (Hilt worker) — single source that (1) reads today's incomplete tasks from `TaskRepository`, (2) reads settings from `SettingsStore`, (3) writes `WidgetState`, (4) calls `TaskAppWidget().updateAll()`. Two modes: one-time (on CRUD) and periodic daily at midnight (on widget enable). Debug `Log.d` calls are gated behind `BuildConfig.DEBUG`.
- `WidgetReceiver` enables/disables the periodic worker on widget lifecycle.
- Actions `RefreshWidgetAction`, `ToggleTaskAction` run in Glance action context — they use `TaskDatabase.getInstance()` directly, not Hilt.
- Taps launch `MainActivity` with `EXTRA_NAVIGATE_TO` → `AppNavigation` uses it as `startDestination` (e.g., open `AddTaskScreen` directly from widget).

### App startup

`SnaptickApplication` (`@HiltAndroidApp`, `Configuration.Provider`):
1. Initializes ACRA (crash reports emailed to `Constants.EMAIL`).
2. Provides `HiltWorkerFactory` to WorkManager.
3. Schedules `RepeatTaskWorker` as a unique periodic job (`"Repeat-Tasks"`, `KEEP`) with initial delay until midnight.

Do **not** rely on the default WorkManager initializer — it's disabled via `tools:node="remove"` on `InitializationProvider` in the manifest. Hilt-injected workers need `HiltWorkerFactory`, so new workers must be `@HiltWorker` + `@AssistedInject`.

### Build config notes

- KSP for Room, `kapt` for Hilt. Don't mix.
- Room schemas exported to `app/schemas/` (set via `ksp { arg("room.schemaLocation", …) }`) — commit new schema JSON when bumping DB version and add a `Migration`.
- Compose compiler `1.5.11` is pinned against Kotlin `1.9.23`; bumping Kotlin requires bumping `kotlinCompilerExtensionVersion` in lockstep.
- `buildConfig = true` is required for `BuildConfig.DEBUG` gating in `WidgetUpdateWorker`.
- `androidx.hilt:hilt-navigation-compose` is required so `AppNavigation` can wire per-screen VMs via `hiltViewModel()`.
- `jitpack.io` repo is enabled (for `WheelPickerCompose`). `repositoriesMode = FAIL_ON_PROJECT_REPOS`, so any new repo must be added in `settings.gradle.kts`.

## Translations

Strings in `app/src/main/res/values-*/strings.xml`. Crowdin config at `crowdin.yml`. Don't edit translated files by hand — changes flow through Crowdin.
