package com.vishal2376.snaptick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.vishal2376.snaptick.presentation.main.TaskViewModel
import com.vishal2376.snaptick.presentation.navigation.AppNavigation
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	private val taskViewModel by viewModels<TaskViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			SnaptickTheme(theme = taskViewModel.appState.theme) {
				AppNavigation(taskViewModel = taskViewModel)
			}
		}
	}
}