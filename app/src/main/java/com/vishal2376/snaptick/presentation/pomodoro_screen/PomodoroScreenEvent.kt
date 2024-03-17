package com.vishal2376.snaptick.presentation.pomodoro_screen

sealed class PomodoroScreenEvent {
	data class OnCompleted(val taskId: Int, val isCompleted: Boolean) : PomodoroScreenEvent()
	data class OnDestroyScreen(val taskId: Int, val remainingTime: Long) : PomodoroScreenEvent()
}