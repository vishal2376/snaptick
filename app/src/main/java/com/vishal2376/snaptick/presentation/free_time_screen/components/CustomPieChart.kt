package com.vishal2376.snaptick.presentation.free_time_screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomPieChart(
	data: List<Long>,
	arcWidth: Dp = 30.dp,
	startAngle: Float = -90f,
	pieChartSize: Dp = 200.dp,
	animDuration: Long = 1000
) {
	// calculate each arc value
	val totalSum = data.sum()
	val arcValues = mutableListOf<Float>()
	data.forEachIndexed { index, value ->
		val arc = value.toFloat() / totalSum.toFloat() * 360f
		arcValues.add(index, arc)
	}

	// pie chart colors
	val pieChartColors = listOf(
		Color(0xFFFFA502),
		Color(0xFF70A1FF),
		Color(0xFFFF4757),
		Color(0xFF2ED573),
		Color(0xFFFF7F50),
		Color(0xFFA4B0BE),
		Color(0xFFFF6B81),
		Color(0xFF747D8C),
		Color(0xFFECCC68),
		Color(0xFF1E90FF),
		Color(0xFFCED6E0),
		Color(0xFF7BED9F),
		Color(0xFF2F3542),
		Color(0xFFFF6348),
		Color(0xFFDCDDDF),
	)
	val totalColors = pieChartColors.size

	// draw pie chart
	var newStartAngle = startAngle
	Column(
		modifier = Modifier.size(pieChartSize * 2f),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Canvas(modifier = Modifier.size(pieChartSize)) {
			arcValues.forEachIndexed { index, arcValue ->
				drawArc(
					color = pieChartColors[index % totalColors],
					startAngle = newStartAngle,
					useCenter = false,
					sweepAngle = arcValue,
					style = Stroke(width = arcWidth.toPx()),
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