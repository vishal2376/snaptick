package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.SwipeBehavior
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.settings.common.ToggleOptions
import com.vishal2376.snaptick.ui.theme.LightGreen
import com.vishal2376.snaptick.ui.theme.Red

@Composable
fun SwipeActionOptionComponent(
	selected: SwipeBehavior,
	onSelect: (SwipeBehavior) -> Unit
) {
	val selectedOptionColor = when (selected) {
		SwipeBehavior.NONE -> MaterialTheme.colorScheme.primary
		SwipeBehavior.DELETE -> Red
		SwipeBehavior.COMPLETE -> LightGreen
	}

	Column(
		modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Text(
			text = stringResource(R.string.choose_swipe_action),
			style = h2TextStyle,
			color = MaterialTheme.colorScheme.onBackground
		)

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			horizontalArrangement = Arrangement.SpaceAround,
		) {
			ToggleOptions(
				title = stringResource(R.string.none),
				selectedBgColor = selectedOptionColor,
				isSelected = selected == SwipeBehavior.NONE
			) {
				onSelect(SwipeBehavior.NONE)
			}
			ToggleOptions(
				title = stringResource(R.string.delete),
				selectedBgColor = selectedOptionColor,
				isSelected = selected == SwipeBehavior.DELETE
			) {
				onSelect(SwipeBehavior.DELETE)
			}
			ToggleOptions(
				title = stringResource(R.string.complete),
				selectedBgColor = selectedOptionColor,
				isSelected = selected == SwipeBehavior.COMPLETE
			) {
				onSelect(SwipeBehavior.COMPLETE)
			}
		}
	}
}