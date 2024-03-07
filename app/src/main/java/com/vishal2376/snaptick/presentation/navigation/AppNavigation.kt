package com.vishal2376.snaptick.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vishal2376.snaptick.presentation.add_edit_screen.AddTaskScreen
import com.vishal2376.snaptick.presentation.add_edit_screen.EditTaskScreen
import com.vishal2376.snaptick.presentation.completed_task_screen.CompletedTaskScreen
import com.vishal2376.snaptick.presentation.free_time_screen.FreeTimeScreen
import com.vishal2376.snaptick.presentation.home_screen.HomeScreen
import com.vishal2376.snaptick.presentation.pomodoro_screen.PomodoroScreen
import com.vishal2376.snaptick.presentation.this_week_task_screen.ThisWeekTaskScreen
import com.vishal2376.snaptick.presentation.viewmodels.TaskViewModel

@Composable
fun AppNavigation(taskViewModel: TaskViewModel) {
	val navController = rememberNavController()
	val todayTasks by taskViewModel.todayTaskList.collectAsStateWithLifecycle(initialValue = emptyList())
	val allTasks by taskViewModel.taskList.collectAsStateWithLifecycle(initialValue = emptyList())

	NavHost(
		navController = navController,
		startDestination = Routes.HomeScreen.name
	) {
		composable(route = Routes.HomeScreen.name) {
			HomeScreen(
				tasks = todayTasks,
				appState = taskViewModel.appState,
				onMainEvent = taskViewModel::onEvent,
				onEvent = taskViewModel::onEvent,
				onEditTask = { id ->
					navController.navigate(route = "${Routes.EditTaskScreen.name}/$id")
				},
				onAddTask = {
					navController.navigate(route = Routes.AddTaskScreen.name)
				},
				onClickCompletedInfo = {
					navController.navigate(route = Routes.CompletedTaskScreen.name)
				},
				onClickThisWeek = {
					navController.navigate(route = Routes.ThisWeekTaskScreen.name)
				},
				onClickFreeTimeInfo = {
					navController.navigate(route = Routes.FreeTimeScreen.name)
				},
				onPomodoroTask = { id ->
					navController.navigate(route = "${Routes.PomodoroScreen.name}/$id")
				})
		}

		composable(route = Routes.CompletedTaskScreen.name) {
			CompletedTaskScreen(
				tasks = todayTasks,
				onEvent = taskViewModel::onEvent,
				onBack = {
					if (navController.isValidBackStack) {
						navController.popBackStack()
					}
				})
		}

		composable(route = Routes.ThisWeekTaskScreen.name) {
			ThisWeekTaskScreen(
				tasks = allTasks,
				onEditTask = { id ->
					navController.navigate(route = "${Routes.EditTaskScreen.name}/$id")
				},
				onEvent = taskViewModel::onEvent,
				onBack = {
					if (navController.isValidBackStack) {
						navController.popBackStack()
					}
				})
		}

		composable(route = Routes.FreeTimeScreen.name) {
			FreeTimeScreen(
				tasks = todayTasks,
				onBack = {
					if (navController.isValidBackStack) {
						navController.popBackStack()
					}
				})
		}

		composable(route = Routes.AddTaskScreen.name) {
			AddTaskScreen(
				appState = taskViewModel.appState,
				onEvent = taskViewModel::onEvent,
				onBack = {
					if (navController.isValidBackStack) {
						navController.popBackStack()
					}
				})
		}

		composable(
			route = "${Routes.EditTaskScreen.name}/{id}",
			arguments = listOf(navArgument("id") {
				type = NavType.IntType
				defaultValue = -1
			})
		) { navBackStackEntry ->
			navBackStackEntry.arguments?.getInt("id").let { id ->
				EditTaskScreen(task = taskViewModel.task,
					appState = taskViewModel.appState,
					onEvent = taskViewModel::onEvent,
					onBack = {
						if (navController.isValidBackStack) {
							navController.popBackStack()
						}
					})
			}
		}

		composable(
			route = "${Routes.PomodoroScreen.name}/{id}",
			arguments = listOf(navArgument("id") {
				type = NavType.IntType
				defaultValue = -1
			})
		) { navBackStackEntry ->
			navBackStackEntry.arguments?.getInt("id").let { id ->
				PomodoroScreen(task = taskViewModel.task,
					onEvent = taskViewModel::onEvent,
					onBack = {
						if (navController.isValidBackStack) {
							navController.popBackStack()
						}
					})
			}
		}
	}
}

val NavHostController.isValidBackStack
	get() = this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED

