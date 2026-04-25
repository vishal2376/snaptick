package com.vishal2376.snaptick.presentation.main.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.data.calendar.CalendarImporter
import com.vishal2376.snaptick.data.calendar.CalendarInfo
import com.vishal2376.snaptick.data.calendar.CalendarRepository
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.BackupData
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.CalenderView
import com.vishal2376.snaptick.presentation.common.NavDrawerItem
import com.vishal2376.snaptick.presentation.common.SortTask
import com.vishal2376.snaptick.presentation.common.SwipeBehavior
import com.vishal2376.snaptick.presentation.main.action.MainAction
import com.vishal2376.snaptick.presentation.main.events.MainEvent
import com.vishal2376.snaptick.presentation.main.state.MainState
import com.vishal2376.snaptick.presentation.main.state.PendingRestore
import com.vishal2376.snaptick.util.BackupManager
import com.vishal2376.snaptick.util.SettingsStore
import com.vishal2376.snaptick.util.SplashThemeMirror
import com.vishal2376.snaptick.util.updateLocale
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

private const val MAX_ICS_BYTES: Long = 8L * 1024 * 1024  // 8 MiB
private const val MAX_BACKUP_TASKS = 10_000

@HiltViewModel
class MainViewModel @Inject constructor(
	@ApplicationContext private val context: Context,
	private val settingsStore: SettingsStore,
	private val backupManager: BackupManager,
	private val repository: TaskRepository,
	private val calendarRepository: CalendarRepository,
	private val calendarImporter: CalendarImporter,
) : ViewModel() {

	private val _state = MutableStateFlow(MainState())
	val state: StateFlow<MainState> = _state.asStateFlow()

	private val _events = MutableSharedFlow<MainEvent>(extraBufferCapacity = 1)
	val events = _events.asSharedFlow()

	val backupData: StateFlow<BackupData> = repository.getAllTasks()
		.map { BackupData(it) }
		.stateIn(viewModelScope, SharingStarted.Eagerly, BackupData())

	init {
		loadPersistedState()
		loadBuildVersion()
	}

	fun onAction(action: MainAction) {
		when (action) {
			is MainAction.UpdateAppTheme -> persist {
				_state.update { s -> s.copy(theme = action.theme) }
				settingsStore.setTheme(action.theme.ordinal)
				SplashThemeMirror.write(context, action.theme)
			}
			is MainAction.UpdateDynamicTheme -> persist { _state.update { s -> s.copy(dynamicTheme = action.isEnabled) }; settingsStore.setDynamicTheme(action.isEnabled) }
			is MainAction.UpdateTimePicker -> persist { _state.update { s -> s.copy(isWheelTimePicker = action.isWheelTimePicker) }; settingsStore.setTimePicker(action.isWheelTimePicker) }
			is MainAction.UpdateTimeFormat -> persist { _state.update { s -> s.copy(is24hourTimeFormat = action.is24Hour) }; settingsStore.setTimeFormat(action.is24Hour) }
			is MainAction.UpdateSleepTime -> persist { _state.update { s -> s.copy(sleepTime = action.sleepTime) }; settingsStore.setSleepTime(action.sleepTime.toString()) }
			is MainAction.UpdateLanguage -> persist {
				_state.update { s -> s.copy(language = action.language) }
				updateLocale(context, action.language)
				settingsStore.setLanguage(action.language)
			}
			is MainAction.UpdateSortByTask -> persist { _state.update { s -> s.copy(sortBy = action.sortTask) }; settingsStore.setSortTask(action.sortTask.ordinal) }
			is MainAction.UpdateCalenderView -> persist { _state.update { s -> s.copy(calenderView = action.calenderView) }; settingsStore.setCalenderView(action.calenderView.ordinal) }
			is MainAction.UpdateCalenderDate -> _state.update { it.copy(calenderDate = action.date) }
			is MainAction.UpdateShowWhatsNew -> persist { _state.update { s -> s.copy(showWhatsNew = action.show) }; settingsStore.setShowWhatsNew(action.show) }
			is MainAction.UpdateFirstTimeOpened -> _state.update { it.copy(firstTimeOpened = action.isFirstTimeOpened) }
			is MainAction.UpdateBuildVersionCode -> persist { _state.update { s -> s.copy(buildVersionCode = action.versionCode) }; settingsStore.setBuildVersionCode(action.versionCode) }
			is MainAction.UpdateSwipeBehaviour -> persist { _state.update { s -> s.copy(swipeBehaviour = action.swipeBehaviour) }; settingsStore.setSwipeBehaviour(action.swipeBehaviour.ordinal) }
			is MainAction.UpdateTotalTaskDuration -> _state.update { it.copy(totalTaskDuration = action.durationSeconds) }
			is MainAction.OnClickNavDrawerItem -> handleNavDrawerClick(action.item)
			is MainAction.CreateBackup -> createBackup(action.uri, action.backupData)
			is MainAction.PreviewBackup -> previewBackup(action.uri)
			is MainAction.ConfirmRestore -> confirmRestore()
			is MainAction.CancelRestore -> _state.update { it.copy(pendingRestore = null) }
			is MainAction.LoadBackup -> previewBackup(action.uri)
			is MainAction.SetCalendarSyncEnabled -> persist {
				if (action.enabled && !calendarRepository.hasWritePermission()) {
					_events.emit(MainEvent.CalendarPermissionRequired)
					return@persist
				}
				_state.update { s -> s.copy(calendarSyncEnabled = action.enabled) }
				settingsStore.setCalendarSyncEnabled(action.enabled)
			}
			is MainAction.SetCalendarSyncTarget -> persist {
				_state.update { s -> s.copy(calendarSyncCalendarId = action.calendarId) }
				settingsStore.setCalendarSyncCalendarId(action.calendarId)
			}
			is MainAction.ImportTasks -> importTasks(action.tasks)
			is MainAction.ParseIcsFile -> parseIcsFile(action.uri)
			is MainAction.ImportIcsFile -> importIcsFile(action.uri)
			is MainAction.ClearImportPreview -> _state.update { it.copy(importPreview = emptyList()) }
			is MainAction.SyncAllTasksNow -> viewModelScope.launch {
				repository.syncAllTasksNow()
				_events.emit(MainEvent.CalendarSyncComplete(0))
			}
			is MainAction.CompleteOnboarding -> persist {
				_state.update { it.copy(onboardingCompleted = true) }
				settingsStore.setOnboardingCompleted(true)
			}
		}
	}

	private fun parseIcsFile(uri: Uri) {
		viewModelScope.launch {
			if (!isIcsSizeAcceptable(uri)) return@launch
			try {
				val tasks = context.contentResolver.openInputStream(uri)?.use { stream ->
					calendarImporter.previewFromIcs(stream.reader())
				}.orEmpty()
				_state.update { it.copy(importPreview = tasks) }
				if (tasks.isEmpty()) {
					_events.emit(MainEvent.ImportFailed("No events found in file"))
				} else {
					_events.emit(MainEvent.IcsParsedReady(tasks.size))
				}
			} catch (e: Exception) {
				_events.emit(MainEvent.ImportFailed(e.message ?: "Failed to read .ics file"))
			}
		}
	}

	private fun importIcsFile(uri: Uri) {
		viewModelScope.launch {
			if (!isIcsSizeAcceptable(uri)) return@launch
			try {
				val tasks = context.contentResolver.openInputStream(uri)?.use { stream ->
					calendarImporter.previewFromIcs(stream.reader())
				}.orEmpty()
				if (tasks.isEmpty()) {
					_events.emit(MainEvent.ImportFailed("No events found in file"))
					return@launch
				}
				tasks.forEach { repository.insertTask(it) }
				_events.emit(MainEvent.ImportComplete(tasks.size))
			} catch (e: Exception) {
				_events.emit(MainEvent.ImportFailed(e.message ?: "Failed to import .ics file"))
			}
		}
	}

	/**
	 * Pre-flights the picked URI's size before we read it. Files above
	 * [MAX_ICS_BYTES] are rejected outright so a hostile .ics can't OOM the
	 * app process.
	 */
	private suspend fun isIcsSizeAcceptable(uri: Uri): Boolean {
		val sizeBytes = runCatching {
			context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length }
		}.getOrNull() ?: -1L
		if (sizeBytes in 1L..MAX_ICS_BYTES) return true
		if (sizeBytes > MAX_ICS_BYTES) {
			_events.emit(MainEvent.ImportFailed("File too large (max 8 MB)"))
			return false
		}
		// sizeBytes < 0 means the provider didn't expose a length. Allow it; the
		// IcsParser caps event count and per-line length defensively.
		return true
	}

	suspend fun loadWritableCalendars(): List<CalendarInfo> = calendarRepository.getWritableCalendars()

	private fun importTasks(tasks: List<Task>) {
		viewModelScope.launch {
			try {
				tasks.forEach { repository.insertTask(it) }
				_events.emit(MainEvent.ImportComplete(tasks.size))
			} catch (e: Exception) {
				_events.emit(MainEvent.ImportFailed(e.message ?: "Import failed"))
			}
		}
	}

	private fun handleNavDrawerClick(item: NavDrawerItem) {
		val subject = when (item) {
			NavDrawerItem.REPORT_BUGS -> context.getString(R.string.report_bug)
			NavDrawerItem.SUGGESTIONS -> context.getString(R.string.suggestions)
		}
		viewModelScope.launch { _events.emit(MainEvent.OpenMail(subject)) }
	}

	private fun createBackup(uri: Uri, backupData: BackupData) {
		viewModelScope.launch {
			val success = backupManager.createBackup(uri, backupData)
			_events.emit(MainEvent.ShowToast(if (success) "Backup created successfully" else "Failed to create backup"))
		}
	}

	/**
	 * Stage 1 of the two-stage restore. Reads + validates the backup, stages it
	 * onto MainState.pendingRestore, and emits BackupPreviewReady so the UI can
	 * surface a confirmation dialog. NO database mutation here. Stage 2
	 * (`confirmRestore`) is what actually wipes and reinserts.
	 */
	private fun previewBackup(uri: Uri) {
		viewModelScope.launch {
			val data = backupManager.loadBackup(uri)
			if (data == null) {
				_events.emit(MainEvent.ShowToast("Failed to read backup file"))
				return@launch
			}
			if (data.tasks.size > MAX_BACKUP_TASKS) {
				_events.emit(MainEvent.ImportFailed("Backup too large (max $MAX_BACKUP_TASKS tasks)"))
				return@launch
			}
			// Drop tasks whose date/time fields can't round-trip through their
			// Java-time parsers. Gson tolerates malformed strings here, but Room
			// would crash later. Better to drop with a warning than wipe the DB
			// then fail mid-insert.
			val validTasks = data.tasks.filter { task ->
				runCatching {
					task.startTime.toString()
					task.endTime.toString()
					task.date.toString()
				}.isSuccess
			}
			val droppedCount = data.tasks.size - validTasks.size
			val pending = PendingRestore(
				data = data.copy(tasks = validTasks),
				taskCount = validTasks.size,
				droppedCount = droppedCount,
			)
			_state.update { it.copy(pendingRestore = pending) }
			_events.emit(MainEvent.BackupPreviewReady(validTasks.size, droppedCount))
		}
	}

	/**
	 * Stage 2: user confirmed. Wipe the DB and insert the staged tasks. Clears
	 * `pendingRestore` whether the operation succeeds or fails so the dialog
	 * never sticks.
	 */
	private fun confirmRestore() {
		viewModelScope.launch {
			val pending = _state.value.pendingRestore
			if (pending == null) {
				_events.emit(MainEvent.ShowToast("Nothing to restore"))
				return@launch
			}
			try {
				repository.deleteAllTasks()
				for (task in pending.data.tasks) repository.insertTask(task)
				val msg = if (pending.droppedCount > 0)
					"Restored ${pending.taskCount} tasks (${pending.droppedCount} skipped)"
				else
					"Restored ${pending.taskCount} tasks"
				_events.emit(MainEvent.ShowToast(msg))
			} catch (e: Exception) {
				_events.emit(MainEvent.ShowToast("Restore failed: ${e.message ?: "unknown error"}"))
			} finally {
				_state.update { it.copy(pendingRestore = null) }
			}
		}
	}

	private fun persist(block: suspend () -> Unit) {
		viewModelScope.launch { block() }
	}

	private fun loadBuildVersion() {
		val versionName = context.packageManager
			.getPackageInfo(context.packageName, 0).versionName ?: "0.0"
		_state.update { it.copy(buildVersion = versionName) }
	}

	private fun loadPersistedState() {
		viewModelScope.launch { settingsStore.themeKey.collect { ordinal -> _state.update { it.copy(theme = AppTheme.entries[ordinal]) } } }
		viewModelScope.launch { settingsStore.dynamicThemeKey.collect { v -> _state.update { it.copy(dynamicTheme = v) } } }
		viewModelScope.launch { settingsStore.timePickerKey.collect { v -> _state.update { it.copy(isWheelTimePicker = v) } } }
		viewModelScope.launch { settingsStore.streakKey.collect { v -> _state.update { it.copy(streak = v) } } }
		viewModelScope.launch { settingsStore.sleepTimeKey.collect { v -> _state.update { it.copy(sleepTime = LocalTime.parse(v)) } } }
		viewModelScope.launch { settingsStore.languageKey.collect { v -> _state.update { it.copy(language = v) } } }
		viewModelScope.launch { settingsStore.calenderViewKey.collect { v -> _state.update { it.copy(calenderView = CalenderView.entries[v]) } } }
		viewModelScope.launch { settingsStore.timeFormatKey.collect { v -> _state.update { it.copy(is24hourTimeFormat = v) } } }
		viewModelScope.launch { settingsStore.sortTaskKey.collect { v -> _state.update { it.copy(sortBy = SortTask.entries[v]) } } }
		viewModelScope.launch { settingsStore.showWhatsNewKey.collect { v -> _state.update { it.copy(showWhatsNew = v) } } }
		viewModelScope.launch { settingsStore.swipeBehaviourKey.collect { v -> _state.update { it.copy(swipeBehaviour = SwipeBehavior.entries[v]) } } }
		viewModelScope.launch { settingsStore.buildVersionCode.collect { v -> _state.update { it.copy(buildVersionCode = v) } } }
		viewModelScope.launch { settingsStore.calendarSyncEnabledKey.collect { v -> _state.update { it.copy(calendarSyncEnabled = v) } } }
		viewModelScope.launch { settingsStore.calendarSyncCalendarIdKey.collect { v -> _state.update { it.copy(calendarSyncCalendarId = v) } } }
		viewModelScope.launch {
			settingsStore.onboardingCompletedKey.collect { v ->
				_state.update {
					it.copy(
						onboardingCompleted = v,
						bootResolved = true
					)
				}
			}
		}
		viewModelScope.launch {
			settingsStore.lastOpenedKey.collect { lastDateString ->
				if (lastDateString == "") {
					settingsStore.setLastOpened(LocalDate.now().toString())
				} else {
					val lastDate = LocalDate.parse(lastDateString)
					val isToday = lastDate.isEqual(LocalDate.now())
					val isYesterday = lastDate.isEqual(LocalDate.now().minusDays(1))
					if (!isToday) {
						val currentStreak = _state.value.streak
						val newStreak = if (isYesterday) currentStreak + 1 else 0
						settingsStore.setStreak(newStreak)
						settingsStore.setLastOpened(LocalDate.now().toString())
					}
				}
			}
		}
	}
}
