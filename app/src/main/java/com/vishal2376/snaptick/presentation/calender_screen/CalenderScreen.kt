package com.vishal2376.snaptick.presentation.calender_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.calender_screen.component.WeekDayComponent
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalenderScreen(onBack: () -> Unit) {
	val currentDate = remember { LocalDate.now() }
	val startDate = remember { currentDate.minusDays(200) }
	val endDate = remember { currentDate.plusDays(200) }
	var selection by remember { mutableStateOf<LocalDate>(currentDate) }

	val state = rememberWeekCalendarState(
		startDate = startDate,
		endDate = endDate,
		firstDayOfWeek = DayOfWeek.MONDAY
	)

	Scaffold(topBar = {
		TopAppBar(
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = MaterialTheme.colorScheme.background,
			),
			title = {
				Text(
					text = stringResource(R.string.calender),
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
		)
	}) { innerPadding ->

		Column(modifier = Modifier.padding(innerPadding)) {

			WeekCalendar(
				modifier = Modifier.padding(horizontal = 10.dp),
				state = state,
				dayContent = { day ->
					WeekDayComponent(day.date, selected = selection == day.date) {
						selection = it
					}
				},
			)

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