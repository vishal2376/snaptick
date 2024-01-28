package com.vishal2376.snaptick.presentation.free_time_screen.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.Blue
import kotlinx.coroutines.delay
import java.time.LocalTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PieChartItemComponent(task: Task, itemColor: Color, animDelay: Int = 100) {

	val duration = task.getDuration()
	val alphaAnimation = remember { Animatable(initialValue = 0f) }

	LaunchedEffect(animDelay) {
		delay(animDelay.toLong())
		alphaAnimation.animateTo(targetValue = 1f, animationSpec = tween(1000))
	}

	Box(
		modifier = Modifier
			.graphicsLayer {
				alpha = alphaAnimation.value
			}
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
			.padding(16.dp, 20.dp)
	) {

		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {

			Box(
				modifier = Modifier
					.size(16.dp)
					.background(itemColor, CircleShape),
			)

			Spacer(modifier = Modifier.width(0.dp))

			Text(
				text = task.title,
				modifier = Modifier
					.basicMarquee(delayMillis = 1000)
					.weight(1f),
				style = taskTextStyle,
				color = Color.White,
			)

			Text(
				text = task.getFormattedDurationTimeStamp(duration, trimSeconds = true),
				style = taskTextStyle,
				color = Color.White
			)
		}
	}
}

@Preview
@Composable
fun PieChartItemComponentPreview() {
	val task = Task(
		id = 2,
		title = "Drink Water",
		isCompleted = true,
		startTime = LocalTime.of(10, 0),
		endTime = LocalTime.of(11, 0),
		reminder = false,
		category = "",
		priority = 1
	)

	PieChartItemComponent(task, Blue)
}