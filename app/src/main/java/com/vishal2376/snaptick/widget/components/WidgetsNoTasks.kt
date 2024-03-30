package com.vishal2376.snaptick.widget.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.ui.theme.LightGray

@Composable
fun WidgetNoTasks(
	modifier: GlanceModifier = GlanceModifier
) {
	val context = LocalContext.current

	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center
	) {
		Text(
			text = context.getString(R.string.no_tasks),
			style = TextStyle(
				color = ColorProvider(color = LightGray),
				fontSize = 30.sp,
				fontWeight = FontWeight.Bold
			)
		)
	}
}