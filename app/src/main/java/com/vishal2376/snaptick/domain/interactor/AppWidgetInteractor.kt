package com.vishal2376.snaptick.domain.interactor

interface AppWidgetInteractor {

	/**
	 * This worker is mainly used for updates in the widget,
	 * Updated may include completing a tasks also adding a new one
	 */
	fun enqueueWidgetDataWorker()

	/**
	 * Periodic Worker which runs daily at midnight updating the task,
	 * This worker is to be enqueued when the widget is enabled
	 */
	fun enqueuePeriodicWidgetUpdateWorker()

	/**
	 * Periodic Worker should be cancelled when the widget is disabled
	 */
	fun cancelPeriodicWidgetUpdateWorker()

	fun cancelWidgetDateWorker()
}