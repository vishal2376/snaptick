package com.vishal2376.snaptick.data.calendar

import com.vishal2376.snaptick.data.local.TaskDao
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.util.SettingsStore
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * One-way push from Snaptick tasks to the device calendar the user selected
 * in settings. No-ops when sync is disabled or no calendar is selected.
 *
 * All methods are idempotent within the scope of a single task: repeating a
 * push for the same task updates the same event instead of creating duplicates
 * (because we persist `Task.calendarEventId` after the first successful write).
 */
@Singleton
class CalendarPusher @Inject constructor(
	private val calendarRepository: CalendarRepository,
	private val taskDao: TaskDao,
	private val settings: SettingsStore,
) {

	suspend fun pushInsert(task: Task) {
		val target = targetCalendarOrNull() ?: return
		val eventId = calendarRepository.insertEvent(task.toEvent(target)) ?: return
		persistEventId(task.uuid, eventId)
	}

	suspend fun pushUpdate(task: Task) {
		val target = targetCalendarOrNull() ?: return
		val existing = task.calendarEventId
		if (existing != null && calendarRepository.updateEvent(existing, task.toEvent(target))) {
			return
		}
		// No existing link (or update failed because row was deleted externally) — insert.
		val eventId = calendarRepository.insertEvent(task.toEvent(target)) ?: return
		persistEventId(task.uuid, eventId)
	}

	suspend fun pushDelete(task: Task) {
		val eventId = task.calendarEventId ?: return
		if (!settings.calendarSyncEnabledKey.first()) return
		calendarRepository.deleteEvent(eventId)
	}

	/**
	 * Re-pushes every task that does not yet have a `calendarEventId`. Useful
	 * as a one-shot backfill when the user first enables sync or switches
	 * target calendar.
	 */
	suspend fun pushAllUnmirrored(tasks: List<Task>) {
		val target = targetCalendarOrNull() ?: return
		tasks.filter { it.calendarEventId == null }
			.forEach { task ->
				val eventId = calendarRepository.insertEvent(task.toEvent(target)) ?: return@forEach
				persistEventId(task.uuid, eventId)
			}
	}

	/**
	 * Deletes every device-calendar event we ever pushed and clears each
	 * task's `calendarEventId` column. Called when the user turns sync off so
	 * we don't leave orphaned events lingering in their Google Calendar.
	 *
	 * Runs even when the sync flag is already off, because the user could
	 * disable sync mid-flight (we still need to clean up what's already there).
	 *
	 * Returns the number of events that were actually deleted (best-effort).
	 */
	suspend fun deleteAllPushedEvents(tasks: List<Task>): Int {
		var deleted = 0
		tasks.filter { it.calendarEventId != null }.forEach { task ->
			val eventId = task.calendarEventId ?: return@forEach
			if (calendarRepository.deleteEvent(eventId)) deleted++
			val latest = taskDao.getTaskByUuid(task.uuid) ?: return@forEach
			if (latest.calendarEventId != null) {
				taskDao.updateTask(latest.copy(calendarEventId = null))
			}
		}
		return deleted
	}

	private suspend fun targetCalendarOrNull(): Long? {
		if (!settings.calendarSyncEnabledKey.first()) return null
		return settings.calendarSyncCalendarIdKey.first()
	}

	private suspend fun persistEventId(uuid: String, eventId: Long) {
		val latest = taskDao.getTaskByUuid(uuid) ?: return
		if (latest.calendarEventId == eventId) return
		taskDao.updateTask(latest.copy(calendarEventId = eventId))
	}

	private fun Task.toEvent(calendarId: Long): CalendarEvent {
		val allDay = isAllDayTaskEnabled()
		val startDateTime = if (allDay) {
			date.atStartOfDay()
		} else {
			LocalDateTime.of(date, startTime)
		}
		val endDateTime = if (allDay) {
			date.plusDays(1).atStartOfDay()
		} else {
			LocalDateTime.of(date, endTime).takeIf { it.isAfter(startDateTime) }
				?: startDateTime.plusMinutes(30)
		}
		return CalendarEvent(
			id = calendarEventId,
			calendarId = calendarId,
			title = title,
			description = null,
			start = startDateTime,
			end = endDateTime,
			allDay = allDay
		)
	}
}
