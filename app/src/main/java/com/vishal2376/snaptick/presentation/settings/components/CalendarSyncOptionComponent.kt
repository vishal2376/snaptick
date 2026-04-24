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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.data.calendar.CalendarInfo
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle

@Composable
fun CalendarSyncOptionComponent(
	enabled: Boolean,
	selectedCalendarId: Long?,
	writableCalendars: List<CalendarInfo>,
	onEnabledChange: (Boolean) -> Unit,
	onCalendarSelected: (Long) -> Unit,
	onSyncAllNow: () -> Unit,
) {
	val selectedCalendar = writableCalendars.firstOrNull { it.id == selectedCalendarId }

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 4.dp, vertical = 8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Text(
			text = stringResource(R.string.calendar_sync),
			style = h1TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
			modifier = Modifier.padding(horizontal = 12.dp)
		)
		Spacer(Modifier.height(4.dp))

		// Toggle row
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 12.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = stringResource(R.string.sync_tasks_to_device_calendar),
				style = h2TextStyle,
				color = MaterialTheme.colorScheme.onBackground,
				modifier = Modifier.weight(1f)
			)
			Switch(
				checked = enabled,
				onCheckedChange = onEnabledChange,
				colors = SwitchDefaults.colors(
					checkedThumbColor = MaterialTheme.colorScheme.primary,
					checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
					uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer
				)
			)
		}

		if (enabled) {
			Divider(color = MaterialTheme.colorScheme.primaryContainer)

			Column(
				modifier = Modifier.padding(horizontal = 12.dp),
				verticalArrangement = Arrangement.spacedBy(4.dp)
			) {
				Text(
					text = stringResource(R.string.pick_calendar),
					style = h2TextStyle,
					color = MaterialTheme.colorScheme.onBackground
				)
				if (writableCalendars.isEmpty()) {
					Text(
						text = stringResource(R.string.no_writable_calendars_found),
						style = infoDescTextStyle,
						color = MaterialTheme.colorScheme.onPrimaryContainer
					)
				} else {
					writableCalendars.forEach { cal ->
						CalendarRow(
							info = cal,
							selected = cal.id == selectedCalendarId,
							onClick = { onCalendarSelected(cal.id) }
						)
					}
				}
			}

			Button(
				onClick = onSyncAllNow,
				enabled = selectedCalendar != null,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 12.dp),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = MaterialTheme.colorScheme.onPrimary
				),
				shape = RoundedCornerShape(12.dp)
			) {
				Text(
					text = stringResource(R.string.sync_all_tasks_now),
					style = taskTextStyle,
					modifier = Modifier.padding(8.dp)
				)
			}
		}
	}
}

@Composable
private fun CalendarRow(
	info: CalendarInfo,
	selected: Boolean,
	onClick: () -> Unit,
) {
	val bg = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
	else Color.Transparent
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onClick() }
			.background(bg, RoundedCornerShape(8.dp))
			.padding(horizontal = 8.dp, vertical = 10.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp)
	) {
		Box(
			modifier = Modifier
				.size(16.dp)
				.background(Color(0xFF000000.toInt() or info.colorArgb), CircleShape)
		)
		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = info.displayName.ifBlank { info.accountName },
				style = taskTextStyle,
				color = MaterialTheme.colorScheme.onBackground
			)
			if (info.accountName.isNotBlank()) {
				Text(
					text = info.accountName,
					style = infoDescTextStyle,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
		}
	}
}
