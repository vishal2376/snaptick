package com.vishal2376.snaptick.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
	Scaffold(topBar = {
		TopAppBar(
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = MaterialTheme.colorScheme.background,
			),
			title = {
				Text(
					text = stringResource(R.string.settings),
					style = h1TextStyle
				)
			},
			navigationIcon = {
				IconButton(onClick = { }) {
					Icon(
						imageVector = Icons.Rounded.ArrowBack,
						contentDescription = null
					)
				}
			},
		)
	}) { innerPadding ->
		Column(modifier = Modifier.padding(innerPadding)) {

		}
	}
}

@Preview
@Composable
fun SettingsScreenPreview() {
	SnaptickTheme {
		SettingsScreen()
	}
}