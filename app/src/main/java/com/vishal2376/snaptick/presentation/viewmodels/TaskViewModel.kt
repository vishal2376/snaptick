package com.vishal2376.snaptick.presentation.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.add_edit_screen.AddEditScreenEvent
import com.vishal2376.snaptick.presentation.home_screen.HomeScreenEvent
import com.vishal2376.snaptick.presentation.main.MainEvent
import com.vishal2376.snaptick.presentation.main.MainState
import com.vishal2376.snaptick.ui.theme.AppTheme
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.NavDrawerItem
import com.vishal2376.snaptick.util.PreferenceManager
import com.vishal2376.snaptick.util.SortTask
import com.vishal2376.snaptick.util.getDateDifference
import com.vishal2376.snaptick.util.openMail
import com.vishal2376.snaptick.util.openUrl
import com.vishal2376.snaptick.util.shareApp
import com.vishal2376.snaptick.worker.NotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

	var appState by mutableStateOf(MainState())
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
			date = LocalDate.now(),
			priority = 0
		)
	)
		private set

	//	var taskList = repository.getAllTasks()
	var todayTaskList = repository.getTasksByDate(LocalDate.now())

	// Main App Events
	fun onEvent(event: MainEvent) {
		when (event) {
			is MainEvent.ToggleAmoledTheme -> {
				viewModelScope.launch {
					appState = if (event.isEnabled) {
						appState.copy(theme = AppTheme.Amoled)
					} else {
						appState.copy(theme = AppTheme.Dark)
					}

					PreferenceManager.savePreferences(
						event.context,
						Constants.THEME_KEY,
						appState.theme.ordinal
					)
				}
			}

			is MainEvent.UpdateSortByTask -> {
				viewModelScope.launch {
					PreferenceManager.savePreferences(
						event.context,
						Constants.SORT_TASK_KEY,
						event.sortTask.ordinal
					)
					appState = appState.copy(sortBy = event.sortTask)
				}
			}

			is MainEvent.UpdateFreeTime -> {
				appState = appState.copy(freeTime = event.freeTime)
			}

			is MainEvent.OnClickNavDrawerItem -> {
				when (event.item) {
					NavDrawerItem.REPORT_BUGS -> {
						openMail(event.context, event.context.getString(R.string.report_bug))
					}

					NavDrawerItem.SUGGESTIONS -> {
						openMail(event.context, event.context.getString(R.string.suggestions))
					}

					NavDrawerItem.RATE_US -> {
						val appUrl = Constants.PLAY_STORE_BASE_URL + event.context.packageName
						openUrl(event.context, appUrl)
					}

					NavDrawerItem.SHARE_APP -> {
						shareApp(event.context)
					}
				}
			}
		}
	}

	// Home Screen Events
	fun onEvent(event: HomeScreenEvent) {
		when (event) {
			is HomeScreenEvent.OnCompleted -> {
				viewModelScope.launch(Dispatchers.IO) {
					task = repository.getTaskById(event.taskId)
					task = task.copy(isCompleted = event.isCompleted)
					repository.updateTask(task)
				}
			}

			is HomeScreenEvent.OnEditTask -> {
				getTaskById(event.taskId)
			}

			is HomeScreenEvent.OnPomodoro -> {
				getTaskById(event.taskId)
			}
		}
	}

	// Add/Edit Screen Events
	fun onEvent(event: AddEditScreenEvent) {
		when (event) {
			is AddEditScreenEvent.OnAddTaskClick -> {
				viewModelScope.launch(Dispatchers.IO) {
					repository.insertTask(event.task)
				}
				if (event.task.reminder) {
					scheduleNotification(event.task)
				}
			}

			is AddEditScreenEvent.OnDeleteTaskClick -> {
				viewModelScope.launch(Dispatchers.IO) {
					cancelNotification(event.task.uuid)
					repository.deleteTask(event.task)
				}
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

	private fun getTaskById(id: Int) {
		viewModelScope.launch(Dispatchers.IO) {
			task = repository.getTaskById(id)
		}
	}

	private fun scheduleNotification(task: Task) {
		val data = Data.Builder().putString(Constants.TASK_UUID, task.uuid)
			.putString(Constants.TASK_TITLE, task.title)
			.putString(Constants.TASK_TIME, task.getFormattedTime())
			.build()

		val startTimeSec = task.startTime.toSecondOfDay()
		val currentTimeSec = LocalTime.now().toSecondOfDay()
		val delaySec = startTimeSec - currentTimeSec

		if (delaySec > 0) {

			cancelNotification(task.uuid)

			val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
				.setInputData(data)
				.setInitialDelay(delaySec.toLong(), TimeUnit.SECONDS)
				.addTag(task.uuid)
				.build()

			// Enqueue the work request with WorkManager
			WorkManager.getInstance().enqueue(workRequest)
		}
	}

	private fun cancelNotification(taskUUID: String) {
		WorkManager.getInstance().cancelAllWorkByTag(taskUUID)
	}

	fun loadAppState(context: Context) {
		viewModelScope.launch {
			PreferenceManager.loadPreference(context, Constants.THEME_KEY, defaultValue = 1)
				.collect {
					appState = appState.copy(theme = AppTheme.entries[it])
				}
		}

		viewModelScope.launch {
			PreferenceManager.loadPreference(context, Constants.STREAK_KEY, defaultValue = 0)
				.collect {
					appState = appState.copy(streak = it)
				}
		}

		viewModelScope.launch {
			PreferenceManager.loadStringPreference(
				context,
				Constants.LAST_OPENED_KEY,
				defaultValue = LocalDate.now().toString()
			).collect { lastDate ->

				if (lastDate != LocalDate.now().toString()) {
					val day = getDateDifference(lastDate)
					var newStreak = 0

					if (day == 1) newStreak = appState.streak + 1
					PreferenceManager.savePreferences(context, Constants.STREAK_KEY, newStreak)

					PreferenceManager.saveStringPreferences(
						context,
						Constants.LAST_OPENED_KEY,
						LocalDate.now().toString()
					)
				}

			}
		}

		viewModelScope.launch {
			PreferenceManager.loadPreference(
				context,
				Constants.SORT_TASK_KEY,
				defaultValue = appState.sortBy.ordinal
			).collect {
				appState = appState.copy(sortBy = SortTask.entries[it])
			}
		}

		//load build version
		val buildVersionCode =
			context.packageManager.getPackageInfo(context.packageName, 0).versionName
		appState = appState.copy(buildVersion = buildVersionCode)
	}

}