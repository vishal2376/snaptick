package com.vishal2376.snaptick.presentation.calender_screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.Black500
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.DarkGray
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthDayComponent(
	day: CalendarDay,
	selected: Boolean,
	onClick: (LocalDate) -> Unit = {}
) {
	val textColor = if (selected) {
		Black500
	} else {
		if (day.position == DayPosition.MonthDate)
			MaterialTheme.colorScheme.onPrimary
		else
			DarkGray
	}

	Box(
		modifier = Modifier
			.aspectRatio(1f)
			.padding(6.dp)
			.clip(RoundedCornerShape(8.dp))
			.background(if (selected) Blue else Color.Transparent)
			.border(
				if (day.date == LocalDate.now()) 2.dp else (-1).dp,
				Blue,
				RoundedCornerShape(8.dp)
			)
			.clickable { onClick(day.date) },
		contentAlignment = Alignment.Center
	) {
		Text(
			text = day.date.dayOfMonth.toString(),
			style = taskTextStyle,
			color = textColor
		)
	}
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
	Row(modifier = Modifier.fillMaxWidth()) {
		for (dayOfWeek in daysOfWeek) {
			Text(
				modifier = Modifier.weight(1f),
				textAlign = TextAlign.Center,
				style = infoDescTextStyle,
				color = MaterialTheme.colorScheme.onSecondary,
				text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
			)
		}
	}
}

@Preview
@Composable
fun MonthDayComponentPreview() {
	MonthDayComponent(CalendarDay(LocalDate.now(), DayPosition.MonthDate), true)
}