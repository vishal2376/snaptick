package com.nullstudiosapp.snaptick.widget.interactor

import android.content.Context
import com.nullstudiosapp.snaptick.domain.interactor.AppWidgetInteractor
import com.nullstudiosapp.snaptick.widget.worker.WidgetUpdateWorker

/**
 * Implementation of AppWidgetInteractor that uses the new WidgetUpdateWorker.
 * This replaces the old implementation from widget_old package.
 */
class AppWidgetInteractorImpl(
	private val context: Context
) : AppWidgetInteractor {

	override fun enqueueWidgetDataWorker() =
		WidgetUpdateWorker.enqueueWorker(context)

	override fun cancelWidgetDateWorker() =
		WidgetUpdateWorker.cancelWorker(context)

	override fun enqueuePeriodicWidgetUpdateWorker() =
		WidgetUpdateWorker.enqueuePeriodicWorker(context)

	override fun cancelPeriodicWidgetUpdateWorker() =
		WidgetUpdateWorker.cancelPeriodicWorker(context)
}
