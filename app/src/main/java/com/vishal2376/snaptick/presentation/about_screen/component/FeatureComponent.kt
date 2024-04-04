package com.vishal2376.snaptick.presentation.about_screen.component

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
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.infoTextStyle

@Composable
fun FeaturesComponent(modifier: Modifier = Modifier) {
	Column(
		modifier = modifier
			.padding(32.dp,0.dp)
			.verticalScroll(rememberScrollState())
		, verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		FeatureItem(icon = "📝", text = "Create and Edit Tasks")
		FeatureItem(icon = "⏲️", text = "Pomodoro Timer")
		FeatureItem(icon = "🔄", text = "Sort Tasks")
		FeatureItem(icon = "⏰", text = "Analyze Free Time")
		FeatureItem(icon = "😴", text = "Set Sleep Time")
		FeatureItem(icon = "🗓️", text = "Manage tasks in Calendar View")
		FeatureItem(icon = "🔁", text = "Repeatable Tasks with Notification")
		FeatureItem(icon = "🎬", text = "Smooth Animations")
		FeatureItem(icon = "🎨", text = "Modern UI with Cool Themes")
		FeatureItem(icon = "🌐", text = "Available in 15+ Languages")
		FeatureItem(icon = "🧩", text = "Create Widgets")
	}
}

@Composable
fun FeatureItem(icon: String, text: String) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		Text(text = icon, style = infoTextStyle, color = MaterialTheme.colorScheme.onPrimary)
		Spacer(modifier = Modifier.width(8.dp))
		Text(text = text, style = h3TextStyle, color = MaterialTheme.colorScheme.onPrimary)
	}
}
