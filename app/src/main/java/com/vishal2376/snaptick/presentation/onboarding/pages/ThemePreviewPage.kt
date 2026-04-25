package com.vishal2376.snaptick.presentation.onboarding.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.AppTheme
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.IntOffset
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.home_screen.components.TaskComponent
import com.vishal2376.snaptick.presentation.onboarding.components.AnimatedBorderCard
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThemePreviewPage(
	selectedTheme: AppTheme,
	onThemeSelected: (AppTheme) -> Unit,
) {
	val initialDemos = remember { demoTasks() }
	var demoOrder by remember { mutableStateOf(initialDemos) }

	LaunchedEffect(selectedTheme) {
		demoOrder = demoOrder.shuffled()
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = 24.dp, vertical = 16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "Choose your Theme",
			style = h1TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.height(6.dp))
		Text(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp),
			text = "Tap a theme below to see how Snaptick looks.",
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.height(20.dp))

		Box(modifier = Modifier.weight(1f)) {
			LazyColumn(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				items(demoOrder, key = { it.uuid }) { task ->
					Box(
						modifier = Modifier.animateItemPlacement(
							spring(
								dampingRatio = 0.85f,
								stiffness = Spring.StiffnessLow,
								visibilityThreshold = IntOffset.VisibilityThreshold
							)
						)
					) {
						TaskComponent(
							task = task,
							onEdit = {},
							onComplete = {},
							onPomodoro = {},
							onDelete = {},
							is24HourTimeFormat = false
						)
					}
				}
			}
		}

		Spacer(Modifier.height(24.dp))

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			AppTheme.entries.forEach { theme ->
				ThemeCard(
					theme = theme,
					selected = theme == selectedTheme,
					onClick = { onThemeSelected(theme) },
					modifier = Modifier.weight(1f)
				)
			}
		}
	}
}

@Composable
private fun ThemeCard(
	theme: AppTheme,
	selected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val swatch = themeSwatch(theme)
	val labelColor =
		if (selected) MaterialTheme.colorScheme.primary
		else MaterialTheme.colorScheme.onPrimaryContainer

	AnimatedBorderCard(
		selected = selected,
		onClick = onClick,
		borderColor = MaterialTheme.colorScheme.primary,
		idleBorderColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
		background = MaterialTheme.colorScheme.primaryContainer,
		modifier = modifier
	) {
		Column(
			modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(10.dp)
		) {
			Box(
				modifier = Modifier
					.size(36.dp)
					.background(swatch, CircleShape)
			)
			Text(
				text = theme.name,
				style = if (selected) h3TextStyle else taskTextStyle,
				color = labelColor
			)
		}
	}
}

private fun themeSwatch(theme: AppTheme): Color = when (theme) {
	AppTheme.Light -> Color(0xFFFBFBFB)
	AppTheme.Dark -> Color(0xFF161A30)
	AppTheme.Amoled -> Color.Black
}

private fun demoTasks(): List<Task> = listOf(
	Task(
		id = 1, uuid = "demo-1", title = "Morning run",
		startTime = LocalTime.of(6, 30), endTime = LocalTime.of(7, 15),
		reminder = true, date = LocalDate.now(), priority = 2
	),
	Task(
		id = 2, uuid = "demo-2", title = "Design review",
		startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0),
		reminder = true, date = LocalDate.now(), priority = 1
	),
	Task(
		id = 3, uuid = "demo-3", title = "Read 30 pages",
		startTime = LocalTime.of(21, 0), endTime = LocalTime.of(21, 45),
		date = LocalDate.now(), priority = 0
	),
)
