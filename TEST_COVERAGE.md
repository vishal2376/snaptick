# Snaptick — Automated Test Coverage

Snapshot of the automated test suite on branch `test/automation-coverage`.

| Layer                 | Suites | Tests  | Status |
|-----------------------|:------:|:------:|:------:|
| JVM unit tests        |   8    |   52   |   ✅    |
| Instrumentation tests |   9    |   21   |   ✅    |
| **Total**             | **17** | **73** | **✅**  |

Run: `JAVA_HOME=<jdk17> ./gradlew :app:testDebugUnitTest :app:connectedDebugAndroidTest`

---

## JVM unit tests (`app/src/test/java/...`)

| # | Suite                   | Target                                    | Cases | Covers                                                                                                                                                                                                                                                                                     |
|---|-------------------------|-------------------------------------------|:-----:|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | `TaskTest`              | `domain.model.Task`                       |   7   | `getDuration` (normal + inverted + zero), `isAllDayTaskEnabled`, `getRepeatWeekList` (empty + parsed), `isValidPomodoroSession` boundary                                                                                                                                                   |
| 2 | `TaskFormatterTest`     | `presentation.common.utils.TaskFormatter` |  10   | `formatTaskTime` 12h/24h, `formatDuration` h+m / exact hour / plural hours / minutes-only, `formatDurationTimestamp` mm:ss / HH:mm:ss, `formatWeekDays` (indices + empty)                                                                                                                  |
| 3 | `ValidationTest`        | `util.checkValidTask`                     |   5   | Empty title, duration < 5min not all-day, all-day bypass, past date rejection, future date short-circuit                                                                                                                                                                                   |
| 4 | `MainViewModelTest`     | `MainViewModel`                           |   5   | `UpdateAppTheme` persist, `UpdateLanguage` persist, `OnClickNavDrawerItem(REPORT_BUGS)` → `OpenMail` event, `CreateBackup` success toast, `LoadBackup` null-failure toast                                                                                                                  |
| 5 | `AddEditViewModelTest`  | `AddEditViewModel`                        |   9   | Blank state when `id=-1`, loads task when `id>0`, `UpdateTitle`, `UpdateAllDay` syncs end time, `UpdateDurationMinutes` + `timeUpdateTick`, `SaveTask` (insert + schedule + event), `UpdateTask` (reminder off → cancel + event), `DeleteTask` (remove + cancel + event), `UpdatePriority` |
| 6 | `PomodoroViewModelTest` | `PomodoroViewModel`                       |   6   | Fresh load, resume previous session emits `ResumingPreviousSession`, ticker decrement via virtual time, `TogglePause` freezes ticker, `Reset` restores + pauses, `MarkCompleted` emits + persists                                                                                          |
| 7 | `TaskListViewModelTest` | `TaskListViewModel`                       |   5   | `ToggleCompletion` on/off (schedule/cancel), `SwipeTask` + `UndoDelete` round-trip, missing id no-op, `UndoDelete` before any delete is safe                                                                                                                                               |
| 8 | `BackupGsonTest`        | `util.BackupManager` Gson adapters        |   4   | `BackupData` round-trip preserves every task field, empty backup round-trips, `LocalDateAdapter` emits ISO_LOCAL_DATE, `LocalTimeAdapter` emits ISO_LOCAL_TIME |

Helpers:
- `util/MainDispatcherRule.kt` — `Dispatchers.Main` → `StandardTestDispatcher` swap.
- `util/TaskRepositoryFake.kt` — `mockk<TaskRepository>(relaxed=true)` with stateful `MutableStateFlow` backing.
- `util/SettingsStoreFake.kt` — `mockk<SettingsStore>` wired to per-key `MutableStateFlow`s and a `saved` map.

---

## Instrumentation tests (`app/src/androidTest/java/...`) — Pixel 9 Pro AVD

| # | Suite                       | Target                                                             | Cases | Covers                                                                                                                                                                     |
|---|-----------------------------|--------------------------------------------------------------------|:-----:|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | `TaskDaoTest`               | `data.local.TaskDao` + in-memory Room                              |   6   | `insertTask` + `getTaskById`, `getTasksByDate` filter, `deleteAllTasks`, `getLastRepeatedTasks` past + repeated filter, `updateTask` persistence, `deleteTask` removes row |
| 2 | `TaskReminderSchedulerTest` | `util.TaskReminderScheduler` + `WorkManagerTestInitHelper`         |   3   | `schedule` enqueues tagged `OneTimeWorkRequest`, `schedule` no-op when `reminder=false`, `cancel` cancels tagged work                                                      |
| 3 | `ToggleTaskActionTest`      | `widget.action.ToggleTaskAction` + real `TaskDatabase.getInstance` |   2   | Toggle flips `isCompleted` (and flips back), missing `TaskIdKey` is no-op                                                                                                  |
| 4 | `HomeScreenTest`            | `presentation.home_screen.HomeScreen` (Compose)                    |   1   | Renders both task titles from `tasks` list                                                                                                                                 |
| 5 | `AddTaskScreenTest`         | `presentation.add_edit_screen.AddTaskScreen` (Compose)             |   2   | Typing into title field dispatches `UpdateTitle`, tapping "Add Task" button dispatches `SaveTask`                                                                          |
| 6 | `PomodoroScreenTest`        | `presentation.pomodoro_screen.PomodoroScreen` (Compose)            |   2   | `timeLeft=1500` renders "25:00", `isCompleted=true` renders "Completed"                                                                                                    |
| 7 | `SettingsScreenTest`        | `presentation.settings.SettingsScreen` (Compose)                   |   2   | About row invokes `onClickAbout` callback, "Settings" title renders                                                                                                        |
| 8 | `WidgetUpdateWorkerTest`    | `widget.worker.WidgetUpdateWorker` + in-memory Room + real `SettingsStore` + `TestListenableWorkerBuilder` with hand-rolled `WorkerFactory` | 1 | Seeds 2 incomplete + 1 completed task for today, runs worker, reads `WidgetStateDefinition` DataStore, asserts only the incomplete titles survive |
| 9 | `RepeatTaskWorkerTest`      | `worker.RepeatTaskWorker` + in-memory Room + `TestListenableWorkerBuilder` | 2 | Repeated task whose `repeatWeekdays` includes today: original row flipped to `isRepeated=false`, new row created for today with `isRepeated=true`. Repeated task with weekdays not including today: no today row created |

---

## Out-of-scope / known gaps

| Area                                                             | Reason                                                                                                                                                                     |
|------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `AppNavigation` end-to-end                                       | Per-screen Compose tests cover the individual flows; a multi-screen nav test is deferred.                                                                                  |
| Glance widget rendering                                          | No public test framework for Glance snapshot/layout. State-write path is verified via `WidgetUpdateWorkerTest`.                                                            |
| `PomodoroViewModel.onCleared` persistence (`GlobalScope.launch`) | Lifecycle only triggers via real `ViewModelStore.clear()`; asserted indirectly via public actions. Would need an injected `CoroutineScope`.                               |
| `MainViewModel.loadPersistedState` streak branch                 | Depends on wall-clock date transitions; would need an injected `Clock`.                                                                                                    |
| Release ProGuard/R8                                              | No release-config verification tests — build check, not a runtime test.                                                                                                    |

### Bug surfaced while writing tests

`MIGRATION_1_2` (`data/local/Migration.kt`) is broken against the v1 schema snapshot (`app/schemas/.../1.json`):

1. v1 schema already has `repeatWeekdays` and `pomodoroTimer` columns. The migration's `ALTER TABLE task_table ADD COLUMN repeatWeekdays` fails with `SQLITE_ERROR: duplicate column name`.
2. v1 column is named `isRepeat`; v2 renamed it to `isRepeated`. The migration does not handle the rename.

In production the failure is masked by `fallbackToDestructiveMigration()` in `di/AppModule.kt` and `data/local/TaskDatabase.kt` — Room silently wipes the DB when the migration throws, so existing users lose data on upgrade. A proper migration + matching `MigrationTest` belongs in a dedicated follow-up fix.

---

## Production-adjacent changes landed while wiring tests

Small cleanups — not test-only hacks:

- Dropped explicit `Dispatchers.IO` from `viewModelScope.launch(...)` in `TaskListViewModel`, `AddEditViewModel`, `PomodoroViewModel`. Room's `suspend` DAO methods already thread-switch internally.
- Refactored `PomodoroViewModel.startTicker()` from a `while(true)` polling loop with `delay(200)` when paused to an `_state.map { … }.collectLatest { … }` observer. Same behaviour, no CPU spin while paused, and the ticker exits cleanly on pause/reset/completion.
- `app/build.gradle.kts`: test deps (`turbine 1.1.0`, `kotlinx-coroutines-test 1.7.3`, `androidx.arch.core:core-testing 2.2.0`, `mockk 1.13.8`, `room-testing 2.6.1`, `work-testing 2.9.0`, `hilt-android-testing 2.49`, `guava 32.0.1-android`), JVM target bumped to 11 (mockk 1.13.8 ships JVM 11 bytecode), `testOptions.unitTests.isReturnDefaultValues = true`.
