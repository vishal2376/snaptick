package com.vishal2376.snaptick.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.Task

const val NOTIFICATION = "Notification"
const val CHANNEL_ID = "snaptick-notification"
const val CHANNEL_NAME = "Task Reminder"

class NotificationHelper(private val context: Context) {

	private val notificationManager =
		context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

	fun showNotification(task: Task) {
		val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
			.setContentTitle(task.title)
			.setSmallIcon(R.drawable.ic_clock)
			.setStyle(NotificationCompat.BigTextStyle().bigText(task.getFormattedTime()))

		notificationManager.notify(task.id, notificationBuilder.build())
	}

	fun createNotificationChannel() {
		val mChannel =
			NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
		notificationManager.createNotificationChannel(mChannel)
	}
}
