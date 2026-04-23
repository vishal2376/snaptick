package com.vishal2376.snaptick.presentation.add_edit_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.add_edit_screen.action.AddEditAction
import com.vishal2376.snaptick.presentation.add_edit_screen.components.ConfirmDeleteDialog
import com.vishal2376.snaptick.presentation.add_edit_screen.components.CustomDurationDialogComponent
import com.vishal2376.snaptick.presentation.add_edit_screen.components.DurationComponent
import com.vishal2376.snaptick.presentation.add_edit_screen.components.PriorityComponent
import com.vishal2376.snaptick.presentation.add_edit_screen.components.ShowNativeTimePicker
import com.vishal2376.snaptick.presentation.add_edit_screen.components.WeekDaysComponent
import com.vishal2376.snaptick.presentation.add_edit_screen.events.AddEditEvent
import com.vishal2376.snaptick.presentation.add_edit_screen.state.AddEditState
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.NativeTimePickerDialog
import com.vishal2376.snaptick.presentation.common.ShowTimePicker
import com.vishal2376.snaptick.presentation.common.SnackbarController.showCustomSnackbar
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.common.utils.formatDuration
import com.vishal2376.snaptick.presentation.main.state.MainState
import com.vishal2376.snaptick.ui.theme.DarkGreen
import com.vishal2376.snaptick.ui.theme.LightGreen
import com.vishal2376.snaptick.ui.theme.Red
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.ui.theme.priorityColors
import com.vishal2376.snaptick.util.checkValidTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
	state: AddEditState,
	events: Flow<AddEditEvent>,
	appState: MainState,
	onAction: (AddEditAction) -> Unit,
	onBack: () -> Unit
) {
	var showDialogConfirmDelete by remember { mutableStateOf(false) }
	var showDialogCustomDuration by remember { mutableStateOf(false) }
	var showDialogStartTimePicker by remember { mutableStateOf(false) }
	var showDialogEndTimePicker by remember { mutableStateOf(false) }

	LaunchedEffect(state.timeUpdateTick) {
		if (state.timeUpdateTick > 0) {
			onAction(AddEditAction.ResetPomodoroTimer)
		}
	}

	LaunchedEffect(Unit) {
		events.collect { event ->
			when (event) {
				is AddEditEvent.TaskUpdated, is AddEditEvent.TaskDeleted -> onBack()
				else -> {}
			}
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


		if (showDialogStartTimePicker) {
			NativeTimePickerDialog(
				time = state.startTime,
				is24hourFormat = appState.is24hourTimeFormat
			) {
				onAction(AddEditAction.UpdateStartTime(it))
				showDialogStartTimePicker = false
			}
		}

		if (showDialogEndTimePicker) {
			NativeTimePickerDialog(
				time = state.endTime,
				is24hourFormat = appState.is24hourTimeFormat
			) {
				onAction(AddEditAction.UpdateEndTime(it))
				showDialogEndTimePicker = false
			}
		}

		if (showDialogConfirmDelete) {
			ConfirmDeleteDialog(
				onClose = { showDialogConfirmDelete = false },
				onDelete = {
					onAction(AddEditAction.DeleteTask)
					showDialogConfirmDelete = false
				}
			)
		}

		if (showDialogCustomDuration) {
			CustomDurationDialogComponent(
				onClose = { showDialogCustomDuration = false },
				onSelect = { time ->
					val duration = time.toSecondOfDay() / 60L
					onAction(AddEditAction.UpdateDurationMinutes(duration))
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
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {

				Box(
					modifier = Modifier
						.fillMaxWidth()
						.padding(32.dp, 8.dp)
						.background(priorityColors[state.priority.ordinal], RoundedCornerShape(8.dp))
				) {
					TextField(
						value = state.title,
						singleLine = true,
						colors = TextFieldDefaults.colors(
							focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
							unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
							disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
							unfocusedIndicatorColor = Color.Transparent,
							focusedIndicatorColor = Color.Transparent,
							cursorColor = MaterialTheme.colorScheme.onBackground
						),
						textStyle = taskTextStyle,
						onValueChange = { onAction(AddEditAction.UpdateTitle(it)) },
						placeholder = {
							Text(
								text = stringResource(id = R.string.what_would_you_like_to_do),
								color = MaterialTheme.colorScheme.onPrimaryContainer
							)
						},
						shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
						modifier = Modifier
							.fillMaxWidth()
							.padding(start = 8.dp),
						keyboardOptions = KeyboardOptions(
							capitalization = KeyboardCapitalization.Sentences,
							imeAction = ImeAction.Done
						)
					)
				}
				Row(
					horizontalArrangement = Arrangement.SpaceAround,
					modifier = Modifier
						.fillMaxWidth()
						.padding(24.dp, 8.dp)
				) {
					Column(
						modifier = Modifier.alpha(if (state.isAllDay && !state.reminder) 0.3f else 1f),
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Text(
							text = stringResource(R.string.start_time),
							style = taskTextStyle,
							color = if (appState.theme == AppTheme.Light) DarkGreen else LightGreen
						)
						Spacer(modifier = Modifier.height(8.dp))
						if (appState.isWheelTimePicker) {
							ShowTimePicker(
								time = state.startTime,
								is24hourFormat = appState.is24hourTimeFormat
							) { snappedTime ->
								onAction(AddEditAction.UpdateStartTime(snappedTime))
							}
						} else {
							ShowNativeTimePicker(
								time = state.startTime,
								is24hourFormat = appState.is24hourTimeFormat
							) {
								showDialogStartTimePicker = true
							}
						}
					}
					if (!state.isAllDay) {
						Column(horizontalAlignment = Alignment.CenterHorizontally) {
							Text(
								text = stringResource(R.string.end_time),
								style = taskTextStyle,
								color = Red
							)
							Spacer(modifier = Modifier.height(8.dp))
							if (appState.isWheelTimePicker) {
								ShowTimePicker(
									time = state.endTime,
									is24hourFormat = appState.is24hourTimeFormat,
									isTimeUpdated = state.timeUpdateTick % 2 == 1
								) { snappedTime ->
									onAction(AddEditAction.UpdateEndTime(snappedTime))
								}
							} else {
								ShowNativeTimePicker(
									time = state.endTime,
									is24hourFormat = appState.is24hourTimeFormat
								) {
									showDialogEndTimePicker = true
								}
							}
						}
					}
				}
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.alpha(if (state.isAllDay) 0.2f else 1f)
						.padding(start = 32.dp, end = 32.dp, top = 8.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Text(
						text = stringResource(R.string.duration),
						style = h2TextStyle,
						color = MaterialTheme.colorScheme.onBackground
					)

					Text(
						text = formatDuration(state.startTime, state.endTime),
						style = taskTextStyle,
						color = MaterialTheme.colorScheme.onBackground
					)
				}
				DurationComponent(
					modifier = Modifier
						.alpha(if (state.isAllDay) 0.2f else 1f)
						.padding(horizontal = 24.dp),
					durationList = appState.durationList,
					defaultDuration = state.duration
				) { duration ->
					if (!state.isAllDay) {
						if (duration == 0L) {
							showDialogCustomDuration = true
						} else {
							onAction(AddEditAction.UpdateDurationMinutes(duration))
						}
					}
				}

				Column {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(24.dp, 0.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						Icon(imageVector = Icons.Default.AccessTime, contentDescription = null)
						Text(
							modifier = Modifier
								.weight(1f)
								.padding(start = 4.dp),
							text = stringResource(R.string.all_day),
							style = h2TextStyle,
							color = MaterialTheme.colorScheme.onBackground
						)

						Switch(
							checked = state.isAllDay,
							onCheckedChange = { onAction(AddEditAction.UpdateAllDay(it)) },
							colors = SwitchDefaults.colors(
								checkedThumbColor = MaterialTheme.colorScheme.primary,
								checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
								uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer
							)
						)
					}

					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(24.dp, 0.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						Icon(
							imageVector = Icons.Default.NotificationsNone,
							contentDescription = null
						)
						Text(
							modifier = Modifier
								.weight(1f)
								.padding(start = 4.dp),
							text = stringResource(R.string.reminder),
							style = h2TextStyle,
							color = MaterialTheme.colorScheme.onBackground
						)

						Switch(
							checked = state.reminder,
							onCheckedChange = { onAction(AddEditAction.UpdateReminder(it)) },
							colors = SwitchDefaults.colors(
								checkedThumbColor = MaterialTheme.colorScheme.primary,
								checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
								uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer
							)
						)
					}
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(24.dp, 0.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
						Text(
							modifier = Modifier
								.weight(1f)
								.padding(start = 4.dp),
							text = stringResource(R.string.repeat),
							style = h2TextStyle,
							color = MaterialTheme.colorScheme.onBackground
						)

						Switch(
							checked = state.isRepeated,
							onCheckedChange = { onAction(AddEditAction.UpdateRepeated(it)) },
							colors = SwitchDefaults.colors(
								checkedThumbColor = MaterialTheme.colorScheme.primary,
								checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
								uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer
							)
						)
					}

					AnimatedVisibility(visible = state.isRepeated) {
						val repeatDays = if (state.repeatWeekdays.isEmpty()) emptyList()
						else state.repeatWeekdays.split(",").map { it.toInt() }
						WeekDaysComponent(
							defaultRepeatedDays = repeatDays,
							onChange = { onAction(AddEditAction.UpdateRepeatWeekDays(it)) }
						)
					}
				}
				Divider(
					modifier = Modifier.padding(bottom = 8.dp),
					color = MaterialTheme.colorScheme.primaryContainer
				)
				PriorityComponent(defaultSortTask = state.priority) {
					onAction(AddEditAction.UpdatePriority(it))
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
						val task = state.toTask()
						val (isValid, errorMessage) = checkValidTask(
							task = task,
							isTaskAllDay = state.isAllDay,
							totalTasksDuration = appState.totalTaskDuration - task.getDuration(
								checkPastTask = true
							)
						)

						if (isValid) {
							onAction(AddEditAction.UpdateTask)
						} else {
							showCustomSnackbar(errorMessage)
						}
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primary,
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
		EditTaskScreen(
			state = AddEditState(),
			events = emptyFlow(),
			appState = MainState(),
			onAction = {},
			onBack = {}
		)
	}
}
