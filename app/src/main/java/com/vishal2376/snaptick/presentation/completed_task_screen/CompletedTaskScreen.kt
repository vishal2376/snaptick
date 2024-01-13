package com.vishal2376.snaptick.presentation.completed_task_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.home_screen.HomeScreenEvent
import com.vishal2376.snaptick.presentation.home_screen.components.EmptyTaskComponent
import com.vishal2376.snaptick.presentation.home_screen.components.TaskComponent
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedTaskScreen(
	completedTasks: List<Task>,
	onEvent: (HomeScreenEvent) -> Unit,
	onClose: () -> Unit
) {
	Scaffold(topBar = {
		TopAppBar(
			modifier = Modifier.padding(8.dp),
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = MaterialTheme.colorScheme.background,
			),
			title = {
				Text(
					text = "Completed Tasks",
					style = h1TextStyle
				)
			},
			navigationIcon = {
				IconButton(onClick = { onClose() }) {
					Icon(
						imageVector = Icons.Rounded.ArrowBack,
						contentDescription = null
					)
				}
			},
		)
	}) { innerPadding ->

		Column(modifier = Modifier.padding(innerPadding)) {

			if (completedTasks.isEmpty()) {
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
					items(items = completedTasks,
						key = { it.id }) { task ->
						TaskComponent(
							task = task,
							onUpdate = {},
							onComplete = {
								onEvent(
									HomeScreenEvent.OnCompleted(
										it,
										false
									)
								)
							}
						)
						Spacer(modifier = Modifier.height(10.dp))
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun CompletedTaskScreenPreview() {
	SnaptickTheme(
		darkTheme = true,
		dynamicColor = false
	) {
		val tasks = listOf(
			Task(
				id = 1,
				title = "Learn Kotlin",
				isCompleted = true,
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
		CompletedTaskScreen(
			completedTasks = tasks,
			{},
			{}
		)
	}
}