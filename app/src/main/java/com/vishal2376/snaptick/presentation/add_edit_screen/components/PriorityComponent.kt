package com.vishal2376.snaptick.presentation.add_edit_screen.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.ui.theme.priorityColors
import com.vishal2376.snaptick.util.Priority

@Composable
fun PriorityComponent(
	defaultSortTask: Priority = Priority.LOW,
	onSelect: (Priority) -> Unit
) {

	var selectedOption by remember {
		mutableStateOf(defaultSortTask)
	}

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 32.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Priority.entries.forEach {
			PriorityItemComponent(
				title = it.displayText,
				backgroundColor = priorityColors[it.ordinal],
				isSelected = selectedOption == it,
				modifier = Modifier.weight(1f)
			) {
				onSelect(it)
				selectedOption = it
			}
		}
	}
}


@Composable
fun PriorityItemComponent(
	title: String,
	backgroundColor: Color,
	isSelected: Boolean,
	modifier: Modifier,
	onClick: () -> Unit
) {
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(4.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {

		Box(
			modifier = Modifier
				.fillMaxWidth()
				.background(backgroundColor, RoundedCornerShape(8.dp))
				.clickable { onClick() },
			contentAlignment = Alignment.Center
		) {
			Text(
				text = title,
				style = h3TextStyle,
				modifier = Modifier.padding(16.dp, 24.dp)
			)
		}

		if (isSelected) {

			val animValue = remember { Animatable(initialValue = 0f) }

			LaunchedEffect(Unit) {
				animValue.animateTo(1f, tween(300))
			}

			Box(
				modifier = Modifier
					.width(40.dp * animValue.value)
					.height(4.dp)
					.background(backgroundColor, RoundedCornerShape(8.dp))
			)
		}
	}
}

@Preview()
@Composable
fun PriorityComponentPreview() {
	SnaptickTheme(
		darkTheme = true,
		dynamicColor = false
	) {
		PriorityComponent(Priority.LOW, {})
	}
}