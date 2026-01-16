package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.data.calendar.CalendarHelper
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.taskDescTextStyle

data class CalendarSyncState(
	val isEnabled: Boolean = false,
	val selectedCalendarId: Long? = null,
	val selectedCalendarName: String? = null,
	val availableCalendars: List<CalendarHelper.CalendarInfo> = emptyList(),
	val twoWaySyncEnabled: Boolean = false
)

@Composable
fun CalendarSyncOptionComponent(
	state: CalendarSyncState,
	onSyncEnabledChange: (Boolean) -> Unit,
	onCalendarSelected: (Long, String) -> Unit,
	onTwoWaySyncChange: (Boolean) -> Unit,
	onRequestPermission: () -> Unit
) {
	var showCalendarDropdown by remember { mutableStateOf(false) }

	Column(modifier = Modifier.fillMaxWidth()) {
		Text(
			text = stringResource(R.string.calendar_sync),
			style = h2TextStyle,
			color = MaterialTheme.colorScheme.onBackground
		)

		Spacer(modifier = Modifier.height(16.dp))

		// Sync Enable Toggle
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.clip(RoundedCornerShape(8.dp))
				.background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
				.padding(16.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Column {
				Text(
					text = stringResource(R.string.sync_with_calendar),
					style = taskDescTextStyle,
					color = MaterialTheme.colorScheme.onBackground
				)
				Text(
					text = stringResource(R.string.sync_tasks_to_device_calendar),
					style = taskDescTextStyle.copy(
						color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
					)
				)
			}
			Switch(
				checked = state.isEnabled,
				onCheckedChange = { enabled ->
					if (enabled && state.availableCalendars.isEmpty()) {
						onRequestPermission()
					} else {
						onSyncEnabledChange(enabled)
					}
				}
			)
		}

		// Calendar Picker (only show if sync is enabled)
		if (state.isEnabled) {
			Spacer(modifier = Modifier.height(12.dp))

			Column {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.clip(RoundedCornerShape(8.dp))
						.background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
						.clickable { showCalendarDropdown = true }
						.padding(16.dp),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = stringResource(R.string.select_calendar),
						style = taskDescTextStyle,
						color = MaterialTheme.colorScheme.onBackground
					)
					Text(
						text = state.selectedCalendarName ?: stringResource(R.string.none_selected),
						style = taskDescTextStyle,
						color = MaterialTheme.colorScheme.primary
					)
				}

				DropdownMenu(
					expanded = showCalendarDropdown,
					onDismissRequest = { showCalendarDropdown = false }
				) {
					state.availableCalendars.forEach { calendar ->
						DropdownMenuItem(
							text = {
								Column {
									Text(text = calendar.name)
									Text(
										text = calendar.accountName,
										style = taskDescTextStyle.copy(
											color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
										)
									)
								}
							},
							onClick = {
								onCalendarSelected(calendar.id, calendar.name)
								showCalendarDropdown = false
							}
						)
					}
				}
			}

			// Two-way sync toggle
			Spacer(modifier = Modifier.height(12.dp))

			Row(
				modifier = Modifier
					.fillMaxWidth()
					.clip(RoundedCornerShape(8.dp))
					.background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
					.padding(16.dp),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Column {
					Text(
						text = stringResource(R.string.two_way_sync),
						style = taskDescTextStyle,
						color = MaterialTheme.colorScheme.onBackground
					)
					Text(
						text = stringResource(R.string.import_events_from_calendar),
						style = taskDescTextStyle.copy(
							color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
						)
					)
				}
				Switch(
					checked = state.twoWaySyncEnabled,
					onCheckedChange = onTwoWaySyncChange
				)
			}
		}
	}
}
