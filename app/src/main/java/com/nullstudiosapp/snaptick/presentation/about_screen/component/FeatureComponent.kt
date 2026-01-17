package com.nullstudiosapp.snaptick.presentation.about_screen.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullstudiosapp.snaptick.R
import com.nullstudiosapp.snaptick.presentation.common.h3TextStyle
import com.nullstudiosapp.snaptick.presentation.common.infoTextStyle

@Composable
fun FeaturesComponent(modifier: Modifier = Modifier) {
	Column(
		modifier = modifier
			.padding(32.dp, 0.dp)
			.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		FeatureItem(icon = "📝", text = stringResource(R.string.create_and_edit_tasks))
		FeatureItem(icon = "⏲️", text = stringResource(R.string.pomodoro_timer))
		FeatureItem(icon = "🔄", text = stringResource(R.string.sort_tasks))
		FeatureItem(icon = "💾", text = stringResource(R.string.backup_restore_data))
		FeatureItem(icon = "⏰", text = stringResource(R.string.analyze_free_time))
		FeatureItem(icon = "😴", text = stringResource(R.string.set_sleep_time))
		FeatureItem(icon = "🗓️", text = stringResource(R.string.manage_tasks_in_calendar_view))
		FeatureItem(icon = "🔁", text = stringResource(R.string.repeatable_tasks_with_notification))
		FeatureItem(icon = "🎬", text = stringResource(R.string.smooth_animations))
		FeatureItem(icon = "🎨", text = stringResource(R.string.modern_ui_with_cool_themes))
		FeatureItem(icon = "🌐", text = stringResource(R.string.available_in_15_languages))
		FeatureItem(icon = "🧩", text = stringResource(R.string.create_widgets))
	}
}

@Composable
fun FeatureItem(icon: String, text: String) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		Text(text = icon, style = infoTextStyle, color = MaterialTheme.colorScheme.onBackground)
		Spacer(modifier = Modifier.width(8.dp))
		Text(text = text, style = h3TextStyle, color = MaterialTheme.colorScheme.onBackground)
	}
}
