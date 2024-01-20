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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.fontRoboto
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.home_screen.components.EmptyTaskComponent
import com.vishal2376.snaptick.presentation.home_screen.components.InfoComponent
import com.vishal2376.snaptick.presentation.home_screen.components.NavigationDrawerComponent
import com.vishal2376.snaptick.presentation.home_screen.components.TaskComponent
import com.vishal2376.snaptick.presentation.main.MainEvent
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.Green
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.ui.theme.Yellow
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalFoundationApi::class
)
@Composable
fun HomeScreen(
	tasks: List<Task>,
	onMainEvent: (MainEvent) -> Unit,
	onEvent: (HomeScreenEvent) -> Unit,
	onEditTask: (id: Int) -> Unit,
	onAddTask: () -> Unit,
	onClickCompletedInfo: () -> Unit,
	onPomodoroTask: (id: Int) -> Unit,
) {

	val completedTasks = mutableListOf<Task>()
	val inCompletedTasks = mutableListOf<Task>()

	tasks.filterTo(completedTasks) { it.isCompleted }
	tasks.filterTo(inCompletedTasks) { !it.isCompleted }

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

	// navigation drawer
	val scope = rememberCoroutineScope()
	val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

	ModalNavigationDrawer(
		drawerState = drawerState,
		drawerContent = {
			ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.primary) {
				NavigationDrawerComponent(onMainEvent)
			}
		}) {
		Scaffold(topBar = {
			TopAppBar(modifier = Modifier.padding(end = 16.dp),
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.background
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
					Text(
						text = "10",
						fontSize = 18.sp,
						fontFamily = fontRoboto,
						fontWeight = FontWeight.Bold
					)
					Spacer(modifier = Modifier.width(4.dp))
					Icon(
						painter = painterResource(id = R.drawable.ic_fire),
						contentDescription = null,
						tint = Yellow,
						modifier = Modifier.size(22.dp)
					)
				})
		},
			floatingActionButton = {
				FloatingActionButton(
					onClick = {
						onAddTask()
					},
					containerColor = Blue,
					contentColor = MaterialTheme.colorScheme.secondary
				) {
					Icon(
						imageVector = Icons.Default.Add,
						contentDescription = null
					)
				}
			}) { innerPadding ->

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
						title = "Completed",
						desc = "$totalCompletedTasks/$totalTasks Tasks",
						icon = R.drawable.ic_task_list,
						backgroundColor = Green,
						modifier = Modifier
							.weight(1f)
							.graphicsLayer {
								translationX = leftTranslate.value
							},
						onClick = { onClickCompletedInfo() }
					)

					InfoComponent(
						title = "Free Time",
						desc = "8 hours",
						icon = R.drawable.ic_clock,
						backgroundColor = Blue,
						modifier = Modifier
							.weight(1f)
							.graphicsLayer {
								translationX = rightTranslate.value
							},
						onClick = {}
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
							text = stringResource(R.string.today_tasks),
							style = h2TextStyle,
							color = Color.White,
							modifier = Modifier.padding(16.dp)
						)

						IconButton(onClick = { /*TODO*/ }) {
							Icon(
								imageVector = Icons.Default.Sort,
								contentDescription = null,
								tint = Color.White
							)
						}

					}
					LazyColumn(
						modifier = Modifier
							.fillMaxSize()
							.padding(
								16.dp,
								0.dp
							)
					) {
						items(items = inCompletedTasks,
							key = { it.id }) { task ->
							Box(
								modifier = Modifier.animateItemPlacement(
									tween(
										500
									)
								)
							) {
								TaskComponent(
									task = task,
									onUpdate = onEditTask,
									onComplete = {
										onEvent(
											HomeScreenEvent.OnCompleted(
												it,
												true
											)
										)
									},
									onPomodoro = onPomodoroTask

								)
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
	SnaptickTheme(
		darkTheme = true,
		dynamicColor = false
	) {
		val tasks = listOf(
			Task(
				id = 1,
				title = "Learn Kotlin",
				isCompleted = false,
				startTime = LocalTime.now(),
				endTime = LocalTime.now(),
				reminder = true,
				category = "",
				priority = 0
			),
			Task(
				id = 2,
				title = "Drink Water",
				isCompleted = true,
				startTime = LocalTime.now(),
				endTime = LocalTime.now(),
				reminder = false,
				category = "",
				priority = 1
			)
		)
		HomeScreen(
			tasks = tasks,
			{},
			{},
			{},
			{},
			{},
			{}
		)
	}
}