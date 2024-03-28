package com.vishal2376.snaptick.presentation.main

import android.content.Context
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.NavDrawerItem
import com.vishal2376.snaptick.presentation.common.SortTask
import java.time.LocalTime

sealed class MainEvent {
	data class UpdateAppTheme(val theme: AppTheme, val context: Context) : MainEvent()
	data class UpdateTimePicker(val isWheelTimePicker: Boolean, val context: Context) : MainEvent()
	data class UpdateSleepTime(val sleepTime: LocalTime, val context: Context) : MainEvent()
	data class UpdateLanguage(val language: String, val context: Context) : MainEvent()
	data class UpdateSortByTask(val sortTask: SortTask, val context: Context) : MainEvent()
	data class OnClickNavDrawerItem(val context: Context, val item: NavDrawerItem) : MainEvent()
}