package com.vishal2376.snaptick.widget.presentation.components

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.vishal2376.snaptick.R

@Composable
@GlanceComposable
fun EmptyTaskWidgetComponent(
	onAddClick: () -> Intent,
) {
	val context = LocalContext.current

	Column(
		modifier = GlanceModifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = context.getString(R.string.no_tasks),
			style = TextStyle(
				color = GlanceTheme.colors.onBackground,
				fontSize = 30.sp,
				fontWeight = FontWeight.Bold
			),
		)

		Spacer(modifier = GlanceModifier.height(24.dp))

		Button(
			modifier = GlanceModifier,
			text = context.getString(R.string.add_task),
			style = TextStyle(
				color = GlanceTheme.colors.onBackground,
				fontSize = 14.sp,
				fontWeight = FontWeight.Bold
			),
			onClick = actionStartActivity(onAddClick())
		)
	}
}