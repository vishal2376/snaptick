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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.add_edit_screen.components.PriorityComponent
import com.vishal2376.snaptick.presentation.common.fontRoboto
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.Blue200
import com.vishal2376.snaptick.ui.theme.Green
import com.vishal2376.snaptick.ui.theme.LightGray
import com.vishal2376.snaptick.ui.theme.Red
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.ui.theme.Yellow
import kotlinx.coroutines.job
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
	onEvent: (AddEditScreenEvent) -> Unit,
	onClose: () -> Unit
) {

	var taskTitle by remember { mutableStateOf("") }
	var taskStartTime by remember { mutableStateOf(LocalTime.now()) }
	var taskEndTime by remember { mutableStateOf(LocalTime.now()) }
	var isTaskReminderOn by remember { mutableStateOf(true) }
	var taskCategory by remember { mutableStateOf("") }
	var taskPriority by remember { mutableIntStateOf(0) }

	val context = LocalContext.current
	val focusRequester = FocusRequester()

	Scaffold(topBar = {
		TopAppBar(
			modifier = Modifier.padding(8.dp),
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
				IconButton(onClick = { onClose() }) {
					Icon(
						imageVector = Icons.Rounded.ArrowBack,
						contentDescription = null
					)
				}
			},
		)
	}) { innerPadding ->

		LaunchedEffect(key1 = true,
			block = {
				coroutineContext.job.invokeOnCompletion {
					focusRequester.requestFocus()
				}
			})

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
					value = taskTitle,
					singleLine = true,
					colors = TextFieldDefaults.colors(
						focusedContainerColor = Blue200,
						unfocusedContainerColor = Blue200,
						disabledContainerColor = Blue200,
						cursorColor = Color.White,
					),
					textStyle = TextStyle.Default.copy(fontFamily = fontRoboto),
					onValueChange = {
						taskTitle = it
					},
					placeholder = { Text(text = stringResource(id = R.string.what_would_you_like_to_do)) },
					shape = RoundedCornerShape(16.dp),
					modifier = Modifier
						.focusRequester(focusRequester)
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
						.padding(top = 32.dp)
				) {
					Column(horizontalAlignment = Alignment.CenterHorizontally) {
						Text(
							text = "Start Time",
							style = taskTextStyle,
							color = Green
						)
						Spacer(modifier = Modifier.height(8.dp))
						WheelTimePicker(
							timeFormat = TimeFormat.AM_PM,
							minTime = LocalTime.now(),
							startTime = LocalTime.now(),
							textColor = Color.White
						) { snappedTime ->
							taskStartTime = snappedTime
						}
					}
					Column(horizontalAlignment = Alignment.CenterHorizontally) {
						Text(
							text = "End Time",
							style = taskTextStyle,
							color = Red
						)
						Spacer(modifier = Modifier.height(8.dp))
						WheelTimePicker(
							timeFormat = TimeFormat.AM_PM,
							textColor = Color.White,
							minTime = LocalTime.now().plusMinutes(5),
							startTime = LocalTime.now().plusHours(1)
						) { snappedTime ->
							taskEndTime = snappedTime
						}

					}
				}

				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(
							32.dp,
							24.dp
						),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = "Reminder",
						style = h2TextStyle,
						color = Color.White
					)

					Switch(
						checked = isTaskReminderOn,
						onCheckedChange = { isTaskReminderOn = it },
						colors = SwitchDefaults.colors(
							checkedThumbColor = Green,
							checkedTrackColor = Blue200,
							uncheckedTrackColor = Blue200
						)
					)
				}

				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(
							24.dp,
							0.dp
						),
					horizontalArrangement = Arrangement.spacedBy(10.dp)
				) {

					PriorityComponent(
						title = "Low",
						backgroundColor = LightGray,
						modifier = Modifier.weight(0.3f),
						onClick = { taskPriority = 0 }
					)

					PriorityComponent(
						title = "Medium",
						backgroundColor = Yellow,
						modifier = Modifier.weight(0.4f),
						onClick = { taskPriority = 1 }
					)

					PriorityComponent(
						title = "High",
						backgroundColor = Red,
						modifier = Modifier.weight(0.3f),
						onClick = { taskPriority = 2 }
					)
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
						if (taskTitle.isNotBlank()) {
							val task = Task(
								0,
								taskTitle,
								false,
								taskStartTime,
								taskEndTime,
								isTaskReminderOn,
								taskCategory,
								taskPriority
							)
							onEvent(AddEditScreenEvent.onAddTaskClick(task))
							onClose()
						} else if (taskStartTime >= taskEndTime) {
							Toast.makeText(
								context,
								"Invalid Time",
								Toast.LENGTH_SHORT
							).show()
						} else {
							Toast.makeText(
								context,
								"Title can't be empty",
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

@Preview
@Composable
fun AddTaskScreenPreview() {
	SnaptickTheme(
		darkTheme = true,
		dynamicColor = false
	) {
		AddTaskScreen(onEvent = {},
			{})
	}
}