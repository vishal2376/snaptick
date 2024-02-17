package com.vishal2376.snaptick.presentation.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.vishal2376.snaptick.util.WorkManagerHelper
import com.vishal2376.snaptick.util.WorkManagerHelper.scheduleNotification
import com.vishal2376.snaptick.util.getDateDifference
import com.vishal2376.snaptick.util.openMail
import com.vishal2376.snaptick.util.openUrl
import com.vishal2376.snaptick.util.shareApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
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
					if (event.isCompleted) {
						WorkManagerHelper.cancelNotification(task.uuid)
					} else {
						if (task.reminder && (task.startTime > LocalTime.now())) {
							WorkManagerHelper.scheduleNotification(task)
						}
					}
				}
			}

			is HomeScreenEvent.OnEditTask -> {
				getTaskById(event.taskId)
			}

			is HomeScreenEvent.OnPomodoro -> {
				getTaskById(event.taskId)
			}

			is HomeScreenEvent.OnSwipeTask -> {
				deleteTask(event.task)
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
				if (event.task.isRepeated) {
					WorkManagerHelper.scheduleRepeatTask(event.task)
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

			is AddEditScreenEvent.OnUpdateTask -> {
				viewModelScope.launch(Dispatchers.IO) {
					repository.updateTask(task)
					if (task.reminder && !task.isCompleted) {
						WorkManagerHelper.scheduleNotification(task)
					} else {
						WorkManagerHelper.cancelNotification(task.uuid)
					}
					if (task.isRepeated) WorkManagerHelper.scheduleRepeatTask(task)
					else WorkManagerHelper.cancelRepeatTask(task.uuid)

				}
			}

		}
	}

	private fun getTaskById(id: Int) {
		viewModelScope.launch(Dispatchers.IO) {
			task = repository.getTaskById(id)
		}
	}

	private fun deleteTask(task: Task) {
		viewModelScope.launch(Dispatchers.IO) {
			WorkManagerHelper.cancelNotification(task.uuid)
			WorkManagerHelper.cancelRepeatTask(task.uuid)
			repository.deleteTask(task)
		}
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