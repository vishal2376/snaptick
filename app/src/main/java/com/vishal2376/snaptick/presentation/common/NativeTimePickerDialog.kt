package com.vishal2376.snaptick.presentation.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.ui.theme.Black500
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NativeTimePickerDialog(
	time: LocalTime,
	is24hourFormat: Boolean = false,
	onClose: (LocalTime) -> Unit,
) {
	val state = rememberTimePickerState(time.hour, time.minute, is24hourFormat)
	Dialog(onDismissRequest = { onClose(time) }) {
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.border(4.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp)),
			shape = RoundedCornerShape(16.dp),
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
		) {
			Column(
				modifier = Modifier.padding(16.dp),
			) {
				TimePicker(
					state = state, colors = TimePickerDefaults.colors(
						clockDialColor = MaterialTheme.colorScheme.primaryContainer,
						selectorColor = MaterialTheme.colorScheme.primary,
						clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
						timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
						timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
						timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
						periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
						periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
						periodSelectorBorderColor = MaterialTheme.colorScheme.primaryContainer,
						clockDialUnselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
						timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
						periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
						periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.background,
					)
				)
				Text(
					modifier = Modifier
						.padding(16.dp, 8.dp)
						.clickable {
							val newTime = LocalTime.of(state.hour, state.minute)
							onClose(newTime)
						}
						.align(Alignment.End),
					text = stringResource(R.string.done),
					style = h3TextStyle,
					color = MaterialTheme.colorScheme.primary
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NativeTimePickerDialogPreview() {
	val state = rememberTimePickerState()
	SnaptickTheme {
		NativeTimePickerDialog(LocalTime.now(),false, {})
	}
}