package com.vishal2376.snaptick.presentation.onboarding.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.LightGreen
import com.vishal2376.snaptick.ui.theme.Yellow
import kotlinx.coroutines.delay

@Composable
fun RestoreAndSyncPage(
	calendarSyncEnabled: Boolean,
	onRestoreClick: () -> Unit,
	onPickIcsClick: () -> Unit,
	onCalendarSyncToggle: (Boolean) -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = 24.dp, vertical = 16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "Restore Progress",
			style = h1TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.height(6.dp))
		Text(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp),
			text = "Bring your tasks across or sync to your device calendar.",
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.height(24.dp))

		PopAnimatedCard(index = 0) {
			ActionCard(
				iconRes = R.drawable.ic_import,
				accent = Blue,
				title = "Restore from backup",
				subtitle = "Pick a Snaptick .json backup to load every task and setting.",
				onClick = onRestoreClick
			)
		}
		Spacer(Modifier.height(12.dp))

		PopAnimatedCard(index = 1) {
			ActionCard(
				iconRes = R.drawable.ic_calendar_sync,
				accent = Yellow,
				title = "Import .ics file",
				subtitle = "One-tap import from any calendar export. Adds events as tasks.",
				onClick = onPickIcsClick
			)
		}
		Spacer(Modifier.height(12.dp))

		PopAnimatedCard(index = 2) {
			ActionCard(
				iconRes = R.drawable.ic_calendar_sync,
				accent = LightGreen,
				title = "Sync to device calendar",
				subtitle = "Mirror every task to your device calendar automatically.",
				trailing = {
					Switch(
						checked = calendarSyncEnabled,
						onCheckedChange = onCalendarSyncToggle,
						colors = SwitchDefaults.colors(
							checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
							checkedTrackColor = MaterialTheme.colorScheme.primary
						)
					)
				},
				onClick = { onCalendarSyncToggle(!calendarSyncEnabled) }
			)
		}

		Spacer(Modifier.height(20.dp))
		Text(
			text = "You can change these anytime from Settings.",
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
			textAlign = TextAlign.Center,
			modifier = Modifier.fillMaxWidth()
		)
	}
}

@Composable
private fun PopAnimatedCard(
	index: Int,
	content: @Composable () -> Unit,
) {
	var visible by remember { mutableStateOf(false) }
	LaunchedEffect(Unit) {
		delay(140L * index + 80L)
		visible = true
	}
	AnimatedVisibility(
		visible = visible,
		enter = fadeIn(animationSpec = tween(durationMillis = 420)) +
			scaleIn(
				initialScale = 0.86f,
				animationSpec = spring(
					dampingRatio = 0.7f,
					stiffness = 200f
				)
			)
	) {
		content()
	}
}

@Composable
private fun ActionCard(
	iconRes: Int,
	accent: Color,
	title: String,
	subtitle: String,
	trailing: @Composable () -> Unit = { ChevronTrailing() },
	onClick: () -> Unit,
) {
	val interaction = remember { MutableInteractionSource() }
	val pressed by interaction.collectIsPressedAsState()
	val scale by animateFloatAsState(
		targetValue = if (pressed) 0.96f else 1f,
		animationSpec = spring(
			dampingRatio = Spring.DampingRatioMediumBouncy,
			stiffness = Spring.StiffnessMedium
		),
		label = "press-scale"
	)

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.graphicsLayer { scaleX = scale; scaleY = scale }
			.background(
				MaterialTheme.colorScheme.primaryContainer,
				RoundedCornerShape(18.dp)
			)
			.clickable(
				interactionSource = interaction,
				indication = null,
				onClick = onClick
			)
			.padding(horizontal = 16.dp, vertical = 16.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(14.dp)
	) {
		Box(
			modifier = Modifier
				.size(48.dp)
				.background(accent.copy(alpha = 0.22f), RoundedCornerShape(14.dp)),
			contentAlignment = Alignment.Center
		) {
			Icon(
				painter = painterResource(iconRes),
				contentDescription = null,
				tint = accent,
				modifier = Modifier.size(24.dp)
			)
		}
		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = title,
				style = h3TextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
			Spacer(Modifier.height(4.dp))
			Text(
				text = subtitle,
				style = infoDescTextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
			)
		}
		trailing()
	}
}

@Composable
private fun ChevronTrailing() {
	Icon(
		imageVector = Icons.Rounded.ChevronRight,
		contentDescription = null,
		tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
	)
}
