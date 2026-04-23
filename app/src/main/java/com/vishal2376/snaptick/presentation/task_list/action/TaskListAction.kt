package com.vishal2376.snaptick.presentation.task_list.action

import com.vishal2376.snaptick.domain.model.Task

sealed interface TaskListAction {
	data class ToggleCompletion(val taskId: Int, val isCompleted: Boolean) : TaskListAction
	data class SwipeTask(val task: Task) : TaskListAction
	data class DeleteTask(val taskId: Int) : TaskListAction
	data object UndoDelete : TaskListAction
}
