package com.vishal2376.snaptick.presentation.onboarding.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.home_screen.components.TaskComponent
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun ThemePreviewPage(
	selectedTheme: AppTheme,
	onThemeSelected: (AppTheme) -> Unit,
) {
	val demos = demoTasks()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 24.dp, vertical = 16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "Make it yours",
			style = h1TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.height(8.dp))
		Text(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp),
			text = "Pick a theme below. Preview updates live as you tap.",
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer,
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.height(20.dp))

		Text(
			modifier = Modifier.fillMaxWidth(),
			text = "Preview",
			style = h3TextStyle,
			color = MaterialTheme.colorScheme.onBackground
		)
		Spacer(Modifier.height(10.dp))

		demos.forEach { task ->
			TaskComponent(
				task = task,
				onEdit = {},
				onComplete = {},
				onPomodoro = {},
				onDelete = {},
				is24HourTimeFormat = false
			)
			Spacer(Modifier.height(8.dp))
		}

		Spacer(Modifier.height(20.dp))
		Text(
			modifier = Modifier.fillMaxWidth(),
			text = "Choose your theme",
			style = h3TextStyle,
			color = MaterialTheme.colorScheme.onBackground
		)
		Spacer(Modifier.height(10.dp))

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(10.dp)
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
	val borderColor =
		if (selected) MaterialTheme.colorScheme.primary
		else MaterialTheme.colorScheme.primaryContainer

	Column(
		modifier = modifier
			.background(
				MaterialTheme.colorScheme.primaryContainer,
				RoundedCornerShape(16.dp)
			)
			.border(
				width = if (selected) 2.dp else 1.dp,
				color = borderColor,
				shape = RoundedCornerShape(16.dp)
			)
			.clickable { onClick() }
			.padding(vertical = 14.dp, horizontal = 12.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Box(
			modifier = Modifier
				.size(36.dp)
				.background(swatch, CircleShape)
				.border(1.dp, MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f), CircleShape)
		)
		Text(
			text = theme.name,
			style = taskTextStyle,
			color = if (selected) MaterialTheme.colorScheme.primary
			else MaterialTheme.colorScheme.onPrimaryContainer
		)
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
