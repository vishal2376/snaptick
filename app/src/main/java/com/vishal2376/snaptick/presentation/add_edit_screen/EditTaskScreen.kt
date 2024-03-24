package com.vishal2376.snaptick.presentation.add_edit_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.add_edit_screen.components.ConfirmDeleteDialog
import com.vishal2376.snaptick.presentation.add_edit_screen.components.CustomDurationDialogComponent
import com.vishal2376.snaptick.presentation.add_edit_screen.components.DurationComponent
import com.vishal2376.snaptick.presentation.add_edit_screen.components.PriorityComponent
import com.vishal2376.snaptick.presentation.add_edit_screen.components.WeekDaysComponent
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.Priority
import com.vishal2376.snaptick.presentation.common.ShowTimePicker
import com.vishal2376.snaptick.presentation.common.SnackbarController.showCustomSnackbar
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.main.MainState
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.DarkGreen
import com.vishal2376.snaptick.ui.theme.LightGreen
import com.vishal2376.snaptick.ui.theme.Red
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.util.DummyTasks
import com.vishal2376.snaptick.util.checkValidTask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
	task: Task,
	appState: MainState,
	onEvent: (AddEditScreenEvent) -> Unit,
	onBack: () -> Unit
) {
	val context = LocalContext.current

	var taskStartTime by remember { mutableStateOf(task.startTime) }
	var taskEndTime by remember { mutableStateOf(task.endTime) }
	var isTimeUpdated by remember { mutableStateOf(false) }
	val taskDuration by remember { mutableLongStateOf(task.getDuration() / 60) }

	var showDialogConfirmDelete by remember { mutableStateOf(false) }
	var showDialogCustomDuration by remember { mutableStateOf(false) }

	LaunchedEffect(isTimeUpdated)
	{
		if (isTimeUpdated) {
			onEvent(AddEditScreenEvent.ResetPomodoroTimer)
		}
	}

	Scaffold(topBar = {
		TopAppBar(
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = MaterialTheme.colorScheme.background,
			),
			title = {
				Text(
					text = stringResource(R.string.edit_task),
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
				IconButton(onClick = { showDialogConfirmDelete = true }) {
					Icon(
						imageVector = Icons.Default.Delete,
						contentDescription = null
					)
				}
			})
	}) { innerPadding ->


		// confirm delete dialog
		if (showDialogConfirmDelete) {
			ConfirmDeleteDialog(
				onClose = { showDialogConfirmDelete = false },
				onDelete = {
					onEvent(AddEditScreenEvent.OnDeleteTaskClick(task))
					showDialogConfirmDelete = false
					onBack()
				}
			)
		}

		// confirm delete dialog
		if (showDialogCustomDuration) {
			CustomDurationDialogComponent(
				onClose = { showDialogCustomDuration = false },
				onSelect = { time ->
					val duration = time.toSecondOfDay() / 60L
					onEvent(AddEditScreenEvent.OnUpdateEndTime(task.startTime.plusMinutes(duration)))
					taskEndTime = taskStartTime.plusMinutes(duration)
					isTimeUpdated = !isTimeUpdated
				}
			)
		}

		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.verticalScroll(rememberScrollState()),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(16.dp)
			) {

				TextField(
					value = task.title,
					singleLine = true,
					colors = TextFieldDefaults.colors(
						focusedContainerColor = MaterialTheme.colorScheme.secondary,
						unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
						disabledContainerColor = MaterialTheme.colorScheme.secondary,
						unfocusedIndicatorColor = Color.Transparent,
						focusedIndicatorColor = Color.Transparent,
						cursorColor = MaterialTheme.colorScheme.onPrimary
					),
					textStyle = taskTextStyle,
					onValueChange = {
						onEvent(AddEditScreenEvent.OnUpdateTitle(it))
					},
					placeholder = { Text(text = stringResource(id = R.string.what_would_you_like_to_do)) },
					shape = RoundedCornerShape(16.dp),
					modifier = Modifier
						.fillMaxWidth()
						.padding(
							32.dp,
							8.dp
						),
					keyboardOptions = KeyboardOptions(
						capitalization = KeyboardCapitalization.Sentences,
						imeAction = ImeAction.Done
					)
				)
				Row(
					horizontalArrangement = Arrangement.SpaceBetween,
					modifier = Modifier
						.fillMaxWidth(.8f)
				) {
					Column(horizontalAlignment = Alignment.CenterHorizontally) {
						Text(
							text = stringResource(R.string.start_time),
							style = taskTextStyle,
							color = if (appState.theme == AppTheme.Light) DarkGreen else LightGreen
						)
						Spacer(modifier = Modifier.height(8.dp))
						ShowTimePicker(
							time = taskStartTime
						) { snappedTime ->
							onEvent(AddEditScreenEvent.OnUpdateStartTime(snappedTime))
							taskStartTime = snappedTime
						}
					}
					Column(horizontalAlignment = Alignment.CenterHorizontally) {
						Text(
							text = stringResource(R.string.end_time),
							style = taskTextStyle,
							color = Red
						)
						Spacer(modifier = Modifier.height(8.dp))
						ShowTimePicker(
							time = taskEndTime,
							isTimeUpdated = isTimeUpdated
						) { snappedTime ->
							onEvent(AddEditScreenEvent.OnUpdateEndTime(snappedTime))
							taskEndTime = snappedTime
						}
					}
				}
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(start = 32.dp, end = 32.dp, top = 8.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Text(
						text = stringResource(R.string.start_time),
						style = h2TextStyle,
						color = MaterialTheme.colorScheme.onPrimary
					)

					Text(
						text = task.getFormattedDuration(),
						style = taskTextStyle,
						color = MaterialTheme.colorScheme.onPrimary
					)
				}
				DurationComponent(
					modifier = Modifier
						.padding(horizontal = 24.dp),
					durationList = appState.durationList,
					defaultDuration = taskDuration
				) { duration ->
					if (duration == 0L) {
						showDialogCustomDuration = true
					} else {

						onEvent(
							AddEditScreenEvent.OnUpdateEndTime(
								task.startTime.plusMinutes(
									duration
								)
							)
						)
						taskEndTime = taskStartTime.plusMinutes(duration)
						isTimeUpdated = !isTimeUpdated
					}
				}

				Column {

					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(32.dp, 0.dp),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						Text(
							text = stringResource(R.string.reminder),
							style = h2TextStyle,
							color = MaterialTheme.colorScheme.onPrimary
						)

						Switch(
							checked = task.reminder,
							onCheckedChange = {
								onEvent(AddEditScreenEvent.OnUpdateReminder(it))
							},
							colors = SwitchDefaults.colors(
								checkedThumbColor = Blue,
								checkedTrackColor = MaterialTheme.colorScheme.secondary,
								uncheckedTrackColor = MaterialTheme.colorScheme.secondary
							)
						)
					}

					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(32.dp, 0.dp),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						Text(
							text = stringResource(R.string.repeat),
							style = h2TextStyle,
							color = MaterialTheme.colorScheme.onPrimary
						)

						Switch(
							checked = task.isRepeated,
							onCheckedChange = {
								onEvent(AddEditScreenEvent.OnUpdateIsRepeated(it))
							},
							colors = SwitchDefaults.colors(
								checkedThumbColor = Blue,
								checkedTrackColor = MaterialTheme.colorScheme.secondary,
								uncheckedTrackColor = MaterialTheme.colorScheme.secondary
							)
						)
					}

					AnimatedVisibility(visible = task.isRepeated) {
						WeekDaysComponent(
							defaultRepeatedDays = task.getRepeatWeekList(),
							onChange = { onEvent(AddEditScreenEvent.OnUpdateRepeatWeekDays(it)) }
						)
					}
				}

				PriorityComponent(defaultSortTask = Priority.entries[task.priority]) {
					onEvent(AddEditScreenEvent.OnUpdatePriority(it))
				}
			}

			//bottom action buttons
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(32.dp),
				verticalArrangement = Arrangement.spacedBy(16.dp)
			) {
				Button(
					onClick = {
						val (isValid, errorMessage) = checkValidTask(
							task = task,
							totalTasksDuration = appState.totalTaskDuration - task.getDuration(
								checkPastTask = true
							)
						)

						if (isValid) {
							onEvent(AddEditScreenEvent.OnUpdateTask)
							onBack()
						} else {
							showCustomSnackbar(errorMessage)
						}
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = Blue,
						contentColor = Color.Black
					),
					shape = RoundedCornerShape(16.dp),
					modifier = Modifier.fillMaxWidth()
				) {
					Text(
						text = stringResource(R.string.update_task),
						fontWeight = FontWeight.Bold,
						fontSize = 16.sp,
						modifier = Modifier.padding(8.dp)
					)
				}
			}
		}
	}
}

@Preview
@Composable
fun EditTaskScreenPreview() {
	SnaptickTheme {
		val task = DummyTasks.dummyTasks[0]
		EditTaskScreen(task, MainState(), {}, {})
	}
}