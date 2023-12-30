package com.vishal2376.snaptick.presentation.home_screen.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.TaskViewModel
import com.vishal2376.snaptick.presentation.common.fontRoboto
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.Blue200
import com.vishal2376.snaptick.ui.theme.Blue500
import com.vishal2376.snaptick.ui.theme.Green
import com.vishal2376.snaptick.ui.theme.Red
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetComponent(
	showBottomSheet: Boolean,
	onClose: () -> Unit,
	taskViewModel: TaskViewModel
) {
	val sheetState = rememberModalBottomSheetState()
	val scope = rememberCoroutineScope()

	val context = LocalContext.current
	val focusRequester = FocusRequester()

	var taskTitle by remember { mutableStateOf("") }
	var taskStartTime by remember { mutableLongStateOf(0) }
	var taskEndTime by remember { mutableLongStateOf(0) }

	val task = Task(
		0,
		taskTitle,
		false,
		taskStartTime,
		taskEndTime
	)

	if (showBottomSheet) {
		ModalBottomSheet(
			containerColor = Blue500,
			contentColor = Color.White,
			onDismissRequest = {
				onClose()
				taskTitle = ""
			},
			sheetState = sheetState
		) {
			LaunchedEffect(key1 = true,
			               block = {
				               coroutineContext.job.invokeOnCompletion {
					               focusRequester.requestFocus()
				               }
			               })

			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(16.dp)
			) {
				Text(
					text = "What would you like to do ?",
					style = h2TextStyle
				)
				TextField(
					value = taskTitle,
					singleLine = true,
					colors = TextFieldDefaults.textFieldColors(
						containerColor = Blue200,
						cursorColor = Color.White
					),
					textStyle = TextStyle.Default.copy(fontFamily = fontRoboto),
					onValueChange = {
						taskTitle = it
					},
					placeholder = { Text(text = "e.g Water the Plants") },
					shape = RoundedCornerShape(16.dp),
					modifier = Modifier.focusRequester(focusRequester),
					keyboardOptions = KeyboardOptions(
						capitalization = KeyboardCapitalization.Sentences,
						imeAction = ImeAction.Next
					)
				)

				Row(
					horizontalArrangement = Arrangement.SpaceBetween,
					modifier = Modifier.fillMaxWidth(.75f)
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
							startTime = LocalTime.now()
						) { snappedTime ->
							taskStartTime = snappedTime.toNanoOfDay()
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
							minTime = LocalTime.now().plusMinutes(5),
							startTime = LocalTime.now().plusHours(1)
						) { snappedTime ->
							taskEndTime = snappedTime.toNanoOfDay()
						}

					}
				}

				Button(
					onClick = {
						if (taskTitle.isNotBlank() && taskStartTime < taskEndTime) {
							taskViewModel.insertTask(task)
							taskTitle = ""

							scope.launch { sheetState.hide() }.invokeOnCompletion {
								if (!sheetState.isVisible) {
									onClose()
								}
							}
						} else {
							Toast.makeText(
								context,
								"Task Empty!!!",
								Toast.LENGTH_SHORT
							).show()
						}
					},
					colors = ButtonDefaults.buttonColors(
						containerColor = Blue200,
						contentColor = Color.White
					),
					shape = RoundedCornerShape(8.dp)
				) {
					Text(text = "Add Task")
				}
				Spacer(modifier = Modifier.height(8.dp))
			}
		}
	}

}