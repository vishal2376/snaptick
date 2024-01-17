package com.vishal2376.snaptick.presentation.pomodoro_screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.common.timerTextStyle
import com.vishal2376.snaptick.presentation.home_screen.HomeScreenEvent
import com.vishal2376.snaptick.presentation.pomodoro_screen.components.CustomCircularProgressBar
import com.vishal2376.snaptick.ui.theme.LightGray
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
	task: Task,
	onEvent: (HomeScreenEvent) -> Unit,
	onBack: () -> Unit
) {

	var totalTime by remember {
		mutableLongStateOf(0L)
	}

	var timeLeft by remember {
		mutableLongStateOf(0L)
	}

	if (task.title.isNotEmpty() && totalTime == 0L) {
		totalTime = task.getDuration()
		timeLeft = task.getDuration()
	}

	var isPaused by remember {
		mutableStateOf(false)
	}

	//flicker animation
	val alphaValue = remember {
		Animatable(1f)
	}

	LaunchedEffect(
		key1 = timeLeft,
		key2 = isPaused
	) {
		while (timeLeft > 0 && !isPaused) {
			delay(1000L)
			timeLeft--
		}

		//flicker animation
		launch {
			if (isPaused) {
				alphaValue.animateTo(
					targetValue = 0.2f,
					animationSpec = infiniteRepeatable(
						tween(
							1000,
							easing = LinearEasing
						),
						repeatMode = RepeatMode.Reverse
					)
				)
			}
		}
	}

	DisposableEffect(Unit) {
		onDispose {
			totalTime = 0L
			timeLeft = 0L
		}
	}

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
				IconButton(onClick = {
					onEvent(
						HomeScreenEvent.OnCompleted(
							taskId = task.id,
							isCompleted = true
						)
					)
					onBack()
				}) {
					Icon(
						imageVector = Icons.Default.Check,
						contentDescription = null,
						tint = Color.White
					)
				}
			}
		)
	}) { innerPadding ->

		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Box(contentAlignment = Alignment.Center) {
				Text(
					modifier = Modifier.alpha(alphaValue.value),
					text = task.getFormattedDuration(timeLeft),
					style = timerTextStyle,
					color = Color.White
				)
				val calcProgress = 100f - ((timeLeft.toFloat() / totalTime.toFloat()) * 100f)
				CustomCircularProgressBar(progress = calcProgress)
			}

			Spacer(modifier = Modifier.height(64.dp))

			Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
				FloatingActionButton(
					onClick = { isPaused = !isPaused },
					shape = CircleShape,
					containerColor = MaterialTheme.colorScheme.secondary,
					contentColor = LightGray
				) {
					Icon(
						imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
						contentDescription = null
					)
				}

				FloatingActionButton(
					onClick = {
						isPaused = true
						timeLeft = totalTime
					},
					shape = CircleShape,
					containerColor = MaterialTheme.colorScheme.secondary,
					contentColor = LightGray
				) {
					Icon(
						imageVector = Icons.Default.Refresh,
						contentDescription = null
					)
				}
			}
			Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
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
			startTime = LocalTime.of(
				10,
				0
			),
			endTime = LocalTime.of(
				11,
				0
			),
			reminder = true,
			category = "",
			priority = 0
		)
		PomodoroScreen(task,
			{},
			{})
	}
}