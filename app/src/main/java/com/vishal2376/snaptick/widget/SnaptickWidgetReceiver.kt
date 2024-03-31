package com.vishal2376.snaptick.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.vishal2376.snaptick.domain.interactor.AppWidgetInteractor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val LOGGER_TAG = "GLANCE_APP_WIDGET"

@AndroidEntryPoint
class SnaptickWidgetReceiver : GlanceAppWidgetReceiver() {

	@Inject
	lateinit var interceptor: AppWidgetInteractor

	override val glanceAppWidget: GlanceAppWidget
		get() = SnaptickWidget

	override fun onEnabled(context: Context) {
		super.onEnabled(context)
		Log.d(LOGGER_TAG,"ONE_TIME_WORKER_ENQUEUED")
		// enqueue the current worker for current update
		interceptor.enqueueWidgetDataWorker()
		Log.d(LOGGER_TAG, "PERIODIC_WORKER_ENQUEUED")
		// enqueue the periodic worker
		interceptor.enqueuePeriodicWidgetUpdateWorker()
	}

	override fun onDisabled(context: Context?) {
		Log.d(LOGGER_TAG, "PERIODIC_WORKER_CANCELED")
		// cancel worker on remove
		interceptor.cancelPeriodicWidgetUpdateWorker()
		super.onDisabled(context)
	}
}