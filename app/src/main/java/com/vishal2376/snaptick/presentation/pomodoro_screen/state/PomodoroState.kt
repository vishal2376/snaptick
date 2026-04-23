package com.vishal2376.snaptick.presentation.pomodoro_screen.state

data class PomodoroState(
	val taskId: Int = -1,
	val taskTitle: String = "",
	val totalTime: Long = 0L,
	val timeLeft: Long = 0L,
	val isPaused: Boolean = false,
	val isReset: Boolean = false,
	val isCompleted: Boolean = false
)
