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
import androidx.compose.material3.TimePickerState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NativeTimePickerDialog(
	state: TimePickerState,
	onClose: () -> Unit,
) {
	Dialog(onDismissRequest = { onClose() }) {
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.border(4.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp)),
			shape = RoundedCornerShape(16.dp),
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
		) {
			Column(
				modifier = Modifier.padding(16.dp),
			) {
				TimePicker(
					state = state, colors = TimePickerDefaults.colors(
						clockDialColor = MaterialTheme.colorScheme.secondary,
						selectorColor = Blue,
						clockDialSelectedContentColor = Black500,
						timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.secondary,
						timeSelectorSelectedContainerColor = Blue,
						timeSelectorSelectedContentColor = Black500,
						periodSelectorSelectedContainerColor = Blue,
						periodSelectorSelectedContentColor = Black500,
						periodSelectorBorderColor = MaterialTheme.colorScheme.secondary,
						clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSecondary,
						timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSecondary,
						periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSecondary,
						periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.primary,
					)
				)
				Text(
					modifier = Modifier
						.padding(16.dp, 8.dp)
						.clickable {
							onClose()
						}
						.align(Alignment.End),
					text = stringResource(R.string.done),
					style = h3TextStyle,
					color = Blue
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
		NativeTimePickerDialog(state = state, {})
	}
}