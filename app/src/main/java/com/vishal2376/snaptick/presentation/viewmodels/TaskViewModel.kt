package com.vishal2376.snaptick.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.home_screen.HomeScreenEvent
import com.vishal2376.snaptick.presentation.pomodoro_screen.PomodoroScreenEvent
import com.vishal2376.snaptick.util.TaskReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
	private val repository: TaskRepository,
	private val reminderScheduler: TaskReminderScheduler,
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
						reminderScheduler.schedule(deletedTask!!)
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
			reminderScheduler.cancel(task.uuid)
			repository.deleteTask(task)
		}
	}

	private fun toggleTaskCompletion(taskId: Int, isCompleted: Boolean) {
		viewModelScope.launch(Dispatchers.IO) {
			repository.getTaskById(taskId)?.let { task = it }
			task = task.copy(isCompleted = isCompleted)
			repository.updateTask(task)
			if (isCompleted) {
				reminderScheduler.cancel(task.uuid)
			} else {
				reminderScheduler.schedule(task)
			}
		}
	}
}
