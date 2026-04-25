package com.vishal2376.snaptick.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.ReminderScheduler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Re-arms the next reminder for a single task after its previous fire. Used
 * for repeating tasks: the alarm fires, the receiver posts the notification,
 * then this worker computes and arms the next occurrence.
 *
 * Looks up the latest task state in the DB (titles/times can have changed
 * since the original alarm was scheduled) before handing off to the scheduler.
 */
@HiltWorker
class RescheduleSingleReminderWorker @AssistedInject constructor(
	@Assisted appContext: Context,
	@Assisted params: WorkerParameters,
	private val repository: TaskRepository,
	private val reminderScheduler: ReminderScheduler,
) : CoroutineWorker(appContext, params) {

	override suspend fun doWork(): Result {
		val taskId = inputData.getInt(Constants.TASK_ID, -1)
		if (taskId <= 0) return Result.failure()
		val task = repository.getTaskById(taskId) ?: return Result.success()
		reminderScheduler.schedule(task)
		return Result.success()
	}
}
