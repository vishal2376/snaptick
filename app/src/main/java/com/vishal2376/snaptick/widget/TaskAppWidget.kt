package com.vishal2376.snaptick.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.util.DummyTasks
import com.vishal2376.snaptick.widget.presentation.TaskWidget
import com.vishal2376.snaptick.widget.ui.theme.SnaptickWidgetTheme

class TaskAppWidget : GlanceAppWidget() {
	override suspend fun provideGlance(context: Context, id: GlanceId) {
		provideContent {
			SnaptickWidgetTheme(theme = AppTheme.Amoled) {
				val todayTasks = DummyTasks.dummyTasks
				TaskWidget(todayTasks)
			}
		}
	}
}