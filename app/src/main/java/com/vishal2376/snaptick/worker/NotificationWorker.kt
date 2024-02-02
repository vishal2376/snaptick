package com.vishal2376.snaptick.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.NotificationHelper

class NotificationWorker(val context: Context, params: WorkerParameters) :
	Worker(context, params) {

	private val notificationHelper = NotificationHelper(context)

	override fun doWork(): Result {
		//get required data
		val taskId = inputData.getInt(Constants.TASK_ID, -1)
		val taskTitle = inputData.getString(Constants.TASK_TITLE)
		val taskTime = inputData.getString(Constants.TASK_TIME)

		if (taskId != -1 || taskTitle != null || taskTime != null) {
			notificationHelper.showNotification(taskId, taskTitle!!, taskTime!!)
			return Result.success()
		}
		return Result.failure()
	}
}