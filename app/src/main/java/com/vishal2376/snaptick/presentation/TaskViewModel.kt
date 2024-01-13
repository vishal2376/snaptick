package com.vishal2376.snaptick.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.add_edit_screen.AddEditScreenEvent
import com.vishal2376.snaptick.presentation.home_screen.HomeScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

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

	// Home Screen Events
	fun onEvent(event: HomeScreenEvent) {
		when (event) {
			is HomeScreenEvent.OnCompleted -> {
				viewModelScope.launch {
					task = repository.getTaskById(event.taskId)
					task = task.copy(isCompleted = true)
					repository.updateTask(task)
				}
			}
		}
	}

	// Add Edit Screen Events
	fun onEvent(event: AddEditScreenEvent) {
		when (event) {
			is AddEditScreenEvent.OnAddTaskClick -> {
				viewModelScope.launch {
					repository.insertTask(event.task)
				}
			}

			is AddEditScreenEvent.OnDeleteTaskClick -> TODO()
			is AddEditScreenEvent.OnUpdateTitle -> {
				task = task.copy(title = event.title)
			}

			is AddEditScreenEvent.OnUpdateEndTime -> TODO()
			is AddEditScreenEvent.OnUpdatePriority -> TODO()
			is AddEditScreenEvent.OnUpdateReminder -> TODO()
			is AddEditScreenEvent.OnUpdateStartTime -> TODO()
			is AddEditScreenEvent.OnUpdateTask -> TODO()
		}
	}

	fun insertTask(task: Task) {
		viewModelScope.launch {
			repository.insertTask(task)
		}
	}

	fun deleteTask(task: Task) {
		viewModelScope.launch {
			repository.deleteTask(task)
		}
	}

	fun getTaskById(id: Int): Task {
		viewModelScope.launch {
			task = repository.getTaskById(id)
		}
		return task
	}

	fun updateTask(task: Task) {
		viewModelScope.launch {
			repository.updateTask(task)
		}
	}

	fun updateTitle(title: String) {
		task = task.copy(title = title)
	}

	fun updateStartTime(time: LocalTime) {
		task = task.copy(startTime = time)
	}

	fun updateIsCompleted(isCompleted: Boolean) {
		task = task.copy(isCompleted = isCompleted)
	}

	fun updateReminder(isReminderOn: Boolean) {
		task = task.copy(reminder = isReminderOn)
	}

	fun updatePriority(priority: Int) {
		task = task.copy(priority = priority)
	}

	fun updateCategory(category: String) {
		task = task.copy(category = category)
	}

	fun updateEndTime(time: LocalTime) {
		task = task.copy(endTime = time)
	}

}