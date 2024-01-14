package com.vishal2376.snaptick.presentation.pomodoro_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.common.timerTextStyle
import com.vishal2376.snaptick.presentation.pomodoro_screen.components.CustomCircularProgressBar
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
	task: Task,
	onBack: () -> Unit
) {
	Scaffold(topBar = {
		TopAppBar(
			modifier = Modifier.padding(8.dp),
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = MaterialTheme.colorScheme.background,
			),
			title = {
				Text(
					modifier = Modifier
						.fillMaxWidth()
						.wrapContentSize(Alignment.Center),
					text = task.title,
					style = taskTextStyle
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
				IconButton(onClick = { /*TODO*/ }) {
					Icon(
						imageVector = Icons.Default.Check,
						contentDescription = null,
						tint = Color.White
					)
				}
			}
		)
	}) { innerPadding ->

		Box(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			Text(
				text = "01 : 30",
				style = timerTextStyle,
				color = Color.White
			)
			CustomCircularProgressBar()
		}
	}
}

@Preview
@Composable
fun PomodoroScreenPreview() {
	SnaptickTheme(
		darkTheme = true,
		dynamicColor = false
	) {
		val task = Task(
			id = 1,
			title = "Learn Kotlin",
			isCompleted = false,
			startTime = LocalTime.now(),
			endTime = LocalTime.now(),
			reminder = true,
			category = "",
			priority = 0
		)
		PomodoroScreen(task,
			{})
	}
}