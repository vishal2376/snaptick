package com.vishal2376.snaptick.presentation.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.BackupData
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.add_edit_screen.AddEditScreenEvent
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.CalenderView
import com.vishal2376.snaptick.presentation.common.NavDrawerItem
import com.vishal2376.snaptick.presentation.common.SortTask
import com.vishal2376.snaptick.presentation.home_screen.HomeScreenEvent
import com.vishal2376.snaptick.presentation.main.MainEvent
import com.vishal2376.snaptick.presentation.main.MainState
import com.vishal2376.snaptick.presentation.pomodoro_screen.PomodoroScreenEvent
import com.vishal2376.snaptick.util.BackupManager
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.SettingsStore
import com.vishal2376.snaptick.util.openMail
import com.vishal2376.snaptick.util.showToast
import com.vishal2376.snaptick.util.updateLocale
import com.vishal2376.snaptick.worker.NotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
	private val repository: TaskRepository,
	val backupManager: BackupManager
) : ViewModel() {

	var appState by mutableStateOf(MainState())
	private var deletedTask: Task? = null
	var task: Task by mutableStateOf(
		Task(
			id = 0,
			uuid = "",
			title = "",
			isCompleted = false,
			startTime = LocalTime.now(),
			endTime = LocalTime.now(),
			reminder = false,
			isRepeated = false,
			repeatWeekdays = "",
			pomodoroTimer = -1,
			date = LocalDate.now(),
			priority = 0
		)
	)
		private set

	var taskList = repository.getAllTasks()
	var todayTaskList = repository.getTodayTasks()
	val backupData = mutableStateOf(BackupData())

	init {
		viewModelScope.launch {
			repository.getAllTasks().collect {
				backupData.value = BackupData(it)
			}
		}
	}

	// Main App Events
	fun onEvent(event: MainEvent) {
		when (event) {
			is MainEvent.UpdateAppTheme -> {
				viewModelScope.launch {
					appState = appState.copy(theme = event.theme)
					SettingsStore(event.context).setTheme(event.theme.ordinal)
				}
			}

			is MainEvent.UpdateTimePicker -> {
				viewModelScope.launch {
					appState = appState.copy(isWheelTimePicker = event.isWheelTimePicker)
					SettingsStore(event.context).setTimePicker(event.isWheelTimePicker)
				}
			}

			is MainEvent.UpdateTimeFormat -> {
				viewModelScope.launch {
					appState = appState.copy(is24hourTimeFormat = event.isTimeFormat)
					SettingsStore(event.context).setTimeFormat(event.isTimeFormat)
				}
			}

			is MainEvent.UpdateSleepTime -> {
				viewModelScope.launch {
					appState = appState.copy(sleepTime = event.sleepTime)
					SettingsStore(event.context).setSleepTime(event.sleepTime.toString())
				}
			}

			is MainEvent.UpdateLanguage -> {
				viewModelScope.launch {
					appState = appState.copy(language = event.language)
					updateLocale(event.context, event.language)
					SettingsStore(event.context).setLanguage(event.language)
				}
			}

			is MainEvent.UpdateSortByTask -> {
				viewModelScope.launch {
					appState = appState.copy(sortBy = event.sortTask)
					SettingsStore(event.context).setSortTask(event.sortTask.ordinal)
				}
			}

			is MainEvent.UpdateCalenderView -> {
				viewModelScope.launch {
					appState = appState.copy(calenderView = event.calenderView)
					SettingsStore(event.context).setCalenderView(event.calenderView.ordinal)
				}
			}

			is MainEvent.UpdateCalenderDate -> {
				appState = appState.copy(calenderDate = event.date)
			}

			is MainEvent.OnClickNavDrawerItem -> {
				when (event.item) {
					NavDrawerItem.REPORT_BUGS -> {
						openMail(event.context, event.context.getString(R.string.report_bug))
					}

					NavDrawerItem.SUGGESTIONS -> {
						openMail(event.context, event.context.getString(R.string.suggestions))
					}
				}
			}
		}
	}

	// Home Screen Events
	fun onEvent(event: HomeScreenEvent) {
		when (event) {
			is HomeScreenEvent.OnCompleted -> {
				toggleTaskCompletion(event.taskId, event.isCompleted)
			}

			is HomeScreenEvent.OnEditTask -> {
				getTaskById(event.taskId)
			}

			is HomeScreenEvent.OnPomodoro -> {
				getTaskById(event.taskId)
			}

			is HomeScreenEvent.OnDeleteTask -> {
				viewModelScope.launch {
					task = repository.getTaskById(event.taskId)
					deletedTask = task
					repository.deleteTask(task)
				}
			}

			is HomeScreenEvent.OnSwipeTask -> {
				deletedTask = event.task
				deleteTask(event.task)
			}

			is HomeScreenEvent.OnUndoDelete -> {
				viewModelScope.launch(Dispatchers.IO) {
					if (deletedTask != null) {
						repository.insertTask(deletedTask!!)
						scheduleNotification(deletedTask!!)
					}
				}
			}
		}
	}

	// Add/Edit Screen Events
	fun onEvent(event: AddEditScreenEvent) {
		when (event) {
			is AddEditScreenEvent.OnAddTaskClick -> {
				viewModelScope.launch(Dispatchers.IO) {
					repository.insertTask(event.task)
					scheduleNotification(event.task)
				}
			}

			is AddEditScreenEvent.OnDeleteTaskClick -> {
				deleteTask(event.task)
			}

			is AddEditScreenEvent.OnUpdateTitle -> {
				task = task.copy(title = event.title)
			}

			is AddEditScreenEvent.OnUpdateStartTime -> {
				task = task.copy(startTime = event.time)
			}

			is AddEditScreenEvent.OnUpdateEndTime -> {
				task = task.copy(endTime = event.time)
			}

			is AddEditScreenEvent.OnUpdatePriority -> {
				task = task.copy(priority = event.priority.ordinal)
			}

			is AddEditScreenEvent.OnUpdateReminder -> {
				task = task.copy(reminder = event.reminder)
			}

			is AddEditScreenEvent.ResetPomodoroTimer -> {
				task = task.copy(pomodoroTimer = -1)
			}

			is AddEditScreenEvent.OnUpdateIsRepeated -> {
				task = task.copy(isRepeated = event.isRepeated)
			}

			is AddEditScreenEvent.OnUpdateRepeatWeekDays -> {
				task = task.copy(repeatWeekdays = event.weekDays)
			}

			is AddEditScreenEvent.OnUpdateTask -> {
				viewModelScope.launch(Dispatchers.IO) {
					repository.updateTask(task)
					if (task.reminder && !task.isCompleted) {
						scheduleNotification(task)
					} else {
						cancelNotification(task.uuid)
					}
				}
			}

		}
	}

	// Pomodoro Screen Events
	fun onEvent(event: PomodoroScreenEvent) {
		when (event) {
			is PomodoroScreenEvent.OnCompleted -> {
				toggleTaskCompletion(event.taskId, event.isCompleted)
			}

			is PomodoroScreenEvent.OnDestroyScreen -> {
				viewModelScope.launch {
					task = repository.getTaskById(event.taskId)
					task = if (task.isValidPomodoroSession(event.remainingTime))
						task.copy(pomodoroTimer = event.remainingTime.toInt())
					else
						task.copy(pomodoroTimer = -1)
					repository.updateTask(task)
				}
			}
		}
	}

	private fun getTaskById(id: Int) {
		viewModelScope.launch(Dispatchers.IO) {
			task = repository.getTaskById(id)
		}
	}

	fun createBackup(uri: Uri, backupData: BackupData, context: Context) {
		viewModelScope.launch {
			val success = backupManager.createBackup(uri, backupData)
			if (success) {
				showToast(context, "Backup created successfully")
			} else {
				showToast(context, "Failed to create backup")
			}
		}
	}

	fun loadBackup(uri: Uri, context: Context) {
		viewModelScope.launch {
			val backupData = backupManager.loadBackup(uri)
			if (backupData == null) {
				showToast(context, "Failed to restore backup")
			} else {
				repository.deleteAllTasks()
				for (task in backupData.tasks) {
					repository.insertTask(task)
				}
				showToast(context, "Backup Restored")
			}
		}
	}

	private fun deleteTask(task: Task) {
		viewModelScope.launch(Dispatchers.IO) {
			deletedTask = task
			cancelNotification(task.uuid)
			repository.deleteTask(task)
		}
	}

	private fun toggleTaskCompletion(taskId: Int, isCompleted: Boolean) {
		viewModelScope.launch(Dispatchers.IO) {
			task = repository.getTaskById(taskId)
			task = task.copy(isCompleted = isCompleted)
			repository.updateTask(task)
			if (isCompleted) {
				cancelNotification(task.uuid)
			} else {
				scheduleNotification(task)
			}
		}
	}

	private fun scheduleNotification(task: Task) {
		if (task.reminder && !task.isCompleted) {

			// cancel older notification
			cancelNotification(task.uuid)

			//calculate delay
			val startDateTimeSec =
				LocalDateTime.of(task.date, task.startTime).toEpochSecond(ZoneOffset.UTC)
			val currentDateTimeSec = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
			val delaySec = startDateTimeSec - currentDateTimeSec

			if (delaySec > 0) {
				val data = Data.Builder().putString(Constants.TASK_UUID, task.uuid)
					.putString(Constants.TASK_TITLE, task.title)
					.putString(Constants.TASK_TIME, task.getFormattedTime())
					.build()

				// new notification request
				val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
					.setInitialDelay(delaySec, TimeUnit.SECONDS)
					.setInputData(data)
					.addTag(task.uuid)
					.build()
				WorkManager.getInstance().enqueue(workRequest)
			}
		}
	}

	private fun cancelNotification(taskUUID: String) {
		WorkManager.getInstance().cancelAllWorkByTag(taskUUID)
	}

	fun loadAppState(context: Context) {
		val settingsStore = SettingsStore(context)

		viewModelScope.launch {
			settingsStore.themeKey.collect {
				appState = appState.copy(theme = AppTheme.entries[it])
			}
		}

		viewModelScope.launch {
			settingsStore.timePickerKey.collect {
				appState = appState.copy(isWheelTimePicker = it)
			}
		}

		viewModelScope.launch {
			settingsStore.streakKey.collect {
				appState = appState.copy(streak = it)
			}
		}

		viewModelScope.launch {
			settingsStore.sleepTimeKey.collect {
				appState = appState.copy(sleepTime = LocalTime.parse(it))
			}
		}

		viewModelScope.launch {
			settingsStore.languageKey.collect { language ->
				appState = appState.copy(language = language)
			}
		}

		viewModelScope.launch {
			settingsStore.calenderViewKey.collect {
				appState = appState.copy(calenderView = CalenderView.entries[it])
			}
		}

		viewModelScope.launch {
			settingsStore.timeFormatKey.collect {
				appState = appState.copy(is24hourTimeFormat = it)
			}
		}

		viewModelScope.launch {
			settingsStore.sortTaskKey.collect {
				appState = appState.copy(sortBy = SortTask.entries[it])
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
						val newStreak = if (isYesterday) appState.streak + 1 else 0
						settingsStore.setStreak(newStreak)
						settingsStore.setLastOpened(LocalDate.now().toString())
					}
				}
			}
		}

		//load build version
		val buildVersionCode =
			context.packageManager.getPackageInfo(context.packageName, 0).versionName
		appState = appState.copy(buildVersion = buildVersionCode)
	}

}