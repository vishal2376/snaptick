package com.vishal2376.snaptick.presentation.task_list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.task_list.action.TaskListAction
import com.vishal2376.snaptick.presentation.task_list.events.TaskListEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * Owns the today/all task lists and the toggle/delete/undo intents emitted
 * from the home, calendar, this-week, completed, and free-time screens.
 *
 * Reminder scheduling is handled inside [TaskRepository] now; this VM no
 * longer talks to the scheduler directly. For repeating tasks, completion
 * writes a (uuid, today) row to `task_completions` instead of mutating the
 * template's `isCompleted` flag, so the same template stays armed for
 * future occurrences.
 */
@HiltViewModel
class TaskListViewModel @Inject constructor(
	private val repository: TaskRepository,
) : ViewModel() {

	val todayTasks: Flow<List<Task>> = repository.getTodayTasksWithCompletions()
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
			is TaskListAction.DeleteTask -> viewModelScope.launch {
				repository.getTaskById(action.taskId)?.let {
					deletedTask = it
					repository.deleteTask(it)
				}
			}
			is TaskListAction.UndoDelete -> viewModelScope.launch {
				deletedTask?.let { task -> repository.insertTask(task) }
			}
		}
	}

	private fun deleteTask(task: Task) {
		viewModelScope.launch { repository.deleteTask(task) }
	}

	private fun toggleCompletion(taskId: Int, isCompleted: Boolean) {
		viewModelScope.launch {
			val task = repository.getTaskById(taskId) ?: return@launch
			if (task.isRepeated) {
				val today = LocalDate.now()
				if (isCompleted) repository.markCompletedForDate(task.uuid, today)
				else repository.unmarkCompletedForDate(task.uuid, today)
			} else {
				repository.updateTask(task.copy(isCompleted = isCompleted))
			}
		}
	}
}
