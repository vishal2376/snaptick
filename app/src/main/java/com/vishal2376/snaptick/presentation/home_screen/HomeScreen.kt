package com.vishal2376.snaptick.presentation.home_screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.SnackbarController.showCustomSnackbar
import com.vishal2376.snaptick.presentation.common.SortTask
import com.vishal2376.snaptick.presentation.common.SwipeActionBox
import com.vishal2376.snaptick.presentation.common.durationTextStyle
import com.vishal2376.snaptick.presentation.common.fontRobotoMono
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.home_screen.components.EmptyTaskComponent
import com.vishal2376.snaptick.presentation.home_screen.components.InfoComponent
import com.vishal2376.snaptick.presentation.home_screen.components.NavigationDrawerComponent
import com.vishal2376.snaptick.presentation.home_screen.components.SortTaskDialogComponent
import com.vishal2376.snaptick.presentation.home_screen.components.TaskComponent
import com.vishal2376.snaptick.presentation.home_screen.components.WhatsNewDialogComponent
import com.vishal2376.snaptick.presentation.main.MainEvent
import com.vishal2376.snaptick.presentation.main.MainState
import com.vishal2376.snaptick.presentation.navigation.Routes
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.LightGreen
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.ui.theme.Yellow
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.DummyTasks
import com.vishal2376.snaptick.util.SettingsStore
import com.vishal2376.snaptick.util.SoundEvent
import com.vishal2376.snaptick.util.getFreeTime
import com.vishal2376.snaptick.util.playSound
import com.vishal2376.snaptick.util.updateLocale
import kotlinx.coroutines.launch


@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalFoundationApi::class
)
@Composable
fun HomeScreen(
	tasks: List<Task>,
	appState: MainState,
	onMainEvent: (MainEvent) -> Unit,
	onEvent: (HomeScreenEvent) -> Unit,
	onNavigate: (String) -> Unit,
	onBackupData: () -> Unit,
	onRestoreData: () -> Unit
) {
	val completedTasks = tasks.filter { it.isCompleted }
	val inCompletedTasks = tasks.filter { !it.isCompleted }

	// calc free time
	val totalTaskTime = inCompletedTasks.sumOf { it.getDuration(checkPastTask = true) }
	val freeTimeText = getFreeTime(totalTaskTime, appState.sleepTime)

	// streak
	val appStreakText = if (appState.streak > 0) appState.streak.toString() else "0"
	val context = LocalContext.current
	val packageInfo = context.packageManager.getPackageInfo(LocalContext.current.packageName, 0)

	LaunchedEffect(inCompletedTasks) {
		appState.totalTaskDuration = totalTaskTime
	}

	LaunchedEffect(Unit) {
		val store = SettingsStore(context)
		store.languageKey.collect {
			updateLocale(context, it)
		}
	}

	val totalTasks = tasks.size
	val totalCompletedTasks = completedTasks.size

	// animation
	val translateX = 600f
	val leftTranslate = remember { Animatable(-translateX) }
	val rightTranslate = remember { Animatable(translateX) }

	LaunchedEffect(key1 = Unit) {
		launch {
			leftTranslate.animateTo(
				0f,
				tween(1000)
			)
		}
		launch {
			rightTranslate.animateTo(
				0f,
				tween(1000)
			)
		}
	}

	//sort dialog
	var showSortDialog by remember {
		mutableStateOf(false)
	}

	// navigation drawer
	val scope = rememberCoroutineScope()
	val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

	ModalNavigationDrawer(
		drawerState = drawerState,
		drawerContent = {
			ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.background) {
				NavigationDrawerComponent(
					appState,
					onMainEvent,
					onClickThisWeek = {
						onNavigate(Routes.ThisWeekTaskScreen.name)
						scope.launch {
							drawerState.close()
						}
					},
					onClickSettings = {
						onNavigate(Routes.SettingsScreen.name)
						scope.launch {
							drawerState.close()
						}
					},
					onClickBackup = {
						onBackupData()
					},
					onClickRestore = {
						onRestoreData()
					}
				)
			}
		}) {
		Scaffold(topBar = {
			TopAppBar(
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = Color.Transparent
				),
				title = {
					Text(
						text = stringResource(id = R.string.app_name),
						style = h1TextStyle
					)
				},
				navigationIcon = {
					IconButton(onClick = {
						scope.launch {
							drawerState.apply {
								if (isClosed) open() else close()
							}
						}
					}) {
						Icon(
							imageVector = Icons.Default.Menu,
							contentDescription = null
						)
					}
				},
				actions = {
					Row(
						horizontalArrangement = Arrangement.spacedBy(2.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						IconButton(onClick = { onNavigate(Routes.CalenderScreen.name) }) {
							Icon(
								imageVector = Icons.Default.CalendarMonth,
								contentDescription = null
							)
						}
						Text(
							text = appStreakText,
							style = durationTextStyle,
							color = MaterialTheme.colorScheme.onBackground,
							fontFamily = fontRobotoMono,
							fontWeight = FontWeight.Bold
						)
						Icon(
							painter = painterResource(id = R.drawable.ic_fire),
							contentDescription = null,
							tint = Yellow,
						)
					}
					Spacer(modifier = Modifier.width(8.dp))
				})
		},
			floatingActionButton = {
				FloatingActionButton(
					onClick = {
						onNavigate(Routes.AddTaskScreen.name)
					},
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = MaterialTheme.colorScheme.onPrimary
				) {
					Icon(
						imageVector = Icons.Default.Add,
						contentDescription = null
					)
				}
			}) { innerPadding ->

			// sort dialog
			if (showSortDialog)
				SortTaskDialogComponent(
					defaultSortTask = appState.sortBy,
					onClose = { showSortDialog = false },
					onSelect = {
						onMainEvent(MainEvent.UpdateSortByTask(it, context = context))
						showSortDialog = false
					}
				)

			if ((appState.showWhatsNew && appState.firstTimeOpened) || appState.buildVersionCode != packageInfo.versionCode) {
				WhatsNewDialogComponent(appState = appState) {
					onMainEvent(MainEvent.UpdateFirstTimeOpened(false))
					onMainEvent(MainEvent.UpdateShowWhatsNew(it, context))
					onMainEvent(MainEvent.UpdateBuildVersionCode(context, packageInfo.versionCode))
				}
			}

			Column(modifier = Modifier.padding(innerPadding)) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(
							16.dp,
							8.dp
						),
					horizontalArrangement = Arrangement.spacedBy(16.dp),
					verticalAlignment = Alignment.CenterVertically
				) {

					InfoComponent(
						title = stringResource(R.string.completed),
						desc = "$totalCompletedTasks/$totalTasks Tasks",
						icon = R.drawable.ic_task_list,
						backgroundColor = LightGreen,
						dynamicTheme = appState.dynamicTheme,
						modifier = Modifier
							.weight(1f)
							.graphicsLayer {
								translationX = leftTranslate.value
							},
						onClick = { onNavigate(Routes.CompletedTaskScreen.name) }
					)

					InfoComponent(
						title = stringResource(R.string.free_time),
						desc = freeTimeText,
						icon = R.drawable.ic_clock,
						backgroundColor = Blue,
						dynamicTheme = appState.dynamicTheme,
						modifier = Modifier
							.weight(1f)
							.graphicsLayer {
								translationX = rightTranslate.value
							},
						onClick = {
							if (inCompletedTasks.isEmpty()) {
								showCustomSnackbar("Add Tasks to Analyze")
							} else {
								onNavigate(Routes.FreeTimeScreen.name)
							}
						}
					)

				}

				if (inCompletedTasks.isEmpty()) {
					EmptyTaskComponent()
				} else {

					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = 8.dp),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						Text(
							modifier = Modifier.padding(16.dp),
							text = stringResource(R.string.today_tasks),
							style = h2TextStyle,
							color = MaterialTheme.colorScheme.onBackground,
						)

						IconButton(onClick = { showSortDialog = true }) {
							Icon(
								imageVector = Icons.Default.Sort,
								contentDescription = null,
								tint = MaterialTheme.colorScheme.onBackground
							)
						}

					}

					// sort task list
					val sortedTasks: List<Task> = remember(inCompletedTasks, appState.sortBy) {
						inCompletedTasks.sortedWith(compareBy {
							when (appState.sortBy) {
								SortTask.BY_CREATE_TIME_ASCENDING -> {
									it.id
								}

								SortTask.BY_CREATE_TIME_DESCENDING -> {
									-it.id
								}

								SortTask.BY_PRIORITY_ASCENDING -> {
									it.priority
								}

								SortTask.BY_PRIORITY_DESCENDING -> {
									-it.priority
								}

								SortTask.BY_START_TIME_ASCENDING -> {
									it.startTime.toSecondOfDay()
								}

								SortTask.BY_START_TIME_DESCENDING -> {
									-it.startTime.toSecondOfDay()
								}
							}
						})
					}

					LazyColumn(
						modifier = Modifier
							.fillMaxSize()
							.padding(
								16.dp,
								0.dp
							)
					) {

						itemsIndexed(items = sortedTasks,
							key = { _, task ->
								task.id
							}) { index, task ->
							Box(
								modifier = Modifier.animateItemPlacement(tween(500))
							) {
								SwipeActionBox(item = task, onAction = {
									playSound(context, SoundEvent.TASK_DELETED)
									onEvent(HomeScreenEvent.OnSwipeTask(it))
									showCustomSnackbar(
										msg = "Task Deleted",
										actionText = "Undo",
										onClickAction = {
											onEvent(HomeScreenEvent.OnUndoDelete)
										})
								})
								{
									TaskComponent(
										task = task,
										is24HourTimeFormat = appState.is24hourTimeFormat,
										onEdit = { taskId ->
											onEvent(HomeScreenEvent.OnEditTask(taskId))
											onNavigate("${Routes.EditTaskScreen.name}/$taskId")
										},
										onComplete = {
											playSound(context, SoundEvent.TASK_COMPLETED)
											onEvent(HomeScreenEvent.OnCompleted(it, true))
										},
										onPomodoro = { taskId ->
											onEvent(HomeScreenEvent.OnPomodoro(taskId))
											onNavigate("${Routes.PomodoroScreen.name}/$taskId")
										},
										animDelay = index * Constants.LIST_ANIMATION_DELAY
									)
								}
							}
							Spacer(modifier = Modifier.height(10.dp))
						}
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun HomeScreenPreview() {
	SnaptickTheme {
		val tasks = DummyTasks.dummyTasks
		HomeScreen(tasks = tasks, MainState(), {}, {}, {}, {}, {})
	}
}