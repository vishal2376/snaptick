package com.vishal2376.snaptick.presentation.main

import android.content.Context
import com.vishal2376.snaptick.util.NavDrawerItem
import com.vishal2376.snaptick.util.SortTask

sealed class MainEvent {
	data class ToggleAmoledTheme(val isEnabled: Boolean, val context: Context) : MainEvent()
	data class UpdateSortByTask(val sortTask: SortTask, val context: Context) : MainEvent()
	data class UpdateFreeTime(val freeTime: Long) : MainEvent()
	data class OnClickNavDrawerItem(val context: Context, val item: NavDrawerItem) : MainEvent()
}