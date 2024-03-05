package com.vishal2376.snaptick.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.util.showCustomSnackbar
import kotlinx.coroutines.delay

@Composable
fun CustomSnackBar() {
	val isDialogVisible = showCustomSnackbar.value

	if (isDialogVisible.isNullOrBlank().not()) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp)
				.clickable(indication = null,
					interactionSource = remember { MutableInteractionSource() }) {
					//on click
				}
				.background(Color.Transparent),
			contentAlignment = BottomCenter,
		) {
			LaunchedEffect(Unit) {
				delay(3000)
				showCustomSnackbar(null)
			}
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.background(Color.Red, shape = RoundedCornerShape(8.dp))
					.padding(16.dp),
				contentAlignment = Center,
			) {

				Text(
					text = isDialogVisible ?: "",
					modifier = Modifier.fillMaxWidth(),
					style = TextStyle.Default,
					color = Color.White
				)
			}

		}
	}
}
