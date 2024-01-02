package com.vishal2376.snaptick.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vishal2376.snaptick.presentation.TaskViewModel
import com.vishal2376.snaptick.presentation.add_edit_screen.AddEditScreen
import com.vishal2376.snaptick.presentation.home_screen.HomeScreen

@Composable
fun AppNavigation(taskViewModel: TaskViewModel) {
	val navController = rememberNavController()

	NavHost(
		navController = navController,
		startDestination = Routes.HomeScreen.name
	) {
		composable(route = Routes.HomeScreen.name) {
			HomeScreen(taskViewModel,
				onAddEdit = { id ->
					navController.navigate(route = "${Routes.AddEditScreen.name}/$id")
				})
		}

		composable(
			route = "${Routes.AddEditScreen.name}/{id}",
			arguments = listOf(navArgument("id") {
				type = NavType.IntType
			})
		) { navBackStackEntry ->
			navBackStackEntry.arguments?.getInt("id").let { id ->
				AddEditScreen(
					taskViewModel = taskViewModel,
					onBack = { navController.popBackStack() },
					taskId = id!!
				)
			}
		}
	}
}