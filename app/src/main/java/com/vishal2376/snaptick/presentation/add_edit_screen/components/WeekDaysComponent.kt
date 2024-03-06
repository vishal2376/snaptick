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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

@Composable
fun WeekDaysComponent(
	defaultRepeatedDays: List<Int> = listOf(0),
	onChange: (String) -> Unit
) {
	val weekDays = listOf("M", "T", "W", "T", "F", "S", "S")
	var selectedDays by remember { mutableStateOf(defaultRepeatedDays) }

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(24.dp, 8.dp),
		horizontalArrangement = Arrangement.SpaceAround
	) {
		weekDays.forEachIndexed { index, day ->
			WeekDaysItemComponent(
				title = day,
				isSelected = selectedDays.contains(index)
			) { isChecked ->
				selectedDays = if (isChecked) {
					selectedDays + index
				} else {
					if (selectedDays.size > 1) {
						selectedDays - index
					} else {
						selectedDays
					}
				}
				onChange(selectedDays.joinToString(separator = ","))
			}
		}
	}
}

@Composable
fun WeekDaysItemComponent(title: String, isSelected: Boolean, onChange: (Boolean) -> Unit) {

	var bgColor = MaterialTheme.colorScheme.primary
	var textColor = MaterialTheme.colorScheme.onPrimary
	var borderWidth = 2.dp

	if (isSelected) {
		bgColor = Blue
		textColor = Color.Black
		borderWidth = 0.dp
	}

	Box(
		modifier = Modifier
			.size(32.dp)
			.background(bgColor, CircleShape)
			.border(borderWidth, MaterialTheme.colorScheme.secondary, CircleShape),
		contentAlignment = Alignment.Center
	) {
		Checkbox(modifier = Modifier.alpha(0f),
			checked = isSelected,
			onCheckedChange = { onChange(it) }
		)
		Text(text = title, color = textColor, style = infoDescTextStyle)
	}
}

@Preview()
@Composable
fun WeekDaysComponentPreview() {
	SnaptickTheme {
		WeekDaysComponent(listOf(2, 3), {})
	}
}