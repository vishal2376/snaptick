package com.vishal2376.snaptick.presentation.onboarding.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Rounded card whose border has two short stroke segments that continuously
 * travel along the perimeter while [selected]. Both segments start at top-center;
 * one moves clockwise, the other counterclockwise, meeting at bottom-center each
 * cycle. When [selected] is false, only a static thin border is drawn.
 */
@Composable
fun AnimatedBorderCard(
	selected: Boolean,
	onClick: () -> Unit,
	cornerRadius: Dp = 16.dp,
	borderColor: Color = Color.Unspecified,
	idleBorderColor: Color = Color.Unspecified,
	background: Color = Color.Unspecified,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	val transition = rememberInfiniteTransition(label = "border-travel")
	val progress by transition.animateFloat(
		initialValue = 0f,
		targetValue = 1f,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = 3200, easing = LinearEasing),
			repeatMode = RepeatMode.Restart
		),
		label = "border-progress"
	)

	val segmentFraction = 0.18f
	val strokeWidthDp = 2.5.dp
	val idleStrokeDp = 1.dp

	val drawModifier = Modifier.drawWithCache {
		val cornerPx = cornerRadius.toPx()
		val rectPath = Path().apply {
			addRoundRect(
				RoundRect(
					rect = androidx.compose.ui.geometry.Rect(
						0f,
						0f,
						size.width,
						size.height
					),
					cornerRadius = CornerRadius(cornerPx, cornerPx)
				)
			)
		}
		val measure = PathMeasure().apply { setPath(rectPath, false) }
		val totalLength = measure.length

		onDrawWithContent {
			drawContent()

			if (!selected) {
				drawPath(
					path = rectPath,
					color = idleBorderColor,
					style = Stroke(width = idleStrokeDp.toPx())
				)
				return@onDrawWithContent
			}

			drawPath(
				path = rectPath,
				color = borderColor.copy(alpha = 0.25f),
				style = Stroke(width = idleStrokeDp.toPx())
			)

			val segmentLen = totalLength * segmentFraction

			// Clockwise segment: starts at progress * (totalLength / 2)
			val cwStart = (progress * (totalLength / 2f)) % totalLength
			drawSegment(measure, totalLength, cwStart, segmentLen, borderColor, strokeWidthDp.toPx())

			// Counter-clockwise segment: starts on opposite side of perimeter
			val ccwStart = (totalLength - progress * (totalLength / 2f) - segmentLen + totalLength) % totalLength
			drawSegment(measure, totalLength, ccwStart, segmentLen, borderColor, strokeWidthDp.toPx())
		}
	}

	Box(
		modifier = modifier
			.clip(RoundedCornerShape(cornerRadius))
			.background(background, RoundedCornerShape(cornerRadius))
			.then(drawModifier)
			.clickable { onClick() }
	) {
		content()
	}
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSegment(
	measure: PathMeasure,
	totalLength: Float,
	start: Float,
	segmentLen: Float,
	color: Color,
	strokeWidth: Float,
) {
	val end = start + segmentLen
	val segmentPath = Path()
	if (end <= totalLength) {
		measure.getSegment(start, end, segmentPath, true)
	} else {
		measure.getSegment(start, totalLength, segmentPath, true)
		val wrapPath = Path()
		measure.getSegment(0f, end - totalLength, wrapPath, true)
		segmentPath.addPath(wrapPath)
	}
	drawPath(
		path = segmentPath,
		color = color,
		style = Stroke(width = strokeWidth)
	)
}
