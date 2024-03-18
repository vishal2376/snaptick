package com.vishal2376.snaptick.presentation.calender_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.vishal2376.snaptick.presentation.calender_screen.component.DaysOfWeekTitle
import com.vishal2376.snaptick.presentation.calender_screen.component.MonthDayComponent
import com.vishal2376.snaptick.presentation.calender_screen.component.WeekDayComponent
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalenderScreen(onBack: () -> Unit) {
	val scope = rememberCoroutineScope()
	var isWeekCalender by remember { mutableStateOf(false) }
	val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

	// monthly calender
	val currentMonth = remember { YearMonth.now() }
	val startMonth = remember { currentMonth.minusMonths(50) }
	val endMonth = remember { currentMonth.plusMonths(50) }
	val monthState = rememberCalendarState(
		startMonth = startMonth,
		endMonth = endMonth,
		firstVisibleMonth = currentMonth,
		firstDayOfWeek = firstDayOfWeek
	)

	// weekly calender
	val currentDate = remember { LocalDate.now() }
	val startDate = remember { currentDate.minusDays(100) }
	val endDate = remember { currentDate.plusDays(100) }
	var selection by remember { mutableStateOf<LocalDate>(currentDate) }
	val weekState = rememberWeekCalendarState(
		startDate = startDate,
		endDate = endDate,
		firstDayOfWeek = firstDayOfWeek
	)

	var currentMonthTitle by remember { mutableStateOf(currentMonth.month) }
	currentMonthTitle = if (isWeekCalender) weekState.firstVisibleWeek.days[0].date.month
	else monthState.lastVisibleMonth.yearMonth.month

	Scaffold(topBar = {
		TopAppBar(
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = MaterialTheme.colorScheme.background,
			),
			title = {
				Text(
					text = currentMonthTitle.getDisplayName(TextStyle.FULL, Locale.getDefault()),
					style = h1TextStyle
				)
			},
			navigationIcon = {
				IconButton(onClick = { onBack() }) {
					Icon(
						imageVector = Icons.Rounded.ArrowBack,
						contentDescription = null
					)
				}
			},
			actions = {
				IconButton(onClick = {
					scope.launch {
						selection = currentDate
						if (isWeekCalender)
							weekState.animateScrollToWeek(currentDate)
						else
							monthState.animateScrollToMonth(currentMonth)
					}
				}) {
					Icon(imageVector = Icons.Default.Restore, contentDescription = null)
				}

				IconButton(onClick = { isWeekCalender = !isWeekCalender }) {
					val currentIcon =
						if (isWeekCalender) Icons.Default.CalendarMonth else Icons.Default.ViewWeek
					Icon(imageVector = currentIcon, contentDescription = null)
				}
			}
		)
	}) { innerPadding ->

		Column(modifier = Modifier.padding(innerPadding)) {

			AnimatedVisibility(visible = isWeekCalender) {
				WeekCalendar(
					modifier = Modifier.padding(horizontal = 10.dp),
					state = weekState,
					dayContent = { day ->
						WeekDayComponent(day, selected = selection == day.date) {
							selection = it
						}
					},
				)
			}

			AnimatedVisibility(visible = !isWeekCalender) {
				HorizontalCalendar(
					modifier = Modifier.padding(horizontal = 10.dp),
					state = monthState,
					dayContent = { day ->
						MonthDayComponent(day, selected = selection == day.date) {
							selection = it
						}
					},
					monthHeader = { month ->
						val daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek }
						DaysOfWeekTitle(daysOfWeek = daysOfWeek)
					}
				)
			}

			Divider(
				modifier = Modifier.padding(vertical = 24.dp),
				thickness = 2.dp,
				color = MaterialTheme.colorScheme.secondary
			)
		}
	}
}


@Preview
@Composable
fun CalenderScreenPreview() {
	SnaptickTheme {
		CalenderScreen({})
	}
}