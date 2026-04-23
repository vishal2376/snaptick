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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.SnackbarController
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.common.timerTextStyle
import com.vishal2376.snaptick.presentation.common.utils.formatDurationTimestamp
import com.vishal2376.snaptick.presentation.pomodoro_screen.action.PomodoroAction
import com.vishal2376.snaptick.presentation.pomodoro_screen.components.CustomCircularProgressBar
import com.vishal2376.snaptick.presentation.pomodoro_screen.events.PomodoroEvent
import com.vishal2376.snaptick.presentation.pomodoro_screen.state.PomodoroState
import com.vishal2376.snaptick.ui.theme.LightGreen
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
	state: PomodoroState,
	events: Flow<PomodoroEvent>,
	onAction: (PomodoroAction) -> Unit,
	onBack: () -> Unit
) {
	val currentView = LocalView.current

	DisposableEffect(Unit) {
		currentView.keepScreenOn = true
		onDispose { currentView.keepScreenOn = false }
	}

	LaunchedEffect(state.isPaused) {
		currentView.keepScreenOn = !state.isPaused
	}

	LaunchedEffect(Unit) {
		events.collect { event ->
			when (event) {
				is PomodoroEvent.ResumingPreviousSession -> SnackbarController.showCustomSnackbar(
					"Resuming Previous Session",
					actionColor = LightGreen
				)
				is PomodoroEvent.TaskMarkedCompleted -> onBack()
				is PomodoroEvent.TimerCompleted -> {}
			}
		}
	}

	val progressBarAnim = remember { Animatable(100f) }
	LaunchedEffect(Unit) {
		progressBarAnim.animateTo(1f, tween(1000))
	}

	val alphaValue = remember { Animatable(1f) }
	LaunchedEffect(state.isPaused, state.totalTime, state.timeLeft) {
		if (state.isPaused && state.timeLeft != state.totalTime && state.totalTime > 0) {
			alphaValue.animateTo(
				targetValue = 0.2f,
				animationSpec = infiniteRepeatable(
					tween(1000, easing = LinearEasing),
					repeatMode = RepeatMode.Reverse
				)
			)
		} else {
			alphaValue.snapTo(1f)
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
					text = state.taskTitle,
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
				IconButton(onClick = { onAction(PomodoroAction.MarkCompleted) }) {
					Icon(
						imageVector = Icons.Default.Check,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.onBackground
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
					text = if (state.isCompleted) {
						stringResource(R.string.completed)
					} else {
						formatDurationTimestamp(state.timeLeft)
					},
					style = timerTextStyle,
					color = MaterialTheme.colorScheme.onBackground
				)
				val calcProgress = if (state.totalTime > 0) {
					100f - ((state.timeLeft.toFloat() / state.totalTime.toFloat()) * 100f)
				} else 0f
				if (!state.isCompleted) {
					CustomCircularProgressBar(
						progress = if (progressBarAnim.value <= 1f) calcProgress else progressBarAnim.value
					)
				}
			}

			Spacer(modifier = Modifier.height(64.dp))

			AnimatedVisibility(!state.isCompleted) {
				Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
					FloatingActionButton(
						onClick = { onAction(PomodoroAction.TogglePause) },
						shape = CircleShape,
						elevation = FloatingActionButtonDefaults.elevation(4.dp),
						containerColor = MaterialTheme.colorScheme.primaryContainer,
						contentColor = MaterialTheme.colorScheme.onPrimaryContainer
					) {
						Icon(
							imageVector = if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
							tint = MaterialTheme.colorScheme.onPrimaryContainer,
							contentDescription = null
						)
					}

					FloatingActionButton(
						onClick = { onAction(PomodoroAction.Reset) },
						shape = CircleShape,
						elevation = FloatingActionButtonDefaults.elevation(4.dp),
						containerColor = MaterialTheme.colorScheme.primaryContainer,
						contentColor = MaterialTheme.colorScheme.onPrimaryContainer
					) {
						Icon(
							imageVector = Icons.Default.Refresh,
							tint = MaterialTheme.colorScheme.onPrimaryContainer,
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
		PomodoroScreen(
			state = PomodoroState(taskTitle = "Sample", totalTime = 1800L, timeLeft = 900L),
			events = emptyFlow(),
			onAction = {},
			onBack = {}
		)
	}
}
