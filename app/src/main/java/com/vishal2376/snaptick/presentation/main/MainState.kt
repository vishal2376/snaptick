package com.vishal2376.snaptick.presentation.main

import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.SortTask
import java.time.LocalTime
import java.util.Locale

data class MainState(
	val buildVersion: String = "0.0",
	val theme: AppTheme = AppTheme.Dark,
	val sortBy: SortTask = SortTask.BY_START_TIME_ASCENDING,
	var totalTaskDuration: Long = 0,
	val durationList: List<Long> = listOf(30, 60, 90, 0),
	val streak: Int = -1,
	val sleepTime: LocalTime = LocalTime.of(23, 59),
	val language: String = Locale.getDefault().language,
	val isWheelTimePicker: Boolean = true,
)