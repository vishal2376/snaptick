package com.vishal2376.snaptick.presentation.add_edit_screen.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.ui.theme.Red
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

@Composable
fun PriorityComponent(
	title: String,
	backgroundColor: Color,
) {
	Card(
		modifier = Modifier
			.fillMaxWidth(),
		shape = RoundedCornerShape(8.dp),
		colors = CardDefaults.cardColors(containerColor = backgroundColor)
	) {
		Text(
			text = title,
			style = h3TextStyle,
			modifier = Modifier
				.fillMaxWidth()
				.wrapContentSize(Alignment.Center)
				.padding(16.dp)
		)
	}
}

@Preview
@Composable
fun PriorityComponentPreview() {
	SnaptickTheme(
		darkTheme = true,
		dynamicColor = false
	) {
		PriorityComponent(
			"High",
			Red
		)
	}
}