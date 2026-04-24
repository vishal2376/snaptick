package com.vishal2376.snaptick.presentation.onboarding.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle

@Composable
fun WelcomePage(currentTheme: AppTheme) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(32.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Image(
			painter = painterResource(
				if (currentTheme == AppTheme.Amoled) R.drawable.app_logo_amoled
				else R.drawable.app_logo
			),
			contentDescription = null,
			modifier = Modifier.size(96.dp)
		)
		Spacer(Modifier.height(24.dp))
		Text(
			text = stringResource(R.string.app_name),
			style = h1TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
			fontSize = MaterialTheme.typography.displayMedium.fontSize
		)
		Spacer(Modifier.height(12.dp))
		Text(
			modifier = Modifier.fillMaxWidth(),
			text = stringResource(R.string.app_description),
			style = taskTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer,
			textAlign = TextAlign.Center
		)
	}
}
