package com.vishal2376.snaptick.presentation.calender_screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.infoTextStyle
import com.vishal2376.snaptick.presentation.common.taskDescTextStyle
import com.vishal2376.snaptick.ui.theme.Black500
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val dateFormatter = DateTimeFormatter.ofPattern("dd")

@Composable
fun WeekDayComponent(
	date: LocalDate,
	selected: Boolean = false,
	onClick: (LocalDate) -> Unit = {},
) {
	val configuration = LocalConfiguration.current
	val screenWidth = configuration.screenWidthDp.dp
	Box(
		modifier = Modifier
			.width(screenWidth / 7)
			.padding(4.dp)
			.clip(RoundedCornerShape(16.dp))
			.background(if (selected) Blue else MaterialTheme.colorScheme.secondary)
			.border(if (date == LocalDate.now()) 2.dp else (-1).dp, Blue, RoundedCornerShape(16.dp))
			.clickable { onClick(date) },
		contentAlignment = Alignment.Center,
	) {
		Column(
			modifier = Modifier.padding(bottom = 10.dp, top = 6.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Text(
				text = dateFormatter.format(date),
				style = infoTextStyle,
				color = if (selected) Black500 else MaterialTheme.colorScheme.onPrimary
			)
			Text(
				text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
				color = if (selected) Black500 else MaterialTheme.colorScheme.onPrimary,
				style = taskDescTextStyle,
			)
		}
	}
}

@Preview
@Composable
fun WeekDayComponentPreview() {
	SnaptickTheme {
		WeekDayComponent(date = LocalDate.now())
	}
}