package com.vishal2376.snaptick.presentation.main

import com.vishal2376.snaptick.ui.theme.AppTheme
import com.vishal2376.snaptick.util.SortTask

data class MainState(
	val theme: AppTheme = AppTheme.Dark, // todo: load theme using data store
	val sortBy: SortTask = SortTask.BY_PRIORITY_DESCENDING
)