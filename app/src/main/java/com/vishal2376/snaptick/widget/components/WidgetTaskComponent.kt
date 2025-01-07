package com.vishal2376.snaptick.widget.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.ui.theme.LightGray
import com.vishal2376.snaptick.ui.theme.White500
import com.vishal2376.snaptick.widget.model.WidgetTaskModel

@Composable
fun WidgetTaskComponent(
	task: WidgetTaskModel,
	onClick: Action,
	modifier: GlanceModifier = GlanceModifier
) {

	val taskBackground = remember(task) {
		when (task.priority) {
			1 -> ImageProvider(R.drawable.bg_task_med)
			2 -> ImageProvider(R.drawable.bg_task_high)
			else -> ImageProvider(R.drawable.bg_task_low)
		}
	}

	val checkedImage = remember(task) {
		if (task.isCompleted) ImageProvider(R.drawable.ic_check_circle)
		else ImageProvider(R.drawable.ic_uncheck_circle)
	}


	Row(
		modifier = modifier
			.padding(8.dp)
			.background(taskBackground),
		verticalAlignment = Alignment.CenterVertically
	) {
		Image(
			provider = checkedImage,
			contentDescription = null,
			modifier = GlanceModifier
				.size(35.dp)
				.padding(8.dp)
				.clickable(onClick)
		)
		Column {
			Text(
				text = task.title,
				style = TextStyle(
					color = ColorProvider(color = White500),
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
						"All Day"
					} else {
						task.getFormattedTime(is24HourFormat = false)
					},
					style = TextStyle(
						color = ColorProvider(color = LightGray),
						fontSize = 12.sp
					)
				)
			}
		}
	}
}