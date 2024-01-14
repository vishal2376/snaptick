package com.vishal2376.snaptick.presentation.pomodoro_screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.vishal2376.snaptick.presentation.pomodoro_screen.components.CustomCircularProgressBar
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

@Composable
fun PomodoroScreen() {
	CustomCircularProgressBar()
}

@Preview
@Composable
fun PomodoroScreenPreview() {
	SnaptickTheme(
		darkTheme = true,
		dynamicColor = false
	) {
		PomodoroScreen()
	}
}