package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.common.utils.Formatters
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.LightGreen

@Composable
fun EventImportOptionComponent(
	previewTasks: List<Task>,
	onPickIcsFile: () -> Unit,
	onImport: (List<Task>) -> Unit,
	onClearPreview: () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 8.dp, vertical = 4.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Text(
			text = stringResource(R.string.import_events),
			style = h1TextStyle,
			color = MaterialTheme.colorScheme.onBackground
		)

		if (previewTasks.isEmpty()) {
			EmptyImportState(onPickIcsFile = onPickIcsFile)
		} else {
			LoadedImportState(
				previewTasks = previewTasks,
				onImport = onImport,
				onClearPreview = onClearPreview,
				onPickAnother = onPickIcsFile,
			)
		}
	}
}

@Composable
private fun EmptyImportState(onPickIcsFile: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				MaterialTheme.colorScheme.primaryContainer,
				RoundedCornerShape(20.dp)
			)
			.padding(horizontal = 20.dp, vertical = 24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Box(
			modifier = Modifier
				.size(72.dp)
				.background(Blue.copy(alpha = 0.2f), CircleShape),
			contentAlignment = Alignment.Center
		) {
			Icon(
				painter = painterResource(R.drawable.ic_import),
				contentDescription = null,
				tint = Blue,
				modifier = Modifier.size(36.dp)
			)
		}
		Text(
			text = "Import from .ics file",
			style = h3TextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
		Text(
			text = "Pick a calendar export from Google, Outlook, or Apple Calendar.",
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
		)
		Spacer(Modifier.height(4.dp))
		Button(
			onClick = onPickIcsFile,
			modifier = Modifier.fillMaxWidth(),
			colors = ButtonDefaults.buttonColors(
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = MaterialTheme.colorScheme.onPrimary
			),
			shape = RoundedCornerShape(14.dp)
		) {
			Text(
				text = stringResource(R.string.pick_ics_file),
				style = taskTextStyle,
				modifier = Modifier.padding(6.dp)
			)
		}
	}
}

@Composable
private fun LoadedImportState(
	previewTasks: List<Task>,
	onImport: (List<Task>) -> Unit,
	onClearPreview: () -> Unit,
	onPickAnother: () -> Unit,
) {
	var selected by remember(previewTasks) {
		mutableStateOf(previewTasks.map { it.uuid }.toSet())
	}

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(LightGreen.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
			.padding(horizontal = 14.dp, vertical = 10.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(10.dp)
	) {
		Box(
			modifier = Modifier
				.size(36.dp)
				.background(LightGreen.copy(alpha = 0.3f), CircleShape),
			contentAlignment = Alignment.Center
		) {
			Icon(
				painter = painterResource(R.drawable.ic_check_circle),
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary,
				modifier = Modifier.size(20.dp)
			)
		}
		Text(
			modifier = Modifier.weight(1f),
			text = "${previewTasks.size} events ready",
			style = h3TextStyle,
			color = MaterialTheme.colorScheme.onBackground
		)
		Text(
			modifier = Modifier
				.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
				.clickable { onPickAnother() }
				.padding(horizontal = 10.dp, vertical = 6.dp),
			text = "Change",
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
	}

	LazyColumn(
		modifier = Modifier
			.fillMaxWidth()
			.heightIn(max = 320.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		items(previewTasks, key = { it.uuid }) { task ->
			val checked = task.uuid in selected
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.background(
						MaterialTheme.colorScheme.primaryContainer,
						RoundedCornerShape(12.dp)
					)
					.clickable {
						selected = if (checked) selected - task.uuid else selected + task.uuid
					}
					.padding(horizontal = 12.dp, vertical = 10.dp),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Checkbox(
					checked = checked,
					onCheckedChange = {
						selected = if (it) selected + task.uuid else selected - task.uuid
					},
					colors = CheckboxDefaults.colors(
						checkedColor = MaterialTheme.colorScheme.primary
					)
				)
				Column(modifier = Modifier.weight(1f)) {
					Text(
						text = task.title,
						style = taskTextStyle,
						color = MaterialTheme.colorScheme.onBackground
					)
					Text(
						text = task.date.format(Formatters.dayMonth),
						style = infoDescTextStyle,
						color = MaterialTheme.colorScheme.onPrimaryContainer
					)
				}
			}
		}
	}

	Button(
		onClick = {
			val picks = previewTasks.filter { it.uuid in selected }
			onImport(picks)
			onClearPreview()
		},
		enabled = selected.isNotEmpty(),
		modifier = Modifier.fillMaxWidth(),
		colors = ButtonDefaults.buttonColors(
			containerColor = MaterialTheme.colorScheme.primary,
			contentColor = MaterialTheme.colorScheme.onPrimary
		),
		shape = RoundedCornerShape(14.dp)
	) {
		Text(
			text = if (selected.isEmpty()) "Select at least one"
			else "Import ${selected.size} ${if (selected.size == 1) "event" else "events"}",
			style = taskTextStyle,
			modifier = Modifier.padding(6.dp)
		)
	}
}
