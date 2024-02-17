package com.vishal2376.snaptick.util

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.worker.NotificationWorker
import com.vishal2376.snaptick.worker.RepeatTaskWorker
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object WorkManagerHelper {

	fun scheduleNotification(task: Task) {
		val data = Data.Builder().putString(Constants.TASK_UUID, task.uuid)
			.putString(Constants.TASK_TITLE, task.title)
			.putString(Constants.TASK_TIME, task.getFormattedTime())
			.build()

		val startTimeSec = task.startTime.toSecondOfDay()
		val currentTimeSec = LocalTime.now().toSecondOfDay()
		val delaySec = startTimeSec - currentTimeSec

		if (delaySec > 0) {

			cancelNotification(task.uuid)

			val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
				.setInputData(data)
				.setInitialDelay(delaySec.toLong(), TimeUnit.SECONDS)
				.addTag(task.uuid)
				.build()

			// Enqueue the work request with WorkManager
			WorkManager.getInstance().enqueue(workRequest)
		}
	}

	fun scheduleRepeatTask(task: Task) {
		val taskString = Gson().toJson(task)
		val data = Data.Builder().putString(Constants.TASK, taskString).build()
		val tag = "Repeat-${task.uuid}"

		val midNightSec = LocalTime.MIDNIGHT.toSecondOfDay()
		val currentTimeSec = LocalTime.now().toSecondOfDay()
		val delaySec = midNightSec - currentTimeSec

		val workRequest = PeriodicWorkRequestBuilder<RepeatTaskWorker>(
			repeatInterval = 1,
			repeatIntervalTimeUnit = TimeUnit.DAYS
		).setInitialDelay(delaySec.toLong(), TimeUnit.SECONDS)
			.setInputData(data)
			.addTag(tag)
			.build()

		// Enqueue the work request with WorkManager
		WorkManager.getInstance().enqueue(workRequest)
	}

	fun cancelNotification(taskUUID: String) {
		WorkManager.getInstance().cancelAllWorkByTag(taskUUID)
	}

	fun cancelRepeatTask(taskUUID: String) {
		val tag = "Repeat-$taskUUID"
		WorkManager.getInstance().cancelAllWorkByTag(tag)
	}
}