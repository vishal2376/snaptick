package com.vishal2376.snaptick.util

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.gson.Gson
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.worker.NotificationWorker
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

object WorkManagerHelper {

	fun scheduleNotification(task: Task) {
		val taskString = Gson().toJson(task)
		val data = Data.Builder().putString(Constants.TASK, taskString).build()

		val startTimeSec = task.startTime.toSecondOfDay()
		val currentTimeSec = LocalTime.now().toSecondOfDay()
		val delaySec = startTimeSec - currentTimeSec

		cancelNotification(task.uuid)

		if (task.isRepeated) {
			val nextDelaySec = if (delaySec < 0) {
				val today = LocalDateTime.now()
				val nextDay = today.plusDays(1)
				val tomorrowTimeSec = today.until(nextDay, ChronoUnit.SECONDS)
				(tomorrowTimeSec - currentTimeSec)
			} else {
				(startTimeSec - currentTimeSec)
			}
			val workRequest =
				PeriodicWorkRequest.Builder(NotificationWorker::class.java, 1, TimeUnit.DAYS)
					.setInitialDelay(nextDelaySec.toLong(), TimeUnit.SECONDS)
					.setInputData(data)
					.addTag(task.uuid)
					.build()
			WorkManager.getInstance().enqueue(workRequest)
		} else {
			val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
				.setInitialDelay(delaySec.toLong(), TimeUnit.SECONDS)
				.setInputData(data)
				.addTag(task.uuid)
				.build()
			WorkManager.getInstance().enqueue(workRequest)
		}
	}

	fun cancelNotification(taskUUID: String) {
		WorkManager.getInstance().cancelAllWorkByTag(taskUUID)
	}

}