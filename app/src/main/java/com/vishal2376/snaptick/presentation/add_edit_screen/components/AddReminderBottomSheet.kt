package com.vishal2376.snaptick.presentation.add_edit_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.domain.model.Reminder
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.Blue200
import com.vishal2376.snaptick.ui.theme.Blue500
import com.vishal2376.snaptick.ui.theme.Green
import com.vishal2376.snaptick.ui.theme.LightGray
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderBottomSheet() {

	var checkValue by remember {
		mutableStateOf(false)
	}

	val availableReminder = remember {
		mutableStateListOf(
			Reminder(5),
			Reminder(10),
			Reminder(15),
			Reminder(30),
		)
	}

	ModalBottomSheet(
		onDismissRequest = { /*TODO*/ },
		containerColor = Blue200
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					32.dp,
					0.dp
				),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(24.dp)
		) {
			Text(
				text = "Add Reminder",
				color = Color.White,
				style = h2TextStyle
			)

			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

				for (reminder in availableReminder) {
					ReminderCheckbox(
						reminder.time,
						reminder.isTurnedOn
					)
				}
			}

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Button(
					onClick = { /*TODO*/ },
					colors = ButtonDefaults.buttonColors(containerColor = Blue500),
					shape = RoundedCornerShape(8.dp),
					contentPadding = PaddingValues(
						40.dp,
						8.dp
					),
					border = BorderStroke(
						2.dp,
						LightGray
					)
				) {
					Text(
						text = "Cancel",
						color = LightGray
					)
				}
				Button(
					onClick = { /*TODO*/ },
					colors = ButtonDefaults.buttonColors(containerColor = Green),
					contentPadding = PaddingValues(
						40.dp,
						8.dp
					),
					shape = RoundedCornerShape(8.dp),
				) {
					Text(
						text = "Done",
						color = Color.Black
					)
				}

			}
			Spacer(modifier = Modifier.height(8.dp))
		}
	}
}

@Composable
private fun ReminderCheckbox(time: Int, isTurnedOn: Boolean) {
	var checkValue by remember {
		mutableStateOf(isTurnedOn)
	}
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				Blue500,
				RoundedCornerShape(8.dp)
			)
			.padding(
				8.dp,
				0.dp
			),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.Start
	) {
		Checkbox(
			checked = checkValue,
			colors = CheckboxDefaults.colors(checkedColor = Green),
			onCheckedChange = { checkValue = it }
		)

		Text(
			text = "$time minutes before",
			style = taskTextStyle,
			color = LightGray
		)
	}
}

@Preview
@Composable
fun AddReminderBottomSheetPreview() {
	SnaptickTheme(
		darkTheme = true,
		dynamicColor = false
	) {
		AddReminderBottomSheet()
	}
}