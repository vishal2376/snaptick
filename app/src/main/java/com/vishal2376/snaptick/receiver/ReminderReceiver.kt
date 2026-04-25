package com.vishal2376.snaptick.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.NotificationHelper
import com.vishal2376.snaptick.worker.RescheduleSingleReminderWorker

/**
 * Fired by AlarmManager when a reminder should appear. Posts the notification
 * directly (cheap synchronous work) and enqueues a one-shot worker to re-arm
 * the next occurrence for repeating tasks. We don't touch the database from
 * `onReceive` because broadcast receivers have a strict ~10s budget.
 *
 * Not annotated `@AndroidEntryPoint` because we don't need any DI here. The
 * receiver re-builds its NotificationHelper each fire (cheap) and forwards the
 * reschedule request to a Hilt-injected worker.
 */
class ReminderReceiver : BroadcastReceiver() {

	override fun onReceive(context: Context, intent: Intent) {
		val taskId = intent.getIntExtra(Constants.TASK_ID, -1)
		if (taskId == -1) return
		val taskTitle = intent.getStringExtra(Constants.TASK_TITLE).orEmpty()
		val taskTime = intent.getStringExtra(Constants.TASK_TIME).orEmpty()

		NotificationHelper(context.applicationContext)
			.showNotification(taskId, taskTitle, taskTime)

		val data = Data.Builder()
			.putInt(Constants.TASK_ID, taskId)
			.build()
		val request = OneTimeWorkRequestBuilder<RescheduleSingleReminderWorker>()
			.setInputData(data)
			.build()
		WorkManager.getInstance(context.applicationContext).enqueue(request)
	}
}
