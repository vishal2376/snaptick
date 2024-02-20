package com.vishal2376.snaptick.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.NotificationHelper

class NotificationWorker(val context: Context, params: WorkerParameters) :
	Worker(context, params) {

	private val notificationHelper = NotificationHelper(context)

	override fun doWork(): Result {
		//get required data
		val taskString = inputData.getString(Constants.TASK)
		val task = Gson().fromJson(taskString, Task::class.java)

		if (task != null) {
			notificationHelper.showNotification(
				task.hashCode().hashCode(),
				task.title,
				task.getFormattedTime()
			)
			return Result.success()
		}
		return Result.failure()
	}
}