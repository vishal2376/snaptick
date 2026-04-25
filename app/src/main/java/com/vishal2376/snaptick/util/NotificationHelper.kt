package com.vishal2376.snaptick.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vishal2376.snaptick.MainActivity
import com.vishal2376.snaptick.R

const val NOTIFICATION = "Notification"
const val CHANNEL_ID = "snaptick-notification"
const val CHANNEL_NAME = "Task Reminder"

class NotificationHelper(private val context: Context) {

	private val notificationManager =
		context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

	fun showNotification(taskId: Int, taskTitle: String, taskTime: String) {
		if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
			Log.w("NotificationHelper", "Notifications disabled; skipping taskId=$taskId")
			return
		}

		val intent = Intent(context, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		}

		val pendingIntent = PendingIntent.getActivity(
			context,
			taskId,
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
			.setContentTitle(taskTitle)
			.setContentText(taskTime)
			.setSmallIcon(R.drawable.ic_clock)
			.setStyle(NotificationCompat.BigTextStyle().bigText(taskTime))
			.setDefaults(NotificationCompat.DEFAULT_ALL)
			.setContentIntent(pendingIntent)
			.setAutoCancel(true)

		try {
			notificationManager.notify(taskId, notificationBuilder.build())
		} catch (e: SecurityException) {
			Log.e("NotificationHelper", "notify() denied for taskId=$taskId", e)
		}
	}

	fun createNotificationChannel() {
		val mChannel =
			NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
		notificationManager.createNotificationChannel(mChannel)
	}
}
