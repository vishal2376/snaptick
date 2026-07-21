package com.vishal2376.snaptick.presentation.home_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskDescTextStyle
import com.vishal2376.snaptick.presentation.main.state.MainState
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.util.openUrl

data class NewItem(
	val title: String,
	val description: String,
	val link: String = ""
)

@Composable
fun WhatsNewDialogComponent(
	appState: MainState,
	onClose: (Boolean) -> Unit
) {
	// Guards against double-fire when both a button click and the dialog's
	// onDismissRequest land in the same frame as state recomposes.
	var hasFired by remember { mutableStateOf(false) }
	val safeClose: (Boolean) -> Unit = { keepShowing ->
		if (!hasFired) {
			hasFired = true
			onClose(keepShowing)
		}
	}

	val newFeatures = listOf(
		NewItem(
			"Calendar Sync",
			"Mirror tasks to your device calendar and import .ics files."
		),
		NewItem(
			"Refreshed UI",
			"New onboarding, theme cards, swipe action picker, and minimal time picker."
		),
		NewItem(
			"Multiple Reminders",
			"Set up to four reminders per task with custom offsets."
		),
		NewItem(
			"Pomodoro Timer",
			"Foreground timer keeps running even when the app is closed."
		),
		NewItem(
			"Home-screen Widget",
			"Today's tasks at a glance with one-tap complete."
		),
		NewItem(
			"Bug Fixes",
			"Fixed crashes and improved stability."
		),
	)

	Dialog(onDismissRequest = { safeClose(true) }) {
		Card(
			modifier = Modifier
				.fillMaxWidth(1f),
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.primaryContainer,
				contentColor = MaterialTheme.colorScheme.onPrimaryContainer
			)
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = "What's New 🎉",
					style = h2TextStyle,
					color = MaterialTheme.colorScheme.primary
				)

				Text(
					text = stringResource(id = R.string.buildVersion, appState.buildVersion),
					style = infoDescTextStyle,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}

			LazyColumn(
				contentPadding = PaddingValues(horizontal = 16.dp)
			) {
				items(newFeatures) { item ->
					NewItemComponent(newItem = item)
				}
			}

			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 20.dp)
					.padding(bottom = 16.dp),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					modifier = Modifier
						.clickable { safeClose(false) }
						.padding(8.dp),
					text = "Don't show again",
					style = h3TextStyle,
					color = MaterialTheme.colorScheme.error
				)
				Text(
					modifier = Modifier
						.clickable { safeClose(true) }
						.padding(8.dp),
					text = "Continue",
					style = h3TextStyle,
					color = MaterialTheme.colorScheme.primary
				)
			}
		}
	}
}

@Composable
private fun NewItemComponent(newItem: NewItem) {
	val context = LocalContext.current
	Column {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Box(
				modifier = Modifier
					.width(4.dp)
					.height(16.dp)
					.background(MaterialTheme.colorScheme.primary)
			)
			Text(
				modifier = Modifier.weight(1f),
				text = newItem.title,
				style = h3TextStyle,
				color = MaterialTheme.colorScheme.onBackground
			)
			if (newItem.link.isNotEmpty()) {
				Icon(
					modifier = Modifier
						.clickable {
							openUrl(context, newItem.link)
						}
						.size(16.dp),
					imageVector = Icons.Default.OpenInNew,
					contentDescription = null
				)
			}

		}
		Text(
			modifier = Modifier.padding(start = 12.dp),
			text = newItem.description,
			style = taskDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
		Spacer(modifier = Modifier.height(16.dp))
	}
}

@Preview
@Composable
private fun WhatsNewDialogComponentPreview() {
	SnaptickTheme(dynamicColor = false) {
		WhatsNewDialogComponent(MainState(), {})
	}
}