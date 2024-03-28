package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskDescTextStyle
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SleepTimeOptionComponent(defaultSleepTime: LocalTime, onSelect: (LocalTime) -> Unit) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		val minSleepTime = LocalTime.of(21, 0)
		val dtf = DateTimeFormatter.ofPattern("hh:mm a")

		Text(
			text = stringResource(R.string.set_sleep_time),
			style = h1TextStyle,
			color = MaterialTheme.colorScheme.onPrimary,
		)
		Text(
			text = stringResource(
				R.string.min_max,
				minSleepTime.format(dtf),
				LocalTime.of(23, 59).format(dtf)
			),
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onSecondary,
		)
		Spacer(modifier = Modifier.height(16.dp))
		WheelTimePicker(
			timeFormat = TimeFormat.AM_PM,
			startTime = defaultSleepTime,
			minTime = minSleepTime,
			maxTime = LocalTime.MAX,
			textColor = MaterialTheme.colorScheme.onPrimary,
			onSnappedTime = {
				val sleepTime = LocalTime.of(it.hour, it.minute)
				onSelect(sleepTime)
			}
		)
	}
}

@Preview
@Composable
fun SleepTimeOptionComponentPreview() {
	SleepTimeOptionComponent(defaultSleepTime = LocalTime.MAX, onSelect = {})
}