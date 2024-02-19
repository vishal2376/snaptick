package com.vishal2376.snaptick.util

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.gson.Gson
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.worker.NotificationWorker
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object WorkManagerHelper {

	fun scheduleNotification(task: Task) {
		val taskString = Gson().toJson(task)
		val data = Data.Builder().putString(Constants.TASK, taskString).build()

		val startTimeSec = task.startTime.toSecondOfDay()
		val currentTimeSec = LocalTime.now().toSecondOfDay()
		var delaySec = startTimeSec - currentTimeSec


		if (task.isRepeated) {
			if (delaySec < 0) {
				delaySec += LocalTime.MAX.toSecondOfDay()
			}

			val workRequest =
//				PeriodicWorkRequest.Builder(NotificationWorker::class.java, 1, TimeUnit.DAYS)
				PeriodicWorkRequest.Builder(NotificationWorker::class.java, 20, TimeUnit.MINUTES)
					.setInitialDelay(delaySec.toLong(), TimeUnit.SECONDS)
					.setInputData(data)
					.addTag(task.uuid)
					.build()

			// Enqueue the work request with WorkManager
			WorkManager.getInstance().enqueue(workRequest)

		} else {
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

	fun cancelNotification(taskUUID: String) {
		WorkManager.getInstance().cancelAllWorkByTag(taskUUID)
	}

}