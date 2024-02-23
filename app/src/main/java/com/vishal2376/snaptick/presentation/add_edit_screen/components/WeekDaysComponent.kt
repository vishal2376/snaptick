package com.vishal2376.snaptick.presentation.add_edit_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.ui.theme.Green
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

@Composable
fun WeekDaysComponent() {
	val defaultRepeatedDays = mutableListOf(0, 1, 0, 1, 0, 0, 1)
	val weekDays = listOf("M", "T", "W", "T", "F", "S", "S")

	val repeatedDays = defaultRepeatedDays

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(32.dp, 8.dp),
		horizontalArrangement = Arrangement.SpaceBetween
	) {
		weekDays.forEachIndexed { index, day ->
			WeekDaysItemComponent(title = day, isSelected = (repeatedDays[index] == 1))
		}
	}
}

@Composable
fun WeekDaysItemComponent(title: String, isSelected: Boolean) {

	var bgColor = MaterialTheme.colorScheme.primary
	var textColor = Color.White
	var borderWidth = 2.dp

	if (isSelected) {
		bgColor = Green
		textColor = Color.Black
		borderWidth = 0.dp
	}

	Box(
		modifier = Modifier
			.background(bgColor, CircleShape)
			.border(borderWidth, MaterialTheme.colorScheme.secondary, CircleShape)
			.size(32.dp),
		contentAlignment = Alignment.Center
	) {
		Text(text = title, color = textColor, style = infoDescTextStyle)
	}
}

@Preview
@Composable
fun WeekDaysComponentPreview() {
	SnaptickTheme {
		WeekDaysComponent()
	}
}