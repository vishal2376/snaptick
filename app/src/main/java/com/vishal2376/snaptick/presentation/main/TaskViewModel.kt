package com.vishal2376.snaptick.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.add_edit_screen.AddEditScreenEvent
import com.vishal2376.snaptick.presentation.home_screen.HomeScreenEvent
import com.vishal2376.snaptick.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

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
				appState = if (event.isEnabled) {
					appState.copy(theme = AppTheme.Amoled)
				} else {
					appState.copy(theme = AppTheme.Dark)
				}
			}

			is MainEvent.UpdateSortByTask -> {
				appState = appState.copy(sortBy = event.sortTask)
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
		}
	}

	// Add/Edit Screen Events
	fun onEvent(event: AddEditScreenEvent) {
		when (event) {
			is AddEditScreenEvent.OnAddTaskClick -> {
				viewModelScope.launch(Dispatchers.IO) {
					repository.insertTask(event.task)
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

	fun getTaskById(id: Int) {
		viewModelScope.launch(Dispatchers.IO) {
			task = repository.getTaskById(id)
		}
	}


}