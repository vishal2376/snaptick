package com.vishal2376.snaptick.presentation.pomodoro_screen

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.add_edit_screen.AddEditScreenEvent
import com.vishal2376.snaptick.presentation.home_screen.HomeScreenEvent
import com.vishal2376.snaptick.presentation.main.MainEvent
import com.vishal2376.snaptick.presentation.main.MainState
import com.vishal2376.snaptick.ui.theme.AppTheme
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.PreferenceManager
import com.vishal2376.snaptick.util.SortTask
import com.vishal2376.snaptick.worker.NotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val TAG = "@@@"

@HiltViewModel
class TaskViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

	var appState by mutableStateOf(MainState())

	var task: Task by mutableStateOf(
		Task(
			id = 0,
			title = "",
			isCompleted = false,
			startTime = LocalTime.now(),
			endTime = LocalTime.now(),
			reminder = false,
			category = "",
			priority = 0
		)
	)
		private set

	var taskList = repository.getAllTasks()

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
					repository.deleteTask(task)
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
		val data = Data.Builder().putInt(Constants.TASK_ID, task.id)
			.putString(Constants.TASK_TITLE, task.title)
			.putString(Constants.TASK_TIME, task.getFormattedTime())
			.build()

		val startTimeSec = task.startTime.toSecondOfDay()
		val currentTimeSec = LocalTime.now().toSecondOfDay()
		val delaySec = startTimeSec - currentTimeSec

		if (delaySec > 0) {

			val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
				.setInputData(data)
				.setInitialDelay(delaySec.toLong(), TimeUnit.SECONDS)
				.build()

			// Enqueue the work request with WorkManager
			WorkManager.getInstance().enqueue(workRequest)
			Log.e(TAG, "scheduleNotification: ${task.title}")
		}
	}

	fun loadAppState(context: Context) {
		viewModelScope.launch {
			PreferenceManager.loadPreference(context, Constants.THEME_KEY, defaultValue = 1)
				.collect {
					appState = appState.copy(theme = AppTheme.entries[it])
					Log.e(TAG, "loadAppState: appTheme entry : $it")
				}
		}

		viewModelScope.launch {
			PreferenceManager.loadPreference(
				context,
				Constants.SORT_TASK_KEY,
				defaultValue = appState.sortBy.ordinal
			).collect {
				appState = appState.copy(sortBy = SortTask.entries[it])
				Log.e(TAG, "loadAppState: sortTask entry : $it")
			}
		}
	}

}