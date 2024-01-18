package com.vishal2376.snaptick.presentation.main

import com.vishal2376.snaptick.ui.theme.AppTheme

data class MainState(
	val theme: AppTheme = AppTheme.Dark // todo: load previous theme using data pref
)