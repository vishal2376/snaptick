package com.vishal2376.snaptick.presentation.home_screen

sealed class HomeScreenEvent {
	data class OnCompleted(val taskId: Int, val isCompleted: Boolean) : HomeScreenEvent()
	data class OnEditTask(val taskId: Int) : HomeScreenEvent()
	data class OnPomodoro(val taskId: Int) : HomeScreenEvent()
}