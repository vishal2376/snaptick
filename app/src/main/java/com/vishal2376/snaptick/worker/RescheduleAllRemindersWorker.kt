package com.vishal2376.snaptick.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vishal2376.snaptick.data.repositories.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Walks every active task and re-arms its next reminder via
 * [TaskRepository.rescheduleAllReminders]. Triggered on boot, package
 * upgrade, time change, and timezone change.
 *
 * Idempotent: every alarm is cancelled and re-scheduled by the underlying
 * scheduler, so multiple firings of this worker can't double-fire reminders.
 */
@HiltWorker
class RescheduleAllRemindersWorker @AssistedInject constructor(
	@Assisted appContext: Context,
	@Assisted params: WorkerParameters,
	private val repository: TaskRepository,
) : CoroutineWorker(appContext, params) {

	override suspend fun doWork(): Result {
		return try {
			repository.rescheduleAllReminders()
			Result.success()
		} catch (e: Exception) {
			Result.retry()
		}
	}
}
