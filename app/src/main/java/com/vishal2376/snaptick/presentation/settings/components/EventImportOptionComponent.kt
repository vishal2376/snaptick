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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.common.utils.Formatters

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
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 12.dp, vertical = 8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Text(
			text = stringResource(R.string.import_events),
			style = h1TextStyle,
			color = MaterialTheme.colorScheme.onBackground
		)
		Spacer(Modifier.height(4.dp))

		Button(
			onClick = onPickIcsFile,
			modifier = Modifier.fillMaxWidth(),
			colors = ButtonDefaults.buttonColors(
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = MaterialTheme.colorScheme.onPrimary
			),
			shape = RoundedCornerShape(12.dp)
		) {
			Text(
				text = stringResource(R.string.pick_ics_file),
				style = taskTextStyle,
				modifier = Modifier.padding(8.dp)
			)
		}

		if (previewTasks.isEmpty()) {
			Text(
				text = stringResource(R.string.from_ics_file),
				style = infoDescTextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
		} else {
			Divider(color = MaterialTheme.colorScheme.primaryContainer)
			Text(
				text = "${previewTasks.size} events",
				style = h2TextStyle,
				color = MaterialTheme.colorScheme.onBackground
			)
			val selected = remember(previewTasks) {
				mutableStateOf(previewTasks.map { it.uuid }.toSet())
			}
			previewTasks.forEach { task ->
				val checked = task.uuid in selected.value
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.clickable {
							selected.value =
								if (checked) selected.value - task.uuid else selected.value + task.uuid
						}
						.padding(vertical = 4.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					Checkbox(
						checked = checked,
						onCheckedChange = {
							selected.value = if (it) selected.value + task.uuid else selected.value - task.uuid
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
			Button(
				onClick = {
					val picks = previewTasks.filter { it.uuid in selected.value }
					onImport(picks)
					onClearPreview()
				},
				enabled = selected.value.isNotEmpty(),
				modifier = Modifier.fillMaxWidth(),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = MaterialTheme.colorScheme.onPrimary
				),
				shape = RoundedCornerShape(12.dp)
			) {
				Text(
					text = stringResource(R.string.import_selected),
					style = taskTextStyle,
					modifier = Modifier.padding(8.dp)
				)
			}
		}
	}
}
