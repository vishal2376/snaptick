package com.vishal2376.snaptick.widget.receiver

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.vishal2376.snaptick.widget.TaskAppWidget
import com.vishal2376.snaptick.widget.worker.WidgetUpdateWorker

/**
 * Widget receiver that handles widget lifecycle events.
 * Triggers data sync when widget is enabled or updated.
 */
class WidgetReceiver : GlanceAppWidgetReceiver() {

	override val glanceAppWidget: GlanceAppWidget = TaskAppWidget()

	override fun onEnabled(context: Context) {
		super.onEnabled(context)
		// Start periodic updates when first widget is added
		WidgetUpdateWorker.enqueuePeriodicWorker(context)
		// Also do an immediate sync
		WidgetUpdateWorker.enqueueWorker(context)
	}

	override fun onUpdate(
		context: Context,
		appWidgetManager: AppWidgetManager,
		appWidgetIds: IntArray
	) {
		super.onUpdate(context, appWidgetManager, appWidgetIds)
		// Sync data when widget is updated
		WidgetUpdateWorker.enqueueWorker(context)
	}

	override fun onDisabled(context: Context) {
		super.onDisabled(context)
		// Stop periodic updates when last widget is removed
		WidgetUpdateWorker.cancelPeriodicWorker(context)
	}
}