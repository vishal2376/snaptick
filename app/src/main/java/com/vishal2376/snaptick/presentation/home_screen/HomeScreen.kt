package com.vishal2376.snaptick.presentation.home_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.home_screen.components.InfoComponent
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.Green

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen() {
	Scaffold {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {

			InfoComponent(
				title = "Completed", desc = "1/3 Tasks", icon = R.drawable.ic_task_list,
				backgroundColor = Green, modifier = Modifier.weight(1f)
			)

			InfoComponent(
				title = "Free Time", desc = "8 hours", icon = R.drawable.ic_clock,
				backgroundColor = Blue, modifier = Modifier.weight(1f)
			)

		}
	}
}

@Preview
@Composable
private fun HomeScreenPreview() {
	HomeScreen()
}