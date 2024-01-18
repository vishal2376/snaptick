package com.vishal2376.snaptick.presentation.main

sealed class MainEvent {
	data class AmoledTheme(val isEnabled: Boolean) : MainEvent()
}