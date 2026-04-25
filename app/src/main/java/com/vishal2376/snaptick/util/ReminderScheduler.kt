package com.vishal2376.snaptick.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.utils.formatTaskTime
import com.vishal2376.snaptick.receiver.ReminderReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Schedules per-task reminders via AlarmManager exact alarms.
 *
 * Why AlarmManager and not WorkManager:
 * - Exact-second delivery is required for a reminder/alarm-class app. WorkManager
 *   batches work under Doze and can fire 9-15 min late.
 * - Manifest declares `USE_EXACT_ALARM` (auto-granted by Play policy for
 *   alarm/calendar/task-reminder apps). On API levels where it isn't honored we
 *   fall back to `setAndAllowWhileIdle` (inexact, but still wakes Doze).
 *
 * The PendingIntent request code is `task.id` (stable Int) so cancel-by-id is
 * idempotent. The fire intent carries `taskId`, `taskUuid`, `taskTitle`,
 * `taskTime` so the receiver can post the notification without an extra DB
 * read.
 *
 * For repeating tasks, only the **next** occurrence is armed at any time. After
 * the alarm fires, [ReminderReceiver] enqueues a tiny worker that calls
 * [schedule] again to arm the following occurrence. This keeps the active
 * alarm count to O(active tasks), well below AlarmManager's per-app cap.
 */
@Singleton
class ReminderScheduler @Inject constructor(
	@ApplicationContext private val context: Context,
	private val alarmManager: AlarmManager,
) {

	/**
	 * Arms the next fire for [task]. No-op when reminders are off, the task is
	 * globally completed (one-off case), or no future occurrence exists.
	 */
	fun schedule(task: Task) {
		if (!task.reminder || task.isCompleted) {
			cancel(task.id)
			return
		}
		val fireAt = nextFireMillis(task) ?: run {
			cancel(task.id)
			return
		}

		val pendingIntent = buildPendingIntent(task, flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

		val canExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			alarmManager.canScheduleExactAlarms()
		} else true

		try {
			if (canExact) {
				alarmManager.setExactAndAllowWhileIdle(
					AlarmManager.RTC_WAKEUP,
					fireAt,
					pendingIntent
				)
			} else {
				Log.w(TAG, "Exact alarm denied; falling back to inexact for taskId=${task.id}")
				alarmManager.setAndAllowWhileIdle(
					AlarmManager.RTC_WAKEUP,
					fireAt,
					pendingIntent
				)
			}
		} catch (e: SecurityException) {
			Log.e(TAG, "Failed to schedule alarm for taskId=${task.id}", e)
		}
	}

	/** Cancels the alarm armed for [taskId], if any. */
	fun cancel(taskId: Int) {
		val intent = Intent(context, ReminderReceiver::class.java)
		val pendingIntent = PendingIntent.getBroadcast(
			context,
			taskId,
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
		)
		if (pendingIntent != null) {
			alarmManager.cancel(pendingIntent)
			pendingIntent.cancel()
		}
	}

	/** Re-arms reminders for every supplied task. Idempotent. */
	fun rescheduleAll(tasks: List<Task>) {
		tasks.forEach { schedule(it) }
	}

	private fun buildPendingIntent(task: Task, flags: Int): PendingIntent {
		val intent = Intent(context, ReminderReceiver::class.java).apply {
			putExtra(Constants.TASK_ID, task.id)
			putExtra(Constants.TASK_UUID, task.uuid)
			putExtra(Constants.TASK_TITLE, task.title)
			putExtra(Constants.TASK_TIME, formatTaskTime(task))
		}
		return PendingIntent.getBroadcast(context, task.id, intent, flags)
	}

	/**
	 * Returns the epoch-millis of the next fire instant for [task] in the
	 * device's local time zone, or `null` if no future occurrence exists.
	 *
	 * - One-off task: fires at `task.date + task.startTime` if that's in the
	 *   future, else null.
	 * - Repeat task: walks forward up to 7 days from today and picks the first
	 *   weekday in `task.repeatWeekdays`, paired with `task.startTime`. If
	 *   today's slot is in the past and today is in the weekday set, today is
	 *   skipped and the next match wins.
	 */
	internal fun nextFireMillis(task: Task, now: LocalDateTime = LocalDateTime.now()): Long? {
		val zone = ZoneId.systemDefault()

		if (!task.isRepeated) {
			val fire = LocalDateTime.of(task.date, task.startTime)
			return if (fire.isAfter(now)) fire.atZone(zone).toInstant().toEpochMilli() else null
		}

		val weekdays = task.getRepeatWeekList()
		if (weekdays.isEmpty()) return null

		val today = now.toLocalDate()
		for (offset in 0..7) {
			val candidateDate = today.plusDays(offset.toLong())
			val candidateDow = candidateDate.dayOfWeek.value - 1 // Mon=0 … Sun=6
			if (candidateDow !in weekdays) continue
			val candidate = LocalDateTime.of(candidateDate, task.startTime)
			if (candidate.isAfter(now)) {
				return candidate.atZone(zone).toInstant().toEpochMilli()
			}
		}
		return null
	}

	companion object {
		private const val TAG = "ReminderScheduler"

		/** Today's date as ISO yyyy-MM-dd. Shared helper for completion writes. */
		fun todayIso(): String = LocalDate.now().toString()
	}
}
