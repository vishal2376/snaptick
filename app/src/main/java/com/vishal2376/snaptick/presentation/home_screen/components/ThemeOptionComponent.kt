package com.vishal2376.snaptick.presentation.home_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.ui.theme.AppTheme
import com.vishal2376.snaptick.ui.theme.Black200
import com.vishal2376.snaptick.ui.theme.Black500
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.Blue200
import com.vishal2376.snaptick.ui.theme.Blue500
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.ui.theme.White200
import com.vishal2376.snaptick.ui.theme.White500

@Composable
fun ThemeOptionComponent(
	modifier: Modifier = Modifier,
	defaultTheme: AppTheme,
	onSelect: (AppTheme) -> Unit
) {
	var selectedOption by remember { mutableStateOf(defaultTheme) }

	val bgColorList = arrayOf(White500, Blue500, Black500)
	val borderColorList = arrayOf(White200, Blue200, Black200)

	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		AppTheme.entries.forEach {
			ThemeOptionItem(
				bgColor = bgColorList[it.ordinal],
				borderColor = borderColorList[it.ordinal],
				isSelected = selectedOption == it
			) {
				onSelect(it)
				selectedOption = it
			}
		}
	}
}


@Composable
fun ThemeOptionItem(
	bgColor: Color,
	borderColor: Color,
	isSelected: Boolean,
	onClick: () -> Unit
) {
	Box(
		modifier = Modifier
			.size(32.dp)
			.border(1.dp, if (isSelected) Blue else Color.Transparent, CircleShape),
		contentAlignment = Alignment.Center
	) {
		Box(
			modifier = Modifier
				.size(24.dp)
				.border(2.dp, borderColor, CircleShape)
				.background(bgColor, CircleShape)
				.clickable { onClick() }
		) {}
	}

}

@Preview
@Composable
fun ThemeOptionComponentPreview() {
	SnaptickTheme {
		ThemeOptionComponent(defaultTheme = AppTheme.Light, onSelect = {})
	}
}