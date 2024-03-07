package com.vishal2376.snaptick.this_week_task_screen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.home_screen.components.EmptyTaskComponent
import com.vishal2376.snaptick.presentation.home_screen.components.TaskComponent
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.DummyTasks

@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalFoundationApi::class
)
@Composable
fun ThisWeekTaskScreen(
	tasks: List<Task>,
	onBack: () -> Unit
) {

	val repeatedTasks = tasks.filter { it.isRepeated }

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
		)
	}) { innerPadding ->

		Column(modifier = Modifier.padding(innerPadding)) {

			if (repeatedTasks.isEmpty()) {
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
					itemsIndexed(items = repeatedTasks,
						key = { index, task ->
							task.id
						}) { index, task ->
						Box(
							modifier = Modifier.animateItemPlacement(tween(500))
						) {
							TaskComponent(
								task = task,
								onEdit = {},
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
		val tasks = DummyTasks.tasks
		ThisWeekTaskScreen(
			tasks = tasks,
			{}
		)
	}
}