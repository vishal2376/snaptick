package com.vishal2376.snaptick.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.vishal2376.snaptick.data.local.TaskDatabase
import com.vishal2376.snaptick.widget.worker.WidgetUpdateWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Action callback for toggling task completion directly from the widget.
 * This updates the task in the database and refreshes the widget.
 */
class ToggleTaskAction : ActionCallback {

	companion object {
		val TaskIdKey = ActionParameters.Key<Int>("task_id")
	}

	override suspend fun onAction(
		context: Context,
		glanceId: GlanceId,
		parameters: ActionParameters
	) {
		val taskId = parameters[TaskIdKey] ?: return

		withContext(Dispatchers.IO) {
			// Get task from database
			val database = TaskDatabase.getInstance(context)
			val taskDao = database.taskDao()
			val task = taskDao.getTaskById(taskId) ?: return@withContext

			// Toggle completion status
			val updatedTask = task.copy(isCompleted = !task.isCompleted)
			taskDao.updateTask(updatedTask)

			// Refresh widget
			WidgetUpdateWorker.enqueueWorker(context)
		}
	}
}
