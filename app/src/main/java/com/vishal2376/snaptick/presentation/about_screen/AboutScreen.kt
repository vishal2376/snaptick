package com.vishal2376.snaptick.presentation.about_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.about_screen.component.FeaturesComponent
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.fontRobotoMono
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.main.MainState
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.ui.theme.Yellow
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.openUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
	appState: MainState,
	onBack: () -> Unit
) {
	val context = LocalContext.current
	val repoUrl = "${Constants.GITHUB}/snaptick"

	Scaffold(topBar = {
		TopAppBar(
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = MaterialTheme.colorScheme.background,
			),
			title = {
				Text(
					text = stringResource(R.string.about),
					style = h1TextStyle
				)
			},
			navigationIcon = {
				IconButton(onClick = { onBack() }) {
					Icon(
						imageVector = Icons.Rounded.ArrowBack,
						contentDescription = null
					)
				}
			},
		)
	}) { innerPadding ->

		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(innerPadding), verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Image(
					painter = painterResource(if (appState.theme == AppTheme.Amoled) R.drawable.app_logo_amoled else R.drawable.app_logo),
					contentDescription = null,
					modifier = Modifier.size(64.dp),
				)
				Spacer(modifier = Modifier.height(16.dp))
				Text(
					text = stringResource(R.string.app_name),
					style = h2TextStyle,
					color = MaterialTheme.colorScheme.onPrimary
				)
				Text(
					text = stringResource(R.string.buildVersion, appState.buildVersion),
					fontFamily = fontRobotoMono,
					fontSize = 15.sp,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.onPrimary
				)
			}
			Divider(color = MaterialTheme.colorScheme.secondary)
			Text(
				modifier = Modifier.padding(32.dp, 0.dp),
				text = stringResource(R.string.app_description),
				color = MaterialTheme.colorScheme.onSecondary,
				style = h3TextStyle
			)

			Text(
				text = stringResource(R.string.source_code),
				modifier = Modifier
					.fillMaxWidth()
					.clickable {
						openUrl(context, repoUrl)
					},
				fontStyle = FontStyle.Italic,
				style = taskTextStyle,
				textDecoration = TextDecoration.Underline,
				textAlign = TextAlign.Center
			)

			Divider(color = MaterialTheme.colorScheme.secondary)
			Text(
				modifier = Modifier.padding(32.dp, 0.dp),
				text = stringResource(R.string.features),
				color = Yellow,
				style = h2TextStyle,
			)
			FeaturesComponent()
		}
	}
}

@Preview
@Composable
fun AboutScreenPreview() {
	SnaptickTheme {
		AboutScreen(MainState(), {})
	}
}