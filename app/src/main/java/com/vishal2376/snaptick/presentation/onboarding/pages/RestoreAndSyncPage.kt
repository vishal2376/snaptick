package com.vishal2376.snaptick.presentation.onboarding.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle

@Composable
fun RestoreAndSyncPage(
	calendarSyncEnabled: Boolean,
	onRestoreClick: () -> Unit,
	onCalendarSyncToggle: (Boolean) -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = 24.dp, vertical = 32.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "Finish setup",
			style = h1TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.height(24.dp))

		// Restore backup row
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
				.clickable { onRestoreClick() }
				.padding(20.dp),
		) {
			Text(
				text = stringResource(R.string.backup_restore_data),
				style = h2TextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
			Spacer(Modifier.height(4.dp))
			Text(
				text = "Coming from another device? Load your previous backup now.",
				style = infoDescTextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
		}

		Spacer(Modifier.height(16.dp))

		// Calendar sync toggle
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
				.padding(20.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = stringResource(R.string.sync_tasks_to_device_calendar),
					style = h2TextStyle,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
				Spacer(Modifier.height(4.dp))
				Text(
					text = "Mirror every task to your device calendar.",
					style = infoDescTextStyle,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
			Switch(
				checked = calendarSyncEnabled,
				onCheckedChange = onCalendarSyncToggle,
				colors = SwitchDefaults.colors(
					checkedThumbColor = MaterialTheme.colorScheme.primary,
					checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
				)
			)
		}

		Spacer(Modifier.height(12.dp))
		Text(
			text = "You can change these anytime in Settings.",
			style = taskTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer,
			textAlign = TextAlign.Center
		)
	}
}
