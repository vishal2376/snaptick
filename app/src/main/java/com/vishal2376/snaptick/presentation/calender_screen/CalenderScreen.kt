package com.vishal2376.snaptick.presentation.calender_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.calender_screen.component.DaysOfWeekTitle
import com.vishal2376.snaptick.presentation.calender_screen.component.MonthDayComponent
import com.vishal2376.snaptick.presentation.calender_screen.component.WeekDayComponent
import com.vishal2376.snaptick.presentation.common.SnackbarController.showCustomSnackbar
import com.vishal2376.snaptick.presentation.common.filterTasksByDate
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.home_screen.HomeScreenEvent
import com.vishal2376.snaptick.presentation.home_screen.components.EmptyTaskComponent
import com.vishal2376.snaptick.presentation.home_screen.components.TaskComponent
import com.vishal2376.snaptick.presentation.main.MainEvent
import com.vishal2376.snaptick.presentation.navigation.Routes
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.DummyTasks
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CalenderScreen(
	tasks: List<Task>,
	onEvent: (HomeScreenEvent) -> Unit,
	onMainEvent: (MainEvent) -> Unit,
	onNavigate: (route: String) -> Unit,
	onBack: () -> Unit
) {
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
	val weekState = rememberWeekCalendarState(
		startDate = startDate,
		endDate = endDate,
		firstDayOfWeek = firstDayOfWeek
	)

	var selectedDay by remember { mutableStateOf<LocalDate>(currentDate) }
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
					text = currentMonthTitle.getDisplayName(
						TextStyle.FULL,
						Locale.getDefault()
					),
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
						selectedDay = currentDate
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
	},
		floatingActionButton = {
			if (selectedDay >= LocalDate.now()) {
				FloatingActionButton(
					onClick = {
						onMainEvent(MainEvent.UpdateCalenderDate(selectedDay))
						onNavigate(Routes.AddTaskScreen.name)
					},
					containerColor = Blue,
					contentColor = MaterialTheme.colorScheme.secondary
				) {
					Icon(
						imageVector = Icons.Default.Add,
						contentDescription = null
					)
				}
			}
		}) { innerPadding ->

		Column(modifier = Modifier.padding(innerPadding)) {

			AnimatedVisibility(visible = isWeekCalender) {
				WeekCalendar(
					modifier = Modifier.padding(horizontal = 10.dp),
					state = weekState,
					dayContent = { day ->
						WeekDayComponent(
							day,
							selected = selectedDay == day.date,
							indicator = filterTasksByDate(tasks, day.date).isNotEmpty()
						) {
							selectedDay = it
						}
					},
				)
			}

			AnimatedVisibility(visible = !isWeekCalender) {
				HorizontalCalendar(
					modifier = Modifier.padding(horizontal = 10.dp),
					state = monthState,
					dayContent = { day ->
						MonthDayComponent(
							day,
							selected = selectedDay == day.date,
							indicator = filterTasksByDate(tasks, day.date).isNotEmpty()
						) {
							selectedDay = it
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

			val selectedDayTasks = filterTasksByDate(tasks, selectedDay)
			if (selectedDayTasks.isEmpty()) {
				EmptyTaskComponent()
			} else {
				LazyColumn(
					modifier = Modifier
						.fillMaxSize()
						.padding(16.dp, 0.dp)
				) {
					itemsIndexed(items = selectedDayTasks,
						key = { index, task ->
							task.id
						}) { index, task ->
						Box(
							modifier = Modifier.animateItemPlacement(tween(500))
						) {
							TaskComponent(
								task = task,
								onEdit = { taskId ->
									if (task.date >= LocalDate.now()) {
										onEvent(HomeScreenEvent.OnEditTask(taskId))
										onNavigate("${Routes.EditTaskScreen.name}/$taskId")
									}
								},
								onComplete = {
									if (task.date >= LocalDate.now()) {
										onEvent(HomeScreenEvent.OnCompleted(it, !task.isCompleted))
									}
								},
								onPomodoro = { taskId ->
									onEvent(HomeScreenEvent.OnPomodoro(taskId))
									onNavigate("${Routes.PomodoroScreen.name}/$taskId")
								},
								onDelete = {
									onEvent(HomeScreenEvent.OnDeleteTask(it))
									showCustomSnackbar(
										msg = "Task Deleted",
										actionText = "Undo",
										onClickAction = {
											onEvent(HomeScreenEvent.OnUndoDelete)
										})
								},
								animDelay = index * Constants.LIST_ANIMATION_DELAY
							)
						}
						Spacer(modifier = Modifier.height(10.dp))
					}
				}
			}

		}
	}
}


@Preview
@Composable
fun CalenderScreenPreview() {
	SnaptickTheme {
		val tasks = DummyTasks.dummyTasks
		CalenderScreen(tasks, {}, {}, {}, {})
	}
}