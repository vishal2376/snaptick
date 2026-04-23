package com.vishal2376.snaptick.presentation.task_list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.task_list.action.TaskListAction
import com.vishal2376.snaptick.presentation.task_list.events.TaskListEvent
import com.vishal2376.snaptick.util.TaskReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
	private val repository: TaskRepository,
	private val reminderScheduler: TaskReminderScheduler,
) : ViewModel() {

	val todayTasks: Flow<List<Task>> = repository.getTodayTasks()
	val allTasks: Flow<List<Task>> = repository.getAllTasks()

	private val _events = MutableSharedFlow<TaskListEvent>(extraBufferCapacity = 1)
	val events = _events.asSharedFlow()

	private var deletedTask: Task? = null

	fun onAction(action: TaskListAction) {
		when (action) {
			is TaskListAction.ToggleCompletion -> toggleCompletion(action.taskId, action.isCompleted)
			is TaskListAction.SwipeTask -> {
				deletedTask = action.task
				deleteTask(action.task)
			}
			is TaskListAction.DeleteTask -> viewModelScope.launch(Dispatchers.IO) {
				repository.getTaskById(action.taskId)?.let {
					deletedTask = it
					reminderScheduler.cancel(it.uuid)
					repository.deleteTask(it)
				}
			}
			is TaskListAction.UndoDelete -> viewModelScope.launch(Dispatchers.IO) {
				deletedTask?.let { task ->
					repository.insertTask(task)
					reminderScheduler.schedule(task)
				}
			}
		}
	}

	private fun deleteTask(task: Task) {
		viewModelScope.launch(Dispatchers.IO) {
			reminderScheduler.cancel(task.uuid)
			repository.deleteTask(task)
		}
	}

	private fun toggleCompletion(taskId: Int, isCompleted: Boolean) {
		viewModelScope.launch(Dispatchers.IO) {
			val task = repository.getTaskById(taskId) ?: return@launch
			val updated = task.copy(isCompleted = isCompleted)
			repository.updateTask(updated)
			if (isCompleted) reminderScheduler.cancel(updated.uuid)
			else reminderScheduler.schedule(updated)
		}
	}
}
