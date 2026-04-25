package com.vishal2376.snaptick.presentation.onboarding.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Rounded card with a static primary border when [selected]. No breathing or
 * traveling segment animation. Calm Material 3 wallpaper-picker style. Border
 * width and color animate via colorAsState for smooth transitions.
 */
@Composable
fun AnimatedBorderCard(
	selected: Boolean,
	onClick: () -> Unit,
	cornerRadius: Dp = 16.dp,
	borderColor: Color = Color.Unspecified,
	idleBorderColor: Color = Color.Unspecified,
	background: Color = Color.Unspecified,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	val shape = RoundedCornerShape(cornerRadius)
	val animatedBorder by animateColorAsState(
		targetValue = if (selected) borderColor else idleBorderColor,
		animationSpec = tween(durationMillis = 240),
		label = "border-color"
	)

	Box(
		modifier = modifier
			.clip(shape)
			.background(background, shape)
			.border(
				width = if (selected) 2.5.dp else 1.dp,
				color = animatedBorder,
				shape = shape
			)
			.clickable(
				interactionSource = interactionSource,
				indication = null
			) { onClick() }
	) {
		content()
	}
}
