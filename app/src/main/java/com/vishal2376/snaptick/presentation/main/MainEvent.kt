package com.vishal2376.snaptick.presentation.main

import com.vishal2376.snaptick.ui.theme.AppTheme

sealed class MainEvent {
	data class ChangeTheme(val theme: AppTheme) : MainEvent()
}