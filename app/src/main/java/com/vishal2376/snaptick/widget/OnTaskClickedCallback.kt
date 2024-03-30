package com.vishal2376.snaptick.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.interactor.AppWidgetInteractor
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

class OnTaskClickedCallback : ActionCallback {

	@EntryPoint
	@InstallIn(SingletonComponent::class)
	interface GlanceActionEntryPoint {

		fun taskRepository(): TaskRepository

		fun glanceInterceptor(): AppWidgetInteractor

	}

	override suspend fun onAction(
		context: Context,
		glanceId: GlanceId,
		parameters: ActionParameters
	) {
		val taskId: Int = parameters[parameterTaskId] ?: -1

		val entryPoint =
			EntryPoints.get(context.applicationContext, GlanceActionEntryPoint::class.java)
		val taskRepository = entryPoint.taskRepository()

		val task = taskRepository.getTaskById(taskId)
		val updatedTask = task.copy(isCompleted = !task.isCompleted)
		// update the task
		taskRepository.updateTask(updatedTask)

		entryPoint.glanceInterceptor().enqueueWidgetDataWorker()
	}
}
