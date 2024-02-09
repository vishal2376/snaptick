package com.vishal2376.snaptick.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import java.time.LocalTime

@Composable
fun ShowTimePicker(
	time: LocalTime,
	isTimeUpdated: Boolean = false,
	onSelect: (LocalTime) -> Unit
) {
	if (isTimeUpdated) {
		WheelTimePicker(
			timeFormat = TimeFormat.AM_PM,
			startTime = time,
			textColor = Color.White,
			onSnappedTime = onSelect
		)
	} else {
		WheelTimePicker(
			timeFormat = TimeFormat.AM_PM,
			startTime = time,
			textColor = Color.White,
			onSnappedTime = onSelect
		)
	}
}