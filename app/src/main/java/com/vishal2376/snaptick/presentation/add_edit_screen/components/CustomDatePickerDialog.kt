package com.vishal2376.snaptick.presentation.add_edit_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.calender_screen.component.DaysOfWeekTitle
import com.vishal2376.snaptick.presentation.calender_screen.component.MonthDayComponent
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun CustomDatePickerDialog(onClose: (LocalDate) -> Unit) {
	val scope = rememberCoroutineScope()
	val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
	var selectedDay by remember { mutableStateOf(LocalDate.now()) }

	// monthly calender
	val currentMonth = remember { YearMonth.now() }
	val startMonth = remember { currentMonth }
	val endMonth = remember { currentMonth.plusMonths(12) }
	val monthState = rememberCalendarState(
		startMonth = startMonth,
		endMonth = endMonth,
		firstVisibleMonth = currentMonth,
		firstDayOfWeek = firstDayOfWeek
	)

	val dtf = DateTimeFormatter.ofPattern("d MMMM, yyyy")

	Dialog(onDismissRequest = { onClose(selectedDay) }) {
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.border(4.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp)),
			shape = RoundedCornerShape(16.dp),
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
		) {
			Column {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.background(MaterialTheme.colorScheme.secondary)
						.padding(16.dp),
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Text(text = stringResource(R.string.selected_date), style = h3TextStyle)
					Text(text = selectedDay.format(dtf), style = taskTextStyle)
				}

				Column(
					modifier = Modifier.padding(8.dp, 16.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					val monthTitle = monthState.lastVisibleMonth.yearMonth.month.getDisplayName(
						TextStyle.FULL,
						Locale.getDefault()
					)
					Row(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(4.dp)
					) {
						Icon(
							modifier = Modifier
								.size(32.dp)
								.clip(CircleShape)
								.clickable {
									scope.launch {
										monthState.animateScrollToMonth(
											monthState.firstVisibleMonth.yearMonth.minusMonths(
												1
											)
										)
									}
								}
								.padding(8.dp),
							imageVector = Icons.Default.ArrowBackIosNew,
							contentDescription = null
						)
						Text(text = monthTitle, style = h2TextStyle)
						Icon(
							modifier = Modifier
								.size(32.dp)
								.clip(CircleShape)
								.clickable {
									scope.launch {
										monthState.animateScrollToMonth(
											monthState.firstVisibleMonth.yearMonth.plusMonths(
												1
											)
										)
									}
								}
								.padding(8.dp),
							imageVector = Icons.Default.ArrowForwardIos,
							contentDescription = null
						)
					}
					HorizontalCalendar(
						state = monthState,
						dayContent = { day ->
							MonthDayComponent(
								day,
								selected = selectedDay == day.date,
								isDatePicker = true,
								indicator = false
							) {
								selectedDay = it
							}
						},
						monthHeader = { month ->
							val daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek }
							DaysOfWeekTitle(daysOfWeek = daysOfWeek)
						}
					)
					Text(
						modifier = Modifier
							.padding(16.dp, 8.dp)
							.clickable {
								onClose(selectedDay)
							}
							.align(Alignment.End),
						text = stringResource(R.string.done),
						style = h3TextStyle,
						color = Blue
					)
				}

			}
		}
	}
}

@Preview
@Composable
fun CustomDatePickerPreview() {
	SnaptickTheme {
		CustomDatePickerDialog({})
	}
}