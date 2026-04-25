package com.vishal2376.snaptick.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.vishal2376.snaptick.widget.di.WidgetEntryPoint
import com.vishal2376.snaptick.widget.worker.WidgetUpdateWorker
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

/**
 * Toggles task completion from the widget.
 *
 * Routes through `TaskRepository` (via Hilt entry point) so the same
 * per-date completion semantics apply as in the home screen:
 *
 * - One-off task: flips `Task.isCompleted`. Repository cancels the alarm
 *   and re-schedules if needed.
 * - Repeat template: writes (or removes) a row in `task_completions`
 *   for today, leaving the template untouched.
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
			val repo = EntryPointAccessors
				.fromApplication(context.applicationContext, WidgetEntryPoint::class.java)
				.taskRepository()

			val task = repo.getTaskById(taskId) ?: return@withContext

			if (task.isRepeated) {
				val today = LocalDate.now()
				if (repo.isCompletedOn(task.uuid, today)) {
					repo.unmarkCompletedForDate(task.uuid, today)
				} else {
					repo.markCompletedForDate(task.uuid, today)
				}
			} else {
				repo.updateTask(task.copy(isCompleted = !task.isCompleted))
			}

			WidgetUpdateWorker.enqueueWorker(context)
		}
	}
}
