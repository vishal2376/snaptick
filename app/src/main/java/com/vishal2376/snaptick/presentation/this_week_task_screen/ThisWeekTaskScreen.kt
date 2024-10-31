package com.vishal2376.snaptick.presentation.this_week_task_screen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.infoTextStyle
import com.vishal2376.snaptick.presentation.home_screen.HomeScreenEvent
import com.vishal2376.snaptick.presentation.home_screen.components.EmptyTaskComponent
import com.vishal2376.snaptick.presentation.home_screen.components.TaskComponent
import com.vishal2376.snaptick.presentation.main.MainState
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.DummyTasks
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalFoundationApi::class
)
@Composable
fun ThisWeekTaskScreen(
	tasks: List<Task>,
	appState: MainState,
	onEvent: (HomeScreenEvent) -> Unit,
	onEditTask: (id: Int) -> Unit,
	onBack: () -> Unit
) {


	val startDayOfWeek = WeekFields.of(DayOfWeek.MONDAY, 1).weekOfYear()
	val currentWeek = LocalDate.now().get(startDayOfWeek)
	val previousWeek = LocalDate.now().minusWeeks(1)

	val thisWeekTasks = tasks.filter { task ->
		val taskWeekOfYear = task.date.get(startDayOfWeek)
		(taskWeekOfYear == currentWeek) || ((task.date in previousWeek..LocalDate.now()) && task.isRepeated)
	}

	Scaffold(topBar = {
		TopAppBar(
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = MaterialTheme.colorScheme.background,
			),
			title = {
				Text(
					text = stringResource(R.string.this_week),
					style = h1TextStyle
				)
			},
			navigationIcon = {
				IconButton(onClick = { onBack() }) {
					Icon(
						imageVector = Icons.Rounded.ArrowBack,
						contentDescription = null
					)
				}
			},
			actions = {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(4.dp),
					modifier = Modifier.padding(end = 16.dp)
				) {
					Icon(
						imageVector = Icons.Default.Today, contentDescription = null,
						tint = MaterialTheme.colorScheme.onBackground
					)
					Text(
						text = LocalDate.now().dayOfWeek.getDisplayName(
							TextStyle.SHORT,
							Locale.getDefault()
						),
						style = infoTextStyle,
						color = MaterialTheme.colorScheme.onBackground
					)
				}
			}
		)
	}) { innerPadding ->

		Column(modifier = Modifier.padding(innerPadding)) {

			if (thisWeekTasks.isEmpty()) {
				EmptyTaskComponent()
			} else {
				LazyColumn(
					modifier = Modifier
						.fillMaxSize()
						.padding(
							16.dp,
							0.dp
						)
				) {
					itemsIndexed(items = thisWeekTasks,
						key = { index, task ->
							task.id
						}) { index, task ->
						Box(
							modifier = Modifier.animateItemPlacement(tween(500))
						) {
							TaskComponent(
								task = task,
								is24HourTimeFormat = appState.is24hourTimeFormat,
								onEdit = {
									onEvent(HomeScreenEvent.OnEditTask(it))
									onEditTask(it)
								},
								onComplete = {},
								onPomodoro = {},
								animDelay = index * Constants.LIST_ANIMATION_DELAY
							)
						}
						Spacer(modifier = Modifier.height(10.dp))
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun ThisWeekTaskScreenPreview() {
	SnaptickTheme {
		val tasks = DummyTasks.dummyTasks
		ThisWeekTaskScreen(
			tasks = tasks, appState = MainState(), {}, {}, {})
	}
}