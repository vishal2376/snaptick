package com.vishal2376.snaptick.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.vishal2376.snaptick.widget.worker.WidgetUpdateWorker

/**
 * Action callback for the refresh button in the widget.
 * Triggers an immediate widget data sync via WidgetUpdateWorker.
 */
class RefreshWidgetAction : ActionCallback {
	override suspend fun onAction(
		context: Context,
		glanceId: GlanceId,
		parameters: ActionParameters
	) {
		WidgetUpdateWorker.enqueueWorker(context)
	}
}
