package com.vishal2376.snaptick.presentation.onboarding.pages

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
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
			.padding(horizontal = 24.dp, vertical = 32.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "Here's what Snaptick looks like",
			style = h1TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.height(16.dp))

		demos.forEach { task ->
			TaskComponent(
				task = task,
				onEdit = {},
				onComplete = {},
				onPomodoro = {},
				onDelete = {},
				is24HourTimeFormat = false
			)
			Spacer(Modifier.height(10.dp))
		}

		Spacer(Modifier.height(24.dp))
		Text(
			text = "Pick your theme",
			style = h2TextStyle,
			color = MaterialTheme.colorScheme.onBackground
		)
		Spacer(Modifier.height(12.dp))
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			AppTheme.entries.forEach { theme ->
				val selected = theme == selectedTheme
				val bg = if (selected) MaterialTheme.colorScheme.primary
				else MaterialTheme.colorScheme.primaryContainer
				val fg = if (selected) MaterialTheme.colorScheme.onPrimary
				else MaterialTheme.colorScheme.onPrimaryContainer
				Box(
					modifier = Modifier
						.weight(1f)
						.height(52.dp)
						.background(bg, RoundedCornerShape(14.dp))
						.clickable { onThemeSelected(theme) },
					contentAlignment = Alignment.Center
				) {
					Text(
						text = theme.name,
						style = taskTextStyle,
						color = fg
					)
				}
			}
		}
	}
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
