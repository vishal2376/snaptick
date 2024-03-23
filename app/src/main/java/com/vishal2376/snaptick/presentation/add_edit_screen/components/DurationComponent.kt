package com.vishal2376.snaptick.presentation.add_edit_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.fontRobotoMono
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

@Composable
fun DurationComponent(
	modifier: Modifier = Modifier,
	durationList: List<Long>,
	defaultDuration: Long = 60,
	onSelect: (Long) -> Unit
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically,
	) {
		val lastIndex = durationList.lastIndex

		var defaultDurationIndex = durationList.indexOf(defaultDuration)
		if (defaultDurationIndex == -1) defaultDurationIndex = lastIndex

		var selectedOption by remember { mutableIntStateOf(defaultDurationIndex) }

		durationList.forEachIndexed { index, it ->
			var durationText = "$it min"

			val shape = when (index) {
				0 -> {
					RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
				}

				lastIndex -> {
					durationText = stringResource(R.string.custom)
					RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
				}

				else -> {
					RectangleShape
				}
			}

			DurationItemComponent(
				modifier = Modifier.weight(1f),
				text = durationText,
				isSelected = selectedOption == index,
				shape = shape
			) {
				onSelect(it)
				selectedOption = index
			}
		}
	}
}

@Composable
fun DurationItemComponent(
	modifier: Modifier = Modifier,
	text: String,
	isSelected: Boolean = false,
	shape: Shape = RectangleShape,
	onClick: () -> Unit
) {
	val bgColor = if (isSelected)
		MaterialTheme.colorScheme.secondary
	else
		MaterialTheme.colorScheme.primary

	Box(
		modifier = modifier
			.clickable { onClick() }
			.fillMaxWidth()
			.background(bgColor, shape)
			.border(2.dp, MaterialTheme.colorScheme.secondary, shape)
			.padding(vertical = 16.dp)
		,
		contentAlignment = Alignment.Center
	) {
		Text(text = text, color = MaterialTheme.colorScheme.onPrimary, fontFamily = fontRobotoMono)
	}
}

@Preview
@Composable
fun DurationComponentPreview() {
	SnaptickTheme {
		DurationComponent(durationList = listOf(30, 60, 90, 0), onSelect = {})
	}
}

@Preview
@Composable
fun DurationItemComponentPreview() {
	SnaptickTheme {
		DurationItemComponent(text = "60 min", onClick = {})
	}
}