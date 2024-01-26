package com.vishal2376.snaptick.presentation.main

import com.vishal2376.snaptick.util.SortTask

sealed class MainEvent {
	data class ToggleAmoledTheme(val isEnabled: Boolean) : MainEvent()
	data class UpdateSortByTask(val sortTask: SortTask) : MainEvent()
	data class UpdateFreeTime(val freeTime: Long) : MainEvent()
}