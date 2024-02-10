package com.vishal2376.snaptick.presentation.main

import com.vishal2376.snaptick.ui.theme.AppTheme
import com.vishal2376.snaptick.util.SortTask

data class MainState(
	val buildVersion: String = "0.0",
	val theme: AppTheme = AppTheme.Dark,
	val sortBy: SortTask = SortTask.BY_START_TIME_ASCENDING,
	val freeTime: Long? = null,
	var totalTaskDuration: Long = 0,
	val durationList: List<Long> = listOf(30, 60, 90, 0),
	val streak: Int = 0
)