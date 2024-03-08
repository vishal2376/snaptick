package com.vishal2376.snaptick.presentation.main

import android.content.Context
import com.vishal2376.snaptick.presentation.common.NavDrawerItem
import com.vishal2376.snaptick.presentation.common.SortTask
import com.vishal2376.snaptick.ui.theme.AppTheme

sealed class MainEvent {
	data class UpdateAppTheme(val theme: AppTheme, val context: Context) : MainEvent()
	data class UpdateSortByTask(val sortTask: SortTask, val context: Context) : MainEvent()
	data class UpdateFreeTime(val freeTime: Long) : MainEvent()
	data class OnClickNavDrawerItem(val context: Context, val item: NavDrawerItem) : MainEvent()
}