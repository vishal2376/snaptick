package com.vishal2376.snaptick.presentation.home_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.fontRoboto
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.home_screen.components.InfoComponent
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.Green
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.ui.theme.Yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
	Scaffold(topBar = {
		TopAppBar(modifier = Modifier.padding(end = 16.dp),
		          colors = TopAppBarDefaults.topAppBarColors(
			          containerColor = MaterialTheme.colorScheme.background
		          ), title = {
				Text(text = stringResource(id = R.string.app_name), style = h1TextStyle)
			}, actions = {
				Text(
					text = "10", fontSize = 18.sp, fontFamily = fontRoboto,
					fontWeight = FontWeight.Bold
				)
				Spacer(modifier = Modifier.width(4.dp))
				Icon(
					painter = painterResource(id = R.drawable.ic_fire), contentDescription = null,
					tint = Yellow, modifier = Modifier.size(22.dp)
				)
			})
	}, floatingActionButton = {
		FloatingActionButton(
			onClick = { /*TODO*/ }, containerColor = MaterialTheme.colorScheme.secondary,
			contentColor = Color.White
		) {
			Icon(imageVector = Icons.Default.Add, contentDescription = null)
		}
	}) { innerPadding ->
		Column(modifier = Modifier.padding(innerPadding)) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp, 8.dp),
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

			Text(
				text = stringResource(R.string.today_tasks), style = h2TextStyle,
				color = Color.White, modifier = Modifier.padding(16.dp)
			)
		}
	}
}

@Preview
@Composable
private fun HomeScreenPreview() {
	SnaptickTheme(darkTheme = true, dynamicColor = false) {
		HomeScreen()
	}
}