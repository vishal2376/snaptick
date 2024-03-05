package com.vishal2376.snaptick.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

object SnackbarController {

	// Triggering event
	val _msg = mutableStateOf<String?>("")
	val msg: State<String?> = _msg

	var delay: Long = 1000
	var actionText: String? = null
	var onClickAction: () -> Unit = {}


	fun showCustomSnackbar(
		msg: String?,
		delay: Long = 3000,
		actionText: String? = null,
		onClickAction: () -> Unit = {}
	) {
		this.delay = delay
		this._msg.value = msg
		this.actionText = actionText
		this.onClickAction = onClickAction
	}

}

@Composable
fun CustomSnackBar() {

	val snackBarMessage = SnackbarController.msg.value
	val delay = SnackbarController.delay
	val actionText = SnackbarController.actionText
	val onClickAction: () -> Unit = SnackbarController.onClickAction


	if (snackBarMessage.isNullOrBlank().not()) {
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
				delay(delay)
				SnackbarController._msg.value = null
			}
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.background(Color.Red, shape = RoundedCornerShape(16.dp))
					.padding(8.dp),
				contentAlignment = Center,
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Text(
						text = snackBarMessage ?: "",
						modifier = Modifier.weight(1f),
						style = TextStyle.Default,
						color = Color.White,
						fontSize = 16.sp,
						fontWeight = FontWeight.Bold
					)
					if (!actionText.isNullOrBlank())
						Button(
							onClick = {
								onClickAction.invoke()
							},
							colors = ButtonDefaults.buttonColors(
								containerColor = Color.White,
								contentColor = Color.Red
							),
							shape = RoundedCornerShape(16.dp),
						) {
							Text(
								text = actionText,
								fontWeight = FontWeight.Bold,
								fontSize = 16.sp,
								modifier = Modifier.padding(vertical = 8.dp)
							)
						}

				}
			}
		}
	}
}


@Preview(showSystemUi = true)
@Composable
fun SnackBarPreview() {
	SnackbarController.showCustomSnackbar("Hello", actionText = "Ok")
	CustomSnackBar()
}