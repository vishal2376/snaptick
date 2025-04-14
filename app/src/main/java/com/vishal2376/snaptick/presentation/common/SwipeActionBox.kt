package com.vishal2376.snaptick.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.ui.theme.Blue500
import com.vishal2376.snaptick.ui.theme.LightGreen
import com.vishal2376.snaptick.ui.theme.Red
import kotlinx.coroutines.delay

enum class SwipeBehavior {
	NONE,
	DELETE,
	COMPLETE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SwipeActionBox(
	item: T,
	onDelete: (T) -> Unit,
	onComplete: (T) -> Unit,
	swipeBehavior: SwipeBehavior = SwipeBehavior.DELETE,
	iconTint: Color = Blue500,
	animationDuration: Int = 300,
	content: @Composable (T) -> Unit
) {
	var isActionDone by remember { mutableStateOf(false) }

	val dismissState = rememberDismissState(
		initialValue = DismissValue.Default,
		confirmValueChange = { dismissValue ->
			if (dismissValue == DismissValue.DismissedToStart && swipeBehavior != SwipeBehavior.NONE) {
				isActionDone = true
				true
			} else {
				false
			}
		}
	)

	LaunchedEffect(isActionDone) {
		if (isActionDone) {
			delay(animationDuration.toLong())
			when (swipeBehavior) {
				SwipeBehavior.DELETE -> {
					onDelete(item)
				}

				SwipeBehavior.COMPLETE -> {
					onComplete(item)
				}

				else -> {}
			}
			dismissState.snapTo(DismissValue.Default)
		}
	}

	val bgColor = when (swipeBehavior) {
		SwipeBehavior.DELETE -> Red
		SwipeBehavior.COMPLETE -> LightGreen
		else -> Color.Transparent
	}

	val icon = when (swipeBehavior) {
		SwipeBehavior.DELETE -> Icons.Default.Delete
		SwipeBehavior.COMPLETE -> Icons.Default.CheckCircleOutline
		else -> Icons.Default.Refresh
	}

	AnimatedVisibility(
		visible = !isActionDone,
		exit = fadeOut(tween(animationDuration))
	) {
		if (swipeBehavior == SwipeBehavior.NONE) {
			content(item)
		} else {
			SwipeToDismiss(
				state = dismissState,
				background = {
					ActionBackground(
						dismissState = dismissState,
						bgColor = bgColor,
						icon = icon,
						iconTint = iconTint
					)
				},
				dismissContent = { content(item) },
				directions = setOf(DismissDirection.EndToStart)
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionBackground(
	dismissState: DismissState,
	bgColor: Color,
	icon: ImageVector,
	iconTint: Color
) {
	var alphaValue = 0f
	var color = Color.Transparent

	if (dismissState.dismissDirection == DismissDirection.EndToStart) {
		color = bgColor
		alphaValue = 1.0f
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(color, RoundedCornerShape(8.dp))
			.graphicsLayer {
				alpha = alphaValue
			}
			.padding(16.dp), contentAlignment = Alignment.CenterEnd
	) {
		Icon(imageVector = icon, contentDescription = null, tint = iconTint)
	}
}