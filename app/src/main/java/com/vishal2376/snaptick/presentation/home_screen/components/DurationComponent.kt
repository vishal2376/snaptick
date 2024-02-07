package com.vishal2376.snaptick.presentation.home_screen.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

@Composable
fun DurationComponent() {

}

@Composable
fun DurationItemComponent() {
	val roundShape = RoundedCornerShape(8.dp)
	val startRoundShape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
	val endRoundShape = RoundedCornerShape(bottomEnd = 8.dp, topEnd = 8.dp)

	Box(
		modifier = Modifier
			.border(2.dp, MaterialTheme.colorScheme.secondary, roundShape)
			.padding(16.dp, 8.dp),
		contentAlignment = Alignment.Center
	) {
		Text(text = "30 min", color = Color.White, style = taskTextStyle)
	}
}

@Preview
@Composable
fun DurationComponentPreview() {
	SnaptickTheme {
		DurationComponent()
	}
}

@Preview
@Composable
fun DurationItemComponentPreview() {
	SnaptickTheme {
		DurationItemComponent()
	}
}