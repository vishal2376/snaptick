package com.vishal2376.snaptick.presentation.free_time_screen.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.ui.theme.pieChartColors

@Composable
fun CustomPieChart(
	data: List<Long>,
	arcWidth: Dp = 30.dp,
	startAngle: Float = -90f,
	pieChartSize: Dp = 200.dp,
	animDuration: Int = 1000
) {
	// calculate each arc value
	val totalSum = data.sum()
	val arcValues = mutableListOf<Float>()
	data.forEachIndexed { index, value ->
		val arc = value.toFloat() / totalSum.toFloat() * 360f
		arcValues.add(index, arc)
	}

	var newStartAngle = startAngle

	// animations
	val animationProgress = remember { Animatable(0f) }
	LaunchedEffect(Unit) {
		animationProgress.animateTo(1f, animationSpec = tween(animDuration))
	}

	// draw pie chart
	val totalColors = pieChartColors.size

	Column(
		modifier = Modifier.size(pieChartSize * 1.5f),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Canvas(modifier = Modifier.size(pieChartSize)) {
			arcValues.forEachIndexed { index, arcValue ->
				drawArc(
					color = pieChartColors[index % totalColors],
					startAngle = newStartAngle,
					useCenter = false,
					sweepAngle = arcValue * animationProgress.value,
					style = Stroke(width = arcWidth.toPx())
				)
				newStartAngle += arcValue
			}
		}
	}

}

@Preview
@Composable
fun CustomPieChartPreview() {
	val time = listOf<Long>(10, 20, 40, 15, 60, 20, 10)
	CustomPieChart(time)
}