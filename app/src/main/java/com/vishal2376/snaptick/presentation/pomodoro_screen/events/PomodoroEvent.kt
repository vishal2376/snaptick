package com.vishal2376.snaptick.presentation.pomodoro_screen.events

sealed interface PomodoroEvent {
	data object ResumingPreviousSession : PomodoroEvent
	data object TimerCompleted : PomodoroEvent
	data object TaskMarkedCompleted : PomodoroEvent
}
