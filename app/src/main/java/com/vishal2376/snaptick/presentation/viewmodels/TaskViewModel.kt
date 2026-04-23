package com.vishal2376.snaptick.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.add_edit_screen.AddEditScreenEvent
import com.vishal2376.snaptick.presentation.common.utils.formatTaskTime
import com.vishal2376.snaptick.presentation.home_screen.HomeScreenEvent
import com.vishal2376.snaptick.presentation.pomodoro_screen.PomodoroScreenEvent
import com.vishal2376.snaptick.util.Constants
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
) : ViewModel() {

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
					repository.getTaskById(event.taskId)?.let {
						task = it
						deletedTask = task
						repository.deleteTask(task)
					}
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
				viewModelScope.launch(Dispatchers.IO) {
					val fetchedTask = repository.getTaskById(event.taskId)
					if (fetchedTask != null) {
						task = if (fetchedTask.isValidPomodoroSession(event.remainingTime))
							fetchedTask.copy(pomodoroTimer = event.remainingTime.toInt())
						else
							fetchedTask.copy(pomodoroTimer = -1)

						repository.updateTask(task)
					} else {
						println("TaskViewModel: Task with ID ${event.taskId} not found")
					}
				}
			}

		}
	}

	private fun getTaskById(id: Int) {
		viewModelScope.launch(Dispatchers.IO) {
			repository.getTaskById(id)?.let { task = it }
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
			repository.getTaskById(taskId)?.let { task = it }
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
					.putString(Constants.TASK_TIME, formatTaskTime(task))
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
}
