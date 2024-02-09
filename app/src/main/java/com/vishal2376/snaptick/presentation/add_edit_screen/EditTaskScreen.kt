package com.vishal2376.snaptick.presentation.add_edit_screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.getValue
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
import com.vishal2376.snaptick.presentation.add_edit_screen.components.PriorityComponent
import com.vishal2376.snaptick.presentation.common.ShowTimePicker
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.home_screen.components.DurationComponent
import com.vishal2376.snaptick.presentation.main.MainState
import com.vishal2376.snaptick.ui.theme.Green
import com.vishal2376.snaptick.ui.theme.Red
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.util.Priority
import com.vishal2376.snaptick.util.checkValidTask
import java.time.LocalDate
import java.time.LocalTime

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

	var showDialog by remember { mutableStateOf(false) }

	Scaffold(topBar = {
		TopAppBar(modifier = Modifier.padding(8.dp),
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
				IconButton(onClick = { showDialog = true }) {
					Icon(
						imageVector = Icons.Default.Delete,
						contentDescription = null
					)
				}
			})
	}) { innerPadding ->


		// confirm delete dialog
		if (showDialog) {
			ConfirmDeleteDialog(
				onClose = { showDialog = false },
				onDelete = {
					onEvent(AddEditScreenEvent.OnDeleteTaskClick(task))
					showDialog = false
					onBack()
				}
			)
		}

		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding),
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
						cursorColor = Color.White
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
						.padding(top = 24.dp)
				) {
					Column(horizontalAlignment = Alignment.CenterHorizontally) {
						Text(
							text = "Start Time",
							style = taskTextStyle,
							color = Green
						)
						Spacer(modifier = Modifier.height(8.dp))
						ShowTimePicker(
							time = taskStartTime,
							isTimeUpdated = isTimeUpdated
						) { snappedTime ->
							onEvent(AddEditScreenEvent.OnUpdateStartTime(snappedTime))
						}
					}
					Column(horizontalAlignment = Alignment.CenterHorizontally) {
						Text(
							text = "End Time",
							style = taskTextStyle,
							color = Red
						)
						Spacer(modifier = Modifier.height(8.dp))
						ShowTimePicker(
							time = taskEndTime,
							isTimeUpdated = isTimeUpdated
						) { snappedTime ->
							onEvent(AddEditScreenEvent.OnUpdateEndTime(snappedTime))
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
						text = "Duration",
						style = h2TextStyle,
						color = Color.White
					)

					Text(
						text = task.getFormattedDuration(),
						style = taskTextStyle,
						color = Color.White
					)
				}
				DurationComponent(
					modifier = Modifier
						.padding(horizontal = 24.dp),
					durationList = appState.durationList
				) { duration ->
					onEvent(AddEditScreenEvent.OnUpdateEndTime(task.startTime.plusMinutes(duration)))
					taskEndTime = taskStartTime.plusMinutes(duration)
					isTimeUpdated = !isTimeUpdated
				}
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(32.dp, 0.dp),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = "Reminder",
						style = h2TextStyle,
						color = Color.White
					)

					Switch(
						checked = task.reminder,
						onCheckedChange = {
							onEvent(AddEditScreenEvent.OnUpdateReminder(it))
						},
						colors = SwitchDefaults.colors(
							checkedThumbColor = Green,
							checkedTrackColor = MaterialTheme.colorScheme.secondary,
							uncheckedTrackColor = MaterialTheme.colorScheme.secondary
						)
					)
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
							totalTasksDuration = appState.totalTaskDuration,
							isOptional = true
						)

						if (isValid) {
							onEvent(AddEditScreenEvent.OnUpdateTask())
							onBack()
						} else {
							Toast.makeText(
								context,
								errorMessage,
								Toast.LENGTH_SHORT
							).show()
						}
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = Green,
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
	SnaptickTheme(
		darkTheme = true,
		dynamicColor = false
	) {
		val task = Task(
			id = 1,
			title = "Learn Kotlin",
			isCompleted = false,
			startTime = LocalTime.now(),
			endTime = LocalTime.now().plusHours(1),
			reminder = true,
			date = LocalDate.now(),
			priority = 0
		)
		EditTaskScreen(task, MainState(), {}, {})
	}
}