package com.vishal2376.snaptick.presentation.onboarding.pages

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.AppTheme
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import com.vishal2376.snaptick.presentation.common.animation.SnaptickMotion
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.DarkGreen
import com.vishal2376.snaptick.ui.theme.LightGreen
import com.vishal2376.snaptick.ui.theme.Red
import com.vishal2376.snaptick.ui.theme.Yellow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class WelcomeFeature(
	val iconRes: Int,
	val titleRes: Int,
	val subtitle: String,
	val accent: Color,
)

@Composable
fun WelcomePage(currentTheme: AppTheme) {
	val features = remember {
		listOf(
			WelcomeFeature(
				R.drawable.ic_task_list, R.string.create_and_edit_tasks,
				"Plan your day with start, end, priority", Blue
			),
			WelcomeFeature(
				R.drawable.ic_timer, R.string.pomodoro_timer,
				"Built-in focus timer for every task", Red
			),
			WelcomeFeature(
				R.drawable.ic_refresh, R.string.repeatable_tasks_with_notification,
				"Daily / weekly habits with reminders", Yellow
			),
			WelcomeFeature(
				R.drawable.ic_calendar_sync, R.string.manage_tasks_in_calendar_view,
				"See your week and month at a glance", LightGreen
			),
			WelcomeFeature(
				R.drawable.ic_theme, R.string.modern_ui_with_cool_themes,
				"Light, Dark, and battery-friendly Amoled", DarkGreen
			),
			WelcomeFeature(
				R.drawable.ic_translate, R.string.available_in_15_languages,
				"Use Snaptick in your language", Blue
			),
		)
	}

	val scrollState = rememberScrollState()

	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(scrollState)
			.padding(horizontal = 24.dp, vertical = 16.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Image(
			painter = painterResource(
				if (currentTheme == AppTheme.Amoled) R.drawable.app_logo_amoled
				else R.drawable.app_logo
			),
			contentDescription = null,
			modifier = Modifier.size(80.dp)
		)
		Spacer(Modifier.height(14.dp))
		Text(
			text = stringResource(R.string.app_name),
			style = h3TextStyle.copy(fontSize = 28.sp),
			color = MaterialTheme.colorScheme.onBackground
		)
		Spacer(Modifier.height(6.dp))
		Text(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 12.dp),
			text = stringResource(R.string.app_description),
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.height(24.dp))

		features.forEachIndexed { index, feature ->
			FeatureRow(
				index = index,
				iconRes = feature.iconRes,
				title = stringResource(feature.titleRes),
				subtitle = feature.subtitle,
				accent = feature.accent
			)
			if (index != features.lastIndex) Spacer(Modifier.height(10.dp))
		}
	}
}

@Composable
private fun FeatureRow(
	index: Int,
	iconRes: Int,
	title: String,
	subtitle: String,
	accent: Color,
) {
	val alpha = remember { Animatable(0f) }
	val translate = remember { Animatable(28f) }
	val density = LocalDensity.current

	LaunchedEffect(Unit) {
		val delayMs = (index.coerceAtMost(SnaptickMotion.MAX_STAGGERED_ITEMS) * 110L)
		delay(delayMs)
		coroutineScope {
			launch {
				alpha.animateTo(
					targetValue = 1f,
					animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing)
				)
			}
			launch {
				translate.animateTo(
					targetValue = 0f,
					animationSpec = tween(durationMillis = 560, easing = FastOutSlowInEasing)
				)
			}
		}
	}

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.graphicsLayer {
				this.alpha = alpha.value
				this.translationY = with(density) { translate.value.dp.toPx() }
			}
			.background(
				MaterialTheme.colorScheme.primaryContainer,
				RoundedCornerShape(16.dp)
			)
			.padding(horizontal = 14.dp, vertical = 14.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(14.dp)
	) {
		Box(
			modifier = Modifier
				.size(44.dp)
				.background(accent.copy(alpha = 0.22f), RoundedCornerShape(12.dp)),
			contentAlignment = Alignment.Center
		) {
			Icon(
				painter = painterResource(iconRes),
				contentDescription = null,
				tint = accent,
				modifier = Modifier.size(22.dp)
			)
		}
		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = title,
				style = h3TextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
			Spacer(Modifier.height(2.dp))
			Text(
				text = subtitle,
				style = infoDescTextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
			)
		}
	}
}

