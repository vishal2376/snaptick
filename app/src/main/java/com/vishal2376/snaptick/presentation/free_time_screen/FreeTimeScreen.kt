package com.vishal2376.snaptick.presentation.free_time_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import com.vishal2376.snaptick.presentation.free_time_screen.components.CustomPieChart
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeTimeScreen(tasks: List<Task>) {

	val inCompletedTasks = tasks.filter { !it.isCompleted }
	val sortedTasks = inCompletedTasks.sortedBy { -it.getDuration() }

	Scaffold(topBar = {
		TopAppBar(
			modifier = Modifier.padding(8.dp),
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = MaterialTheme.colorScheme.background,
			),
			title = {
				Text(
					text = "Free Time",
					style = h1TextStyle
				)
			},
			navigationIcon = {
				IconButton(onClick = { /*onClose()*/ }) {
					Icon(
						imageVector = Icons.Rounded.ArrowBack,
						contentDescription = null
					)
				}
			},
		)
	}) { innerPadding ->

		Column(modifier = Modifier.padding(innerPadding)) {
			val data = sortedTasks.map { it.getDuration() }
			CustomPieChart(data = data)
		}
	}
}

@Preview
@Composable
fun FreeTimeScreenPreview() {
	val tasks = listOf(
		Task(
			id = 1,
			title = "Learn Kotlin",
			isCompleted = false,
			startTime = LocalTime.of(9, 0),
			endTime = LocalTime.of(10, 0),
			reminder = true,
			category = "",
			priority = 0
		),
		Task(
			id = 2,
			title = "Drink Water",
			isCompleted = false,
			startTime = LocalTime.of(9, 0),
			endTime = LocalTime.of(11, 0),
			reminder = false,
			category = "",
			priority = 1
		)
	)
	SnaptickTheme {
		FreeTimeScreen(tasks = tasks)
	}
}