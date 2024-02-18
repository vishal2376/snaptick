package com.vishal2376.snaptick.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import com.vishal2376.snaptick.ui.theme.Blue

class SnaptickWidget : GlanceAppWidget() {

	override suspend fun provideGlance(context: Context, id: GlanceId) {
		provideContent {
			WidgetContent()
		}
	}

	@Composable
	private fun WidgetContent() {
		Column(
			modifier = GlanceModifier.fillMaxSize().background(Blue),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(text = "Snaptick Tasks Widget")
		}
	}
}