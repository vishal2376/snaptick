package com.vishal2376.snaptick.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
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
			endTime = LocalTime.now()
		)
	)
		private set

	var taskList = repository.getAllTasks()
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
			task = repository.getTaskById(id)
		}
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

	fun updateEndTime(time: LocalTime) {
		task = task.copy(endTime = time)
	}

}