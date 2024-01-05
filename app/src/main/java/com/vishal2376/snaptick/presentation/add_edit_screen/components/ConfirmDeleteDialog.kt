package com.vishal2376.snaptick.presentation.add_edit_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.ui.theme.Blue200
import com.vishal2376.snaptick.ui.theme.Blue500
import com.vishal2376.snaptick.ui.theme.LightGray
import com.vishal2376.snaptick.ui.theme.Red
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

@Composable
fun ConfirmDeleteDialog() {
	Dialog(onDismissRequest = { /*TODO*/ }) {
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			shape = RoundedCornerShape(16.dp),
			colors = CardDefaults.cardColors(containerColor = Blue200)
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						32.dp,
						16.dp
					),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Text(
					text = "Delete Task ?",
					color = Color.White,
					style = h2TextStyle
				)
				Image(
					painter = painterResource(id = R.drawable.delete_task),
					contentDescription = null,
					Modifier.size(150.dp)
				)

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Button(
						onClick = { /*TODO*/ },
						colors = ButtonDefaults.buttonColors(containerColor = Blue500),
						shape = RoundedCornerShape(8.dp),
						border = BorderStroke(
							2.dp,
							LightGray
						)
					) {
						Text(
							text = "Cancel",
							color = LightGray
						)
					}
					Button(
						onClick = { /*TODO*/ },
						colors = ButtonDefaults.buttonColors(containerColor = Red),
						shape = RoundedCornerShape(8.dp),
					) {
						Text(
							text = "Delete",
							color = Color.Black
						)
					}
				}
			}
		}
	}
}

@Preview()
@Composable
fun ConfirmDeleteDialogPreview() {
	SnaptickTheme(
		darkTheme = true,
		dynamicColor = false
	) {
		ConfirmDeleteDialog()
	}
}