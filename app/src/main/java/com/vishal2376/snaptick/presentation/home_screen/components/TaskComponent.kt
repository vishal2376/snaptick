package com.vishal2376.snaptick.presentation.home_screen.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.taskDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.DarkGreen
import com.vishal2376.snaptick.ui.theme.LightGreen
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.ui.theme.priorityColors
import com.vishal2376.snaptick.util.DummyTasks
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskComponent(
	task: Task,
	onEdit: (Int) -> Unit,
	onComplete: (Int) -> Unit,
	onPomodoro: (Int) -> Unit,
	onDelete: (Int) -> Unit = {},
	animDelay: Int = 100,
	is24HourTimeFormat: Boolean = false
) {

	val alphaAnimation = remember { Animatable(initialValue = 0f) }

	LaunchedEffect(animDelay) {
		alphaAnimation.animateTo(targetValue = 1f, animationSpec = tween(1000, animDelay))
	}

	Box(
		modifier = Modifier
			.graphicsLayer {
				alpha = alphaAnimation.value
			}
			.fillMaxWidth()
			.background(
				priorityColors[task.priority],
				RoundedCornerShape(
					topStart = 8.dp,
					bottomStart = 8.dp,
					topEnd = 20.dp,
					bottomEnd = 20.dp
				)
			)
			.padding(start = 10.dp)
			.clickable {
				onEdit(task.id)
			}
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.background(
					MaterialTheme.colorScheme.primaryContainer,
					RoundedCornerShape(
						topEnd = 8.dp,
						bottomEnd = 8.dp
					)
				)
				.padding(start = 8.dp, top = 10.dp, end = 8.dp, bottom = 16.dp)
		) {

			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				IconButton(
					onClick = { onComplete(task.id) },
					modifier = Modifier
						.size(32.dp)
						.weight(0.1f)
				) {
					if (task.isCompleted) {
						Icon(
							painter = painterResource(id = R.drawable.ic_check_circle),
							contentDescription = null,
							tint = if (isSystemInDarkTheme()) LightGreen else DarkGreen,
							modifier = Modifier.size(20.dp)
						)
					} else {
						Box(modifier = Modifier
							.size(20.dp)
							.border(
								width = 2.dp,
								color = MaterialTheme.colorScheme.onPrimaryContainer,
								shape = CircleShape
							),
							contentAlignment = Alignment.Center,
							content = {})
					}


				}
				Row(
					modifier = Modifier.weight(0.8f),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween
				) {

					Column(verticalArrangement = Arrangement.Center) {
						Text(
							modifier = Modifier
								.fillMaxWidth()
								.basicMarquee(delayMillis = 1000),
							text = task.title,
							style = taskTextStyle,
							fontWeight = FontWeight.Bold,
							color = MaterialTheme.colorScheme.onBackground
						)
						Spacer(modifier = Modifier.height(8.dp))
						Row(
							horizontalArrangement = Arrangement.spacedBy(4.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							Icon(
								painter = painterResource(id = R.drawable.ic_clock),
								contentDescription = null,
								modifier = Modifier.size(15.dp),
								tint = MaterialTheme.colorScheme.onPrimaryContainer

							)
							Text(
								text = if (task.isAllDayTaskEnabled()) {
									if (task.reminder) {
										task.getFormattedTime(is24HourTimeFormat).split("-")[0]
									} else {
										stringResource(R.string.all_day)
									}
								} else {
									task.getFormattedTime(is24HourTimeFormat)
								},
								style = taskDescTextStyle,
								color = MaterialTheme.colorScheme.onPrimaryContainer

							)
							if (task.reminder) {
								Icon(
									imageVector = Icons.Default.Notifications,
									contentDescription = null,
									modifier = Modifier.size(15.dp),
									tint = MaterialTheme.colorScheme.onPrimaryContainer

								)
							}
							if (task.isRepeated) {
								Icon(
									imageVector = Icons.Default.Refresh,
									contentDescription = null,
									modifier = Modifier.size(15.dp),
									tint = MaterialTheme.colorScheme.onPrimaryContainer

								)
							}
						}
						if (task.repeatWeekdays.isNotEmpty() && task.isRepeated) {
							val isDailyTask = task.getRepeatWeekList().size == 7
							Row(
								modifier = Modifier.padding(top = 2.dp),
								horizontalArrangement = Arrangement.spacedBy(4.dp),
								verticalAlignment = Alignment.CenterVertically
							) {
								Icon(
									imageVector = Icons.Default.CalendarMonth,
									contentDescription = null,
									modifier = Modifier.size(15.dp),
									tint = MaterialTheme.colorScheme.onPrimaryContainer

								)
								Text(
									text = if (isDailyTask) stringResource(R.string.every_day) else task.getWeekDaysTitle(),
									style = taskDescTextStyle,
									color = MaterialTheme.colorScheme.onPrimaryContainer

								)
							}
						}
					}

				}
				if (!task.isCompleted && task.date.isEqual(LocalDate.now()) && !task.isAllDayTaskEnabled()) {
					IconButton(
						onClick = { onPomodoro(task.id) },
						modifier = Modifier.weight(0.1f)
					) {
						Icon(
							painter = painterResource(id = R.drawable.ic_timer),
							tint = MaterialTheme.colorScheme.onPrimaryContainer,
							contentDescription = null
						)
					}
				}
				if (task.date < LocalDate.now()) {
					IconButton(
						onClick = { onDelete(task.id) },
						modifier = Modifier.weight(0.1f)
					) {
						Icon(
							imageVector = Icons.Default.Delete,
							tint = MaterialTheme.colorScheme.onPrimaryContainer,
							contentDescription = null
						)
					}
				}
			}
		}

	}
}

@Preview
@Composable
fun TaskComponentPreview() {
	val task = DummyTasks.dummyTasks[0]
	SnaptickTheme {
		TaskComponent(
			task = task,
			{},
			{},
			{}
		)
	}
}