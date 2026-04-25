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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
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
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 24.dp, vertical = 24.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "You're all set",
			style = h1TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.height(8.dp))
		Text(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp),
			text = "Restore your previous data or mirror tasks to your device calendar.",
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer,
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.height(28.dp))

		ActionCard(
			iconRes = R.drawable.ic_import,
			title = stringResource(R.string.backup_restore_data),
			subtitle = "Coming from another device? Load a Snaptick backup file now.",
			trailing = { ChevronTrailing() },
			onClick = onRestoreClick
		)

		Spacer(Modifier.height(14.dp))

		ActionCard(
			iconRes = R.drawable.ic_calendar_sync,
			title = stringResource(R.string.device_calendar),
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

		Spacer(Modifier.height(24.dp))

		Box(
			modifier = Modifier
				.fillMaxWidth()
				.background(
					MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
					RoundedCornerShape(14.dp)
				)
				.padding(horizontal = 16.dp, vertical = 12.dp),
			contentAlignment = Alignment.Center
		) {
			Text(
				text = "You can change these anytime from Settings.",
				style = taskTextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer,
				textAlign = TextAlign.Center
			)
		}
	}
}

@Composable
private fun ActionCard(
	iconRes: Int,
	title: String,
	subtitle: String,
	trailing: @Composable () -> Unit,
	onClick: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				MaterialTheme.colorScheme.primaryContainer,
				RoundedCornerShape(18.dp)
			)
			.clickable { onClick() }
			.padding(horizontal = 16.dp, vertical = 18.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(14.dp)
	) {
		Box(
			modifier = Modifier
				.size(44.dp)
				.background(
					MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
					RoundedCornerShape(12.dp)
				),
			contentAlignment = Alignment.Center
		) {
			Icon(
				painter = painterResource(iconRes),
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary,
				modifier = Modifier.size(22.dp)
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
