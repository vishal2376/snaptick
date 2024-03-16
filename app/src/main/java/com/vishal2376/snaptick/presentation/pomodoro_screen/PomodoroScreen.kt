package com.vishal2376.snaptick.presentation.pomodoro_screen

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.common.timerTextStyle
import com.vishal2376.snaptick.presentation.pomodoro_screen.components.CustomCircularProgressBar
import com.vishal2376.snaptick.ui.theme.LightGray
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.util.DummyTasks
import com.vishal2376.snaptick.util.vibrateDevice
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
	task: Task,
	onEvent: (PomodoroScreenEvent) -> Unit,
	onBack: () -> Unit
) {
	val context = LocalContext.current
	val currentView = LocalView.current

	var isTimerCompleted by remember { mutableStateOf(false) }
	var totalTime by remember { mutableLongStateOf(0L) }
	var timeLeft by remember { mutableLongStateOf(0L) }
	var isPaused by remember { mutableStateOf(false) }
	var isReset by remember { mutableStateOf(false) }

	// keep screen on
	DisposableEffect(Unit) {
		currentView.keepScreenOn = true
		onDispose {
			currentView.keepScreenOn = false
		}
	}

	// empty progress bar animation
	val progressBarAnim = remember { Animatable(100f) }
	LaunchedEffect(key1 = Unit) {
		progressBarAnim.animateTo(
			1f,
			tween(1000)
		)
	}

	//flicker animation + toggle keep screen on
	val alphaValue = remember { Animatable(1f) }
	LaunchedEffect(isPaused) {
		currentView.keepScreenOn = !isPaused

		if (isPaused && (timeLeft != totalTime)) {
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
		} else {
			alphaValue.snapTo(1f)
		}
	}

	// timer logic
	if (totalTime == 0L) {
		totalTime = task.getDuration()
		timeLeft = if (task.pomodoroTimer != -1) {
			task.pomodoroTimer.toLong()
		} else {
			totalTime
		}
	}

	LaunchedEffect(isReset) {
		if (isReset) {
			isPaused = true
			timeLeft = totalTime
			alphaValue.snapTo(1f)
		}
	}

	LaunchedEffect(
		key1 = timeLeft,
		key2 = isPaused
	) {
		while (timeLeft > 0 && !isPaused) {
			delay(1000L)
			timeLeft--
		}

		if (isTimerCompleted) {
			vibrateDevice(context)
		}
	}

	// reset timer onDestroy Screen
	DisposableEffect(Unit) {
		onDispose {
			onEvent(PomodoroScreenEvent.OnDestroyScreen(task.id, timeLeft))
			totalTime = 0L
			timeLeft = 0L
		}
	}

	Scaffold(topBar = {
		TopAppBar(
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
						PomodoroScreenEvent.OnCompleted(
							taskId = task.id,
							isCompleted = true
						)
					)
					onBack()
				}) {
					Icon(
						imageVector = Icons.Default.Check,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.onPrimary
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
					text = if (isTimerCompleted) {
						stringResource(R.string.completed)
					} else {
						task.getDurationTimeStamp(timeLeft)
					},
					style = timerTextStyle,
					color = MaterialTheme.colorScheme.onPrimary
				)
				val calcProgress = 100f - ((timeLeft.toFloat() / totalTime.toFloat()) * 100f)
				if (calcProgress >= 99) {
					isTimerCompleted = true
				}
				if (!isTimerCompleted) {
					CustomCircularProgressBar(progress = if (progressBarAnim.value <= 1f) calcProgress else progressBarAnim.value)
				}
			}

			Spacer(modifier = Modifier.height(64.dp))

			AnimatedVisibility(!isTimerCompleted) {
				Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
					FloatingActionButton(
						onClick = {
							isPaused = !isPaused
							isReset = false
						},
						shape = CircleShape,
						elevation = FloatingActionButtonDefaults.elevation(4.dp),
						containerColor = MaterialTheme.colorScheme.secondary,
						contentColor = LightGray
					) {
						Icon(
							imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
							tint = MaterialTheme.colorScheme.onPrimary,
							contentDescription = null
						)
					}

					FloatingActionButton(
						onClick = {
							isReset = true
						},
						shape = CircleShape,
						elevation = FloatingActionButtonDefaults.elevation(4.dp),
						containerColor = MaterialTheme.colorScheme.secondary,
						contentColor = LightGray
					) {
						Icon(
							imageVector = Icons.Default.Refresh,
							tint = MaterialTheme.colorScheme.onPrimary,
							contentDescription = null
						)
					}
				}
			}
			Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
		}
	}
}

@Preview
@Composable
fun PomodoroScreenPreview() {
	SnaptickTheme {
		val task = DummyTasks.dummyTasks[0]
		PomodoroScreen(task, {}, {})
	}
}