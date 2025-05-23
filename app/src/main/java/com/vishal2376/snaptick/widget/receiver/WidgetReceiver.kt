package com.vishal2376.snaptick.widget.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.vishal2376.snaptick.widget.TaskAppWidget

class WidgetReceiver : GlanceAppWidgetReceiver() {
	override val glanceAppWidget: GlanceAppWidget
		get() = TaskAppWidget()
}