package com.vishal2376.snaptick.presentation.main

import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.CalenderView
import com.vishal2376.snaptick.presentation.common.SortTask
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

data class MainState(
	val buildVersion: String = "0.0",
	val theme: AppTheme = AppTheme.Dark,
	val dynamicTheme: Boolean = false,
	val sortBy: SortTask = SortTask.BY_START_TIME_ASCENDING,
	var totalTaskDuration: Long = 0,
	val durationList: List<Long> = listOf(30, 60, 90, 0),
	val streak: Int = -1,
	val sleepTime: LocalTime = LocalTime.of(23, 59),
	val language: String = Locale.ENGLISH.language,
	val isWheelTimePicker: Boolean = true,
	val is24hourTimeFormat: Boolean = false,
	val calenderView: CalenderView = CalenderView.MONTHLY,
	val calenderDate: LocalDate? = null
)