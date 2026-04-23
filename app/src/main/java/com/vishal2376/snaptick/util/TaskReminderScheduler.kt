package com.vishal2376.snaptick.util

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.utils.formatTaskTime
import com.vishal2376.snaptick.worker.NotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskReminderScheduler @Inject constructor(
	@ApplicationContext private val context: Context
) {

	fun schedule(task: Task) {
		if (!task.reminder || task.isCompleted) return

		cancel(task.uuid)

		val startDateTimeSec =
			LocalDateTime.of(task.date, task.startTime).toEpochSecond(ZoneOffset.UTC)
		val currentDateTimeSec = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
		val delaySec = startDateTimeSec - currentDateTimeSec

		if (delaySec <= 0) return

		val data = Data.Builder()
			.putString(Constants.TASK_UUID, task.uuid)
			.putString(Constants.TASK_TITLE, task.title)
			.putString(Constants.TASK_TIME, formatTaskTime(task))
			.build()

		val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
			.setInitialDelay(delaySec, TimeUnit.SECONDS)
			.setInputData(data)
			.addTag(task.uuid)
			.build()

		WorkManager.getInstance(context).enqueue(workRequest)
	}

	fun cancel(taskUuid: String) {
		WorkManager.getInstance(context).cancelAllWorkByTag(taskUuid)
	}
}
