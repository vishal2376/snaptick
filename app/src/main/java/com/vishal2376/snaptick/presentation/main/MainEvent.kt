package com.vishal2376.snaptick.presentation.main

import android.content.Context
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.CalenderView
import com.vishal2376.snaptick.presentation.common.NavDrawerItem
import com.vishal2376.snaptick.presentation.common.SortTask
import java.time.LocalDate
import java.time.LocalTime

sealed class MainEvent {
	data class UpdateAppTheme(val theme: AppTheme, val context: Context) : MainEvent()
	data class UpdateDynamicTheme(val isEnabled: Boolean, val context: Context) : MainEvent()
	data class UpdateTimePicker(val isWheelTimePicker: Boolean, val context: Context) : MainEvent()
	data class UpdateFirstTimeOpened(val isFirstTimeOpened: Boolean) : MainEvent()
	data class UpdateTimeFormat(val isTimeFormat: Boolean, val context: Context) : MainEvent()
	data class UpdateSleepTime(val sleepTime: LocalTime, val context: Context) : MainEvent()
	data class UpdateLanguage(val language: String, val context: Context) : MainEvent()
	data class UpdateSortByTask(val sortTask: SortTask, val context: Context) : MainEvent()
	data class UpdateCalenderDate(val date: LocalDate?) : MainEvent()
	data class UpdateShowWhatsNew(val showWhatsNew: Boolean, val context: Context) : MainEvent()
	data class OnClickNavDrawerItem(val context: Context, val item: NavDrawerItem) : MainEvent()
	data class UpdateBuildVersionCode(val context: Context, val versionCode: Int) : MainEvent()
	data class UpdateCalenderView(val calenderView: CalenderView, val context: Context) :
		MainEvent()
}