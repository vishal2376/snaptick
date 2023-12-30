package com.vishal2376.snaptick.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vishal2376.snaptick.presentation.TaskViewModel
import com.vishal2376.snaptick.presentation.home_screen.HomeScreen

@Composable
fun AppNavigation(viewmodel: TaskViewModel) {
	val navController = rememberNavController()

	NavHost(navController = navController, startDestination = Routes.HomeScreen.name) {
		composable(route = Routes.HomeScreen.name) {
			HomeScreen(viewmodel,)
		}
	}
}