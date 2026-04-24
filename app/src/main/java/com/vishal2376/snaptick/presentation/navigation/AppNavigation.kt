package com.vishal2376.snaptick.presentation.navigation

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.vishal2376.snaptick.presentation.add_edit_screen.viewmodel.AddEditViewModel
import com.vishal2376.snaptick.presentation.calender_screen.CalenderScreen
import com.vishal2376.snaptick.presentation.common.NotificationPermissionHandler
import com.vishal2376.snaptick.presentation.completed_task_screen.CompletedTaskScreen
import com.vishal2376.snaptick.presentation.free_time_screen.FreeTimeScreen
import com.vishal2376.snaptick.presentation.home_screen.HomeScreen
import com.vishal2376.snaptick.presentation.main.events.MainEvent
import com.vishal2376.snaptick.presentation.onboarding.OnboardingScreen
import com.vishal2376.snaptick.presentation.main.viewmodel.MainViewModel
import com.vishal2376.snaptick.presentation.pomodoro_screen.PomodoroScreen
import com.vishal2376.snaptick.presentation.pomodoro_screen.viewmodel.PomodoroViewModel
import com.vishal2376.snaptick.presentation.settings.SettingsScreen
import com.vishal2376.snaptick.presentation.task_list.viewmodel.TaskListViewModel
import com.vishal2376.snaptick.presentation.this_week_task_screen.ThisWeekTaskScreen
import com.vishal2376.snaptick.util.openMail
import com.vishal2376.snaptick.util.showToast
import java.time.LocalDate

@Composable
fun AppNavigation(
	mainViewModel: MainViewModel,
	startDestination: String? = null
) {
	val navController = rememberNavController()
	val mainState by mainViewModel.state.collectAsStateWithLifecycle()

	val activity = LocalContext.current as MainActivity

	LaunchedEffect(Unit) {
		mainViewModel.events.collect { event ->
			when (event) {
				is MainEvent.ShowToast -> showToast(activity, event.message, Toast.LENGTH_SHORT)
				is MainEvent.OpenMail -> openMail(activity, event.subject)
				is MainEvent.CalendarSyncComplete ->
					showToast(activity, "Calendar sync complete", Toast.LENGTH_SHORT)
				is MainEvent.ImportComplete ->
					showToast(activity, "Imported ${event.count} tasks", Toast.LENGTH_SHORT)
				is MainEvent.ImportFailed ->
					showToast(activity, event.message, Toast.LENGTH_LONG)
				is MainEvent.CalendarPermissionRequired ->
					activity.calendarPermissionLauncher.launch(
						arrayOf(
							android.Manifest.permission.READ_CALENDAR,
							android.Manifest.permission.WRITE_CALENDAR
						)
					)
			}
		}
	}

	NotificationPermissionHandler(
		onPermissionGranted = {},
		onPermissionDenied = {
			showToast(activity, "Notification Disabled")
		}
	)

	val actualStartDestination = when {
		startDestination != null -> startDestination
		!mainState.onboardingCompleted -> Routes.Onboarding.name
		else -> Routes.HomeScreen.name
	}

	NavHost(
		navController = navController,
		startDestination = actualStartDestination,
		enterTransition = {
			slideInHorizontally(
				initialOffsetX = { it / 3 },
				animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow)
			) + fadeIn(animationSpec = tween(durationMillis = 220))
		},
		exitTransition = {
			fadeOut(animationSpec = tween(durationMillis = 180))
		},
		popEnterTransition = {
			fadeIn(animationSpec = tween(durationMillis = 220))
		},
		popExitTransition = {
			slideOutHorizontally(
				targetOffsetX = { it / 3 },
				animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow)
			) + fadeOut(animationSpec = tween(durationMillis = 180))
		}
	) {
		composable(route = Routes.HomeScreen.name) {
			val taskListViewModel: TaskListViewModel = hiltViewModel()
			val todayTasks by taskListViewModel.todayTasks.collectAsStateWithLifecycle(initialValue = emptyList())
			val dayOfWeek = LocalDate.now().dayOfWeek.value - 1
			val updatedTodayTasks = todayTasks.filter { task ->
				if (task.isRepeated) task.getRepeatWeekList().contains(dayOfWeek) else true
			}
			HomeScreen(
				tasks = updatedTodayTasks,
				appState = mainState,
				onAction = mainViewModel::onAction,
				onTaskAction = taskListViewModel::onAction,
				onNavigate = { route -> navController.navigate(route = route) },
				onBackupData = {
					activity.backupPickerLauncher.launch(activity.backupManager.getBackupFilePickerIntent())
				},
				onRestoreData = {
					activity.restorePickerLauncher.launch(activity.backupManager.getLoadBackupFilePickerIntent())
				}
			)
		}

		composable(route = Routes.CompletedTaskScreen.name) {
			val taskListViewModel: TaskListViewModel = hiltViewModel()
			val todayTasks by taskListViewModel.todayTasks.collectAsStateWithLifecycle(initialValue = emptyList())
			val dayOfWeek = LocalDate.now().dayOfWeek.value - 1
			val updatedTodayTasks = todayTasks.filter { task ->
				if (task.isRepeated) task.getRepeatWeekList().contains(dayOfWeek) else true
			}
			CompletedTaskScreen(
				tasks = updatedTodayTasks,
				appState = mainState,
				onTaskAction = taskListViewModel::onAction,
				onBack = {
					if (navController.isValidBackStack) {
						navController.popBackStack()
					}
				})
		}

		composable(route = Routes.ThisWeekTaskScreen.name) {
			val taskListViewModel: TaskListViewModel = hiltViewModel()
			val allTasks by taskListViewModel.allTasks.collectAsStateWithLifecycle(initialValue = emptyList())
			ThisWeekTaskScreen(
				tasks = allTasks,
				appState = mainState,
				onEditTask = { id ->
					navController.navigate(route = "${Routes.EditTaskScreen.name}/$id")
				},
				onBack = {
					if (navController.isValidBackStack) {
						navController.popBackStack()
					}
				})
		}

		composable(route = Routes.CalenderScreen.name) {
			val taskListViewModel: TaskListViewModel = hiltViewModel()
			val allTasks by taskListViewModel.allTasks.collectAsStateWithLifecycle(initialValue = emptyList())
			CalenderScreen(
				tasks = allTasks,
				appState = mainState,
				onTaskAction = taskListViewModel::onAction,
				onAction = mainViewModel::onAction,
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
			val taskListViewModel: TaskListViewModel = hiltViewModel()
			val todayTasks by taskListViewModel.todayTasks.collectAsStateWithLifecycle(initialValue = emptyList())
			val dayOfWeek = LocalDate.now().dayOfWeek.value - 1
			val updatedTodayTasks = todayTasks.filter { task ->
				if (task.isRepeated) task.getRepeatWeekList().contains(dayOfWeek) else true
			}
			FreeTimeScreen(
				tasks = updatedTodayTasks,
				appState = mainState,
				onBack = {
					if (navController.isValidBackStack) {
						navController.popBackStack()
					}
				})
		}

		composable(route = Routes.AddTaskScreen.name) {
			val addEditViewModel: AddEditViewModel = hiltViewModel()
			val addEditState by addEditViewModel.state.collectAsStateWithLifecycle()
			AddTaskScreen(
				state = addEditState,
				events = addEditViewModel.events,
				appState = mainState,
				onAction = addEditViewModel::onAction,
				onMainAction = mainViewModel::onAction,
				onBack = {
					if (navController.previousBackStackEntry != null) {
						navController.popBackStack()
					} else {
						navController.navigate(Routes.HomeScreen.name) {
							popUpTo(0) { inclusive = true }
						}
					}
				})
		}

		composable(
			route = "${Routes.EditTaskScreen.name}/{id}",
			arguments = listOf(navArgument("id") {
				type = NavType.IntType
				defaultValue = -1
			})
		) {
			val addEditViewModel: AddEditViewModel = hiltViewModel()
			val addEditState by addEditViewModel.state.collectAsStateWithLifecycle()
			EditTaskScreen(
				state = addEditState,
				events = addEditViewModel.events,
				appState = mainState,
				onAction = addEditViewModel::onAction,
				onBack = {
					if (navController.isValidBackStack) {
						navController.popBackStack()
					}
				})
		}

		composable(
			route = "${Routes.PomodoroScreen.name}/{id}",
			arguments = listOf(navArgument("id") {
				type = NavType.IntType
				defaultValue = -1
			})
		) {
			val pomodoroViewModel: PomodoroViewModel = hiltViewModel()
			val pomodoroState by pomodoroViewModel.state.collectAsStateWithLifecycle()
			PomodoroScreen(
				state = pomodoroState,
				events = pomodoroViewModel.events,
				onAction = pomodoroViewModel::onAction,
				onBack = {
					if (navController.isValidBackStack) {
						navController.popBackStack()
					}
				})
		}

		composable(route = Routes.SettingsScreen.name,
			enterTransition = {
				slideIntoContainer(
					animationSpec = tween(300, easing = EaseOut),
					towards = AnimatedContentTransitionScope.SlideDirection.Up
				)
			}) {
			SettingsScreen(
				appState = mainState,
				onAction = mainViewModel::onAction,
				onClickAbout = {
					navController.navigate(route = Routes.AboutScreen.name)
				},
				onBack = {
					if (navController.isValidBackStack) {
						navController.popBackStack()
					}
				},
				mainViewModel = mainViewModel,
			)
		}

		composable(route = Routes.AboutScreen.name) {
			AboutScreen(
				appState = mainState,
				onBack = {
					if (navController.isValidBackStack) {
						navController.popBackStack()
					}
				}
			)
		}

		composable(route = Routes.Onboarding.name) {
			OnboardingScreen(
				state = mainState,
				onAction = mainViewModel::onAction,
				onRestoreBackup = {
					activity.restorePickerLauncher.launch(activity.backupManager.getLoadBackupFilePickerIntent())
				},
				onToggleCalendarSync = { enabled ->
					mainViewModel.onAction(
						com.vishal2376.snaptick.presentation.main.action.MainAction
							.SetCalendarSyncEnabled(enabled)
					)
				},
				onFinish = {
					mainViewModel.onAction(
						com.vishal2376.snaptick.presentation.main.action.MainAction.CompleteOnboarding
					)
					navController.navigate(Routes.HomeScreen.name) {
						popUpTo(Routes.Onboarding.name) { inclusive = true }
					}
				},
			)
		}
	}
}

val NavHostController.isValidBackStack
	get() = this.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED
