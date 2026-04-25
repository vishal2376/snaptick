package com.vishal2376.snaptick.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.vishal2376.snaptick.worker.RescheduleAllRemindersWorker

/**
 * Re-arms every reminder when the device or app environment shifts:
 *
 * - `BOOT_COMPLETED` / `LOCKED_BOOT_COMPLETED`: AlarmManager loses pending
 *   exact alarms across reboot. Restore them from the DB.
 * - `MY_PACKAGE_REPLACED`: app upgrade clears alarms on most OEMs.
 * - `TIME_SET` / `TIMEZONE_CHANGED`: wall-clock changed under us; previously
 *   armed alarms now point to the wrong instant. Recompute and re-arm.
 *
 * The receiver itself does no DB work because it has a ~10s budget and may
 * run before the app process is fully bootstrapped (especially on locked
 * boot). It enqueues a one-shot Hilt worker that walks the task table and
 * arms next-fire alarms.
 */
class SystemEventReceiver : BroadcastReceiver() {

	override fun onReceive(context: Context, intent: Intent) {
		when (intent.action) {
			Intent.ACTION_BOOT_COMPLETED,
			Intent.ACTION_LOCKED_BOOT_COMPLETED,
			Intent.ACTION_MY_PACKAGE_REPLACED,
			Intent.ACTION_TIME_CHANGED,
			Intent.ACTION_TIMEZONE_CHANGED -> {
				val request = OneTimeWorkRequestBuilder<RescheduleAllRemindersWorker>().build()
				WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
					UNIQUE_WORK_NAME,
					ExistingWorkPolicy.REPLACE,
					request,
				)
			}
		}
	}

	companion object {
		const val UNIQUE_WORK_NAME = "snaptick.reschedule-all-reminders"
	}
}
