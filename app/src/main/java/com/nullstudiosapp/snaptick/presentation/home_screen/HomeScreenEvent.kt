package com.nullstudiosapp.snaptick.presentation.home_screen

import com.nullstudiosapp.snaptick.domain.model.Task

sealed class HomeScreenEvent {
	data class OnCompleted(val taskId: Int, val isCompleted: Boolean) : HomeScreenEvent()
	data class OnSwipeTask(val task: Task) : HomeScreenEvent()
	data class OnEditTask(val taskId: Int) : HomeScreenEvent()
	data class OnPomodoro(val taskId: Int) : HomeScreenEvent()
	data class OnDeleteTask(val taskId: Int) : HomeScreenEvent()
	data object OnUndoDelete : HomeScreenEvent()
}