package com.vishal2376.snaptick.widget.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.ui.theme.LightGray
import com.vishal2376.snaptick.ui.theme.Red
import com.vishal2376.snaptick.ui.theme.Yellow


@Composable
fun TaskWidgetComponent(task: Task, onClick: () -> Unit) {
	val context = LocalContext.current
	val taskPriorityColor = remember(task) {
		when (task.priority) {
			1 -> Yellow
			2 -> Red
			else -> LightGray
		}
	}
	val checkedImage = remember(task) {
		if (task.isCompleted) ImageProvider(R.drawable.ic_check_circle)
		else ImageProvider(R.drawable.ic_uncheck_circle)
	}

	Row {
		Image(
			provider = ImageProvider(R.drawable.bg_task_left_shape),
			contentDescription = null,
			colorFilter = ColorFilter.tint(ColorProvider(taskPriorityColor)),
			modifier = GlanceModifier.fillMaxHeight()
		)
		Row(
			modifier = GlanceModifier
				.fillMaxWidth()
				.padding(8.dp)
				.background(
					ImageProvider(R.drawable.bg_task_right_shape),
					colorFilter = ColorFilter.tint(GlanceTheme.colors.primaryContainer)
				),
			verticalAlignment = Alignment.CenterVertically
		) {
			Image(
				provider = checkedImage,
				contentDescription = null,
				modifier = GlanceModifier
					.size(35.dp)
					.clickable(onClick)
					.cornerRadius(50.dp)
					.padding(8.dp),
			)
			Column {
				Text(
					text = task.title,
					style = TextStyle(
						color = GlanceTheme.colors.onBackground,
						fontSize = 16.sp,
					)
				)
				Spacer(modifier = GlanceModifier.height(8.dp))
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					Image(
						provider = ImageProvider(R.drawable.ic_clock),
						contentDescription = null,
						modifier = GlanceModifier.size(15.dp)
					)
					Spacer(modifier = GlanceModifier.width(4.dp))
					Text(
						text = if (task.isAllDayTaskEnabled()) {
							context.getString(R.string.all_day)
						} else {
							task.getFormattedTime(is24HourFormat = false)
						},
						style = TextStyle(
							color = GlanceTheme.colors.onPrimaryContainer,
							fontSize = 12.sp
						)
					)
				}
			}
		}
	}
}