package com.vishal2376.snaptick.presentation.home_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.main.MainEvent
import com.vishal2376.snaptick.ui.theme.AppTheme
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.Red

@Composable
fun NavigationDrawerComponent(appTheme: AppTheme, onMainEvent: (MainEvent) -> Unit) {

	val context = LocalContext.current

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = Modifier
			.fillMaxWidth(0.8f)
			.fillMaxHeight()
	) {

		Text(
			text = "Temporary Design",
			style = h1TextStyle,
			color = Color.White
		)
		Spacer(modifier = Modifier.height(50.dp))

		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Text(
				text = "Amoled Theme",
				style = taskTextStyle,
				color = Color.White
			)
			Switch(
				checked = appTheme == AppTheme.Amoled,
				onCheckedChange = {
					onMainEvent(MainEvent.ToggleAmoledTheme(it, context))
				},
				colors = SwitchDefaults.colors(
					checkedThumbColor = Blue,
					checkedTrackColor = MaterialTheme.colorScheme.secondary,
					uncheckedTrackColor = MaterialTheme.colorScheme.secondary
				)
			)
		}

		Spacer(modifier = Modifier.height(8.dp))

		Button(
			onClick = { throw RuntimeException("Crash Testing") },
			colors = ButtonDefaults.buttonColors(containerColor = Red),
			shape = RoundedCornerShape(8.dp),
		) {
			Text(text = "Crash Testing")
		}

	}

}

@Preview(widthDp = 350)
@Composable
fun NavigationDrawerComponentPreview() {
	NavigationDrawerComponent(AppTheme.Amoled, {})
}