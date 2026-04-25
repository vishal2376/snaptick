package com.vishal2376.snaptick.presentation.onboarding.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle

private data class WelcomeFeature(
	val iconRes: Int,
	val titleRes: Int,
)

@Composable
fun WelcomePage(currentTheme: AppTheme) {
	val features = listOf(
		WelcomeFeature(R.drawable.ic_task_list, R.string.create_and_edit_tasks),
		WelcomeFeature(R.drawable.ic_timer, R.string.pomodoro_timer),
		WelcomeFeature(R.drawable.ic_refresh, R.string.repeatable_tasks_with_notification),
		WelcomeFeature(R.drawable.ic_calendar_sync, R.string.manage_tasks_in_calendar_view),
		WelcomeFeature(R.drawable.ic_theme, R.string.modern_ui_with_cool_themes),
		WelcomeFeature(R.drawable.ic_translate, R.string.available_in_15_languages),
	)

	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 24.dp, vertical = 24.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Box(
			modifier = Modifier
				.size(112.dp)
				.background(
					MaterialTheme.colorScheme.primaryContainer,
					CircleShape
				),
			contentAlignment = Alignment.Center
		) {
			Image(
				painter = painterResource(
					if (currentTheme == AppTheme.Amoled) R.drawable.app_logo_amoled
					else R.drawable.app_logo
				),
				contentDescription = null,
				modifier = Modifier.size(72.dp)
			)
		}
		Spacer(Modifier.height(20.dp))
		Text(
			text = stringResource(R.string.app_name),
			style = h1TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
			fontSize = MaterialTheme.typography.displaySmall.fontSize
		)
		Spacer(Modifier.height(8.dp))
		Text(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 12.dp),
			text = stringResource(R.string.app_description),
			style = taskTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer,
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.height(28.dp))
		Text(
			modifier = Modifier.fillMaxWidth(),
			text = "What you get",
			style = h3TextStyle,
			color = MaterialTheme.colorScheme.onBackground
		)
		Spacer(Modifier.height(12.dp))
		Column(
			modifier = Modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(10.dp)
		) {
			features.chunked(2).forEach { rowItems ->
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(10.dp)
				) {
					rowItems.forEach { feature ->
						FeatureCard(
							iconRes = feature.iconRes,
							title = stringResource(feature.titleRes),
							modifier = Modifier.weight(1f)
						)
					}
					if (rowItems.size == 1) {
						Spacer(Modifier.weight(1f))
					}
				}
			}
		}
	}
}

@Composable
private fun FeatureCard(
	iconRes: Int,
	title: String,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.background(
				MaterialTheme.colorScheme.primaryContainer,
				RoundedCornerShape(16.dp)
			)
			.padding(horizontal = 14.dp, vertical = 16.dp),
		horizontalAlignment = Alignment.Start,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Box(
			modifier = Modifier
				.size(36.dp)
				.background(
					MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
					RoundedCornerShape(10.dp)
				),
			contentAlignment = Alignment.Center
		) {
			Icon(
				painter = painterResource(iconRes),
				contentDescription = null,
				tint = MaterialTheme.colorScheme.primary,
				modifier = Modifier.size(20.dp)
			)
		}
		Text(
			text = title,
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer
		)
	}
}
