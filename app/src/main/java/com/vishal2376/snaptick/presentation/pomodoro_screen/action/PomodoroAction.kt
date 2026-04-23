package com.vishal2376.snaptick.presentation.pomodoro_screen.action

sealed interface PomodoroAction {
	data object TogglePause : PomodoroAction
	data object Reset : PomodoroAction
	data object MarkCompleted : PomodoroAction
}
