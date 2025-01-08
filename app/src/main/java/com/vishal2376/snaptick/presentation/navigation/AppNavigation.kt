package com.vishal2376.snaptick.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vishal2376.snaptick.MainActivity
import com.vishal2376.snaptick.presentation.about_screen.AboutScreen
import com.vishal2376.snaptick.presentation.add_edit_screen.AddTaskScreen
import com.vishal2376.snaptick.presentation.add_edit_screen.EditTaskScreen
import com.vishal2376.snaptick.presentation.calender_screen.CalenderScreen
import com.vishal2376.snaptick.presentation.common.NotificationPermissionHandler
import com.vishal2376.snaptick.presentation.completed_task_screen.CompletedTaskScreen
import com.vishal2376.snaptick.presentation.free_time_screen.FreeTimeScreen
import com.vishal2376.snaptick.presentation.home_screen.HomeScreen
import com.vishal2376.snaptick.presentation.pomodoro_screen.PomodoroScreen
import com.vishal2376.snaptick.presentation.settings.SettingsScreen
import com.vishal2376.snaptick.presentation.this_week_task_screen.ThisWeekTaskScreen
import com.vishal2376.snaptick.presentation.viewmodels.TaskViewModel
import com.vishal2376.snaptick.util.showToast
import java.time.LocalDate

@Composable
fun AppNavigation(taskViewModel: TaskViewModel) {
	val navController = rememberNavController()
	val todayTasks by taskViewModel.todayTaskList.collectAsStateWithLifecycle(initialValue = emptyList())
	val allTasks by taskViewModel.taskList.collectAsStateWithLifecycle(initialValue = emptyList())

	val dayOfWeek = LocalDate.now().dayOfWeek.value - 1
	val updatedTodayTasks = todayTasks.filter { task ->
		if (task.isRepeated) {
			task.getRepeatWeekList().contains(dayOfWeek)
		} else {
			true
		}
	}

	val activity = LocalContext.current as MainActivity

	// runtime notification permission
	NotificationPermissionHandler(
		onPermissionGranted = {
			showToast(activity,"Notification Enabled")
		},
		onPermissionDenied = {
			showToast(activity,"Notification Disabled")
		}
	)

	NavHost(
		navController = navController,
		startDestination = Routes.HomeScreen.name
	) {
		composable(route = Routes.HomeScreen.name) {
			HomeScreen(
				tasks = updatedTodayTasks,
				appState = taskViewModel.appState,
				onMainEvent = taskViewModel::onEvent,
				onEvent = taskViewModel::onEvent,
				onNavigate = { route ->
					navController.navigate(route = route)
				},
				onBackupData = {
					activity.backupPickerLauncher.launch(taskViewModel.backupManager.getBackupFilePickerIntent())
				},
				onRestoreData = {
					activity.restorePickerLauncher.launch(taskViewModel.backupManager.getLoadBackupFilePickerIntent())
				}
			)
		}

		composable(route = Routes.CompletedTaskScreen.name) {
			CompletedTaskScreen(
				tasks = updatedTodayTasks,
				appState = taskViewModel.appState,
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
				appState = taskViewModel.appState,
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

		composable(route = Routes.CalenderScreen.name) {
			CalenderScreen(
				tasks = allTasks,
				appState = taskViewModel.appState,
				onEvent = taskViewModel::onEvent,
				onMainEvent = taskViewModel::onEvent,
				onBack = {
					if (navController.isValidBackStack) {
						navController.popBackStack()
					}
				},
				onNavigate = { route ->
					navController.navigate(route = route)
				})
		}

		composable(route = Routes.FreeTimeScreen.name) {
			FreeTimeScreen(
				tasks = updatedTodayTasks,
				appState = taskViewModel.appState,
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
				onMainEvent = taskViewModel::onEvent,
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

		composable(route = Routes.SettingsScreen.name,
			enterTransition = {
				slideIntoContainer(
					animationSpec = tween(300, easing = EaseOut),
					towards = AnimatedContentTransitionScope.SlideDirection.Up
				)
			}) {
			SettingsScreen(
				appState = taskViewModel.appState,
				onEvent = taskViewModel::onEvent,
				onClickAbout = {
					navController.navigate(route = Routes.AboutScreen.name)
				},
				onBack = {
					if (navController.isValidBackStack) {
						navController.popBackStack()
					}
				}
			)
		}

		composable(route = Routes.AboutScreen.name) {
			AboutScreen(
				appState = taskViewModel.appState,
				onBack = {
					if (navController.isValidBackStack) {
						navController.popBackStack()
					}
				}
			)
		}
	}
}

val NavHostController.isValidBackStack
	get() = this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED

