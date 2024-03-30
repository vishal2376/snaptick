package com.vishal2376.snaptick.widget.interactor

import android.content.Context
import com.vishal2376.snaptick.domain.interactor.AppWidgetInteractor
import com.vishal2376.snaptick.widget.worker.WidgetTaskUpdateDataWorker

class AppWidgetInteractorImpl(
	private val context: Context
) : AppWidgetInteractor {

	override fun enqueueWidgetDataWorker() =
		WidgetTaskUpdateDataWorker.enqueueWorker(context)

	override fun cancelWidgetDateWorker() =
		WidgetTaskUpdateDataWorker.cancelWorker(context)


	override fun enqueuePeriodicWidgetUpdateWorker() =
		WidgetTaskUpdateDataWorker.enqueuePeriodicWorker(context)

	override fun cancelPeriodicWidgetUpdateWorker() =
		WidgetTaskUpdateDataWorker.cancelPeriodicWorker(context)

}