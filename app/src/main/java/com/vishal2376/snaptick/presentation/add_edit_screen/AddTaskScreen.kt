package com.vishal2376.snaptick.presentation.add_edit_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Today
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import com.vishal2376.snaptick.presentation.add_edit_screen.components.CustomDatePickerDialog
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
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.main.MainState
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.DarkGreen
import com.vishal2376.snaptick.ui.theme.LightGreen
import com.vishal2376.snaptick.ui.theme.Red
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.util.checkValidTask
import com.vishal2376.snaptick.util.getFormattedDuration
import kotlinx.coroutines.job
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
	appState: MainState,
	onEvent: (AddEditScreenEvent) -> Unit,
	onBack: () -> Unit
) {

	var taskTitle by remember { mutableStateOf("") }
	var taskStartTime by remember { mutableStateOf(LocalTime.now()) }
	var taskEndTime by remember { mutableStateOf(LocalTime.now().plusHours(1)) }
	var taskDate by remember { mutableStateOf(LocalDate.now()) }
	var isTaskReminderOn by remember { mutableStateOf(true) }
	var isTaskRepeated by remember { mutableStateOf(false) }
	var repeatedWeekDays by remember { mutableStateOf("") }
	var taskPriority by remember { mutableStateOf(Priority.LOW) }
	val taskDuration by remember { mutableLongStateOf(60) }
	var isTimeUpdated by remember { mutableStateOf(false) }

	val context = LocalContext.current
	val focusRequester = FocusRequester()

	var showDialogCustomDuration by remember { mutableStateOf(false) }
	var showDialogDatePicker by remember { mutableStateOf(false) }

	Scaffold(topBar = {
		TopAppBar(
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = MaterialTheme.colorScheme.background,
			),
			title = {
				Text(
					text = stringResource(R.string.add_task),
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
					modifier = Modifier
						.clickable { showDialogDatePicker = true }
						.border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
						.padding(top = 8.dp, bottom = 8.dp, end = 10.dp, start = 8.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					val dtf = DateTimeFormatter.ofPattern("d MMMM")
					Icon(imageVector = Icons.Default.Today, contentDescription = null)
					Text(
						text = taskDate.format(dtf),
						style = h3TextStyle
					)
				}
				Spacer(modifier = Modifier.width(8.dp))
			}
		)
	}) { innerPadding ->

		LaunchedEffect(key1 = true,
			block = {
				coroutineContext.job.invokeOnCompletion {
					focusRequester.requestFocus()
				}
			})


		// confirm delete dialog
		if (showDialogCustomDuration) {
			CustomDurationDialogComponent(
				onClose = { showDialogCustomDuration = false },
				onSelect = { time ->
					val duration = time.toSecondOfDay() / 60L
					taskEndTime = taskStartTime.plusMinutes(duration)
					isTimeUpdated = !isTimeUpdated
				}
			)
		}

		if (showDialogDatePicker) {
			CustomDatePickerDialog(
				defaultDay = taskDate, onClose = { day ->
					taskDate = day
					showDialogDatePicker = false
				})
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
					value = taskTitle,
					singleLine = true,
					colors = TextFieldDefaults.colors(
						focusedContainerColor = MaterialTheme.colorScheme.secondary,
						unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
						disabledContainerColor = MaterialTheme.colorScheme.secondary,
						unfocusedIndicatorColor = Color.Transparent,
						focusedIndicatorColor = Color.Transparent,
						cursorColor = MaterialTheme.colorScheme.onPrimary,
					),
					textStyle = taskTextStyle,
					onValueChange = {
						taskTitle = it
					},
					placeholder = {
						Text(
							text = stringResource(id = R.string.what_would_you_like_to_do),
							color = MaterialTheme.colorScheme.onSecondary
						)
					},
					shape = RoundedCornerShape(16.dp),
					modifier = Modifier
						.focusRequester(focusRequester)
						.fillMaxWidth()
						.padding(32.dp, 8.dp),
					keyboardOptions = KeyboardOptions(
						capitalization = KeyboardCapitalization.Sentences,
						imeAction = ImeAction.Done
					)
				)
				Row(
					horizontalArrangement = Arrangement.SpaceAround,
					modifier = Modifier
						.fillMaxWidth()
						.padding(24.dp, 0.dp)
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
							taskEndTime = snappedTime
						}
					}
				}

				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(start = 30.dp, end = 32.dp, top = 8.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Text(
						text = stringResource(R.string.duration),
						style = h2TextStyle,
						color = MaterialTheme.colorScheme.onPrimary
					)

					Text(
						text = getFormattedDuration(taskStartTime, taskEndTime),
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
						taskEndTime = taskStartTime.plusMinutes(duration)
						isTimeUpdated = !isTimeUpdated
					}
				}

				Column {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(24.dp, 0.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						Icon(imageVector = Icons.Default.Notifications, contentDescription = null)
						Text(
							modifier = Modifier
								.weight(1f)
								.padding(start = 4.dp),
							text = stringResource(R.string.reminder),
							style = h2TextStyle,
							color = MaterialTheme.colorScheme.onPrimary
						)

						Switch(
							checked = isTaskReminderOn,
							onCheckedChange = { isTaskReminderOn = it },
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
							color = MaterialTheme.colorScheme.onPrimary
						)

						Switch(
							checked = isTaskRepeated,
							onCheckedChange = { isTaskRepeated = it },
							colors = SwitchDefaults.colors(
								checkedThumbColor = Blue,
								checkedTrackColor = MaterialTheme.colorScheme.secondary,
								uncheckedTrackColor = MaterialTheme.colorScheme.secondary
							)
						)
					}

					AnimatedVisibility(visible = isTaskRepeated) {
						val dayOfWeek = LocalDate.now().dayOfWeek.value - 1
						WeekDaysComponent(
							defaultRepeatedDays = listOf(dayOfWeek),
							onChange = { repeatedWeekDays = it }
						)
					}


				}
				PriorityComponent() {
					taskPriority = it
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
						val task = Task(
							id = 0,
							uuid = UUID.randomUUID().toString(),
							title = taskTitle.trim(),
							isCompleted = false,
							startTime = taskStartTime,
							endTime = taskEndTime,
							reminder = isTaskReminderOn,
							isRepeated = isTaskRepeated,
							repeatWeekdays = repeatedWeekDays,
							pomodoroTimer = -1,
							date = taskDate,
							priority = taskPriority.ordinal
						)

						val (isValid, errorMessage) = checkValidTask(
							task = task,
							totalTasksDuration = appState.totalTaskDuration
						)

						if (isValid) {
							onEvent(AddEditScreenEvent.OnAddTaskClick(task))
							showCustomSnackbar(
								context.getString(R.string.tasks_added_successfully),
								actionColor = LightGreen
							)
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
						text = stringResource(R.string.add_task),
						fontWeight = FontWeight.Bold,
						fontSize = 16.sp,
						modifier = Modifier.padding(8.dp)
					)
				}
			}
		}
	}
}

@Preview()
@Composable
fun AddTaskScreenPreview() {
	SnaptickTheme {
		AddTaskScreen(MainState(), onEvent = {}, {})
	}
}