package com.vishal2376.snaptick.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import kotlinx.coroutines.launch
import javax.inject.Inject

class TaskViewmodel @Inject constructor(private val repository: TaskRepository) : ViewModel() {

	var task: Task by mutableStateOf(
		Task(id = 0, title = "", isCompleted = false, startTime = 0, endTime = 0)
	)
		private set

	var tasks = repository.getAllTasks()
	private val deletedTask: Task? = null

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

	fun getTaskById(id: Int) {
		viewModelScope.launch {
			repository.getTaskById(id)
		}
	}

	fun undoDeletedTask() {
		deletedTask?.let {
			viewModelScope.launch {
				repository.insertTask(it)
			}
		}
	}

}