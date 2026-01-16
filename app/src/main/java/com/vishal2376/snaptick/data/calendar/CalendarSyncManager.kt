package com.vishal2376.snaptick.data.calendar

import android.content.Context
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.util.SettingsStore
import kotlinx.coroutines.flow.first

/**
 * Manager class that handles calendar sync logic.
 * Syncs tasks to the selected device calendar when sync is enabled.
 */
class CalendarSyncManager(
	private val context: Context,
	private val settingsStore: SettingsStore
) {
	private val calendarHelper = CalendarHelper(context)

	/**
	 * Sync a task to the calendar (create or update).
	 * @return The task with updated calendarEventId if sync was performed.
	 */
	suspend fun syncTaskToCalendar(task: Task): Task {
		val syncEnabled = settingsStore.calendarSyncEnabledKey.first()
		val calendarId = settingsStore.selectedCalendarIdKey.first()

		if (!syncEnabled || calendarId == null) {
			return task
		}

		return try {
			if (task.calendarEventId != null) {
				// Update existing event
				if (calendarHelper.eventExists(task.calendarEventId)) {
					calendarHelper.updateEventFromTask(task, task.calendarEventId)
				}
				task
			} else {
				// Create new event
				val eventId = calendarHelper.createEventFromTask(task, calendarId)
				task.copy(calendarEventId = eventId)
			}
		} catch (e: Exception) {
			e.printStackTrace()
			task
		}
	}

	/**
	 * Delete the calendar event associated with a task.
	 */
	suspend fun deleteCalendarEvent(task: Task) {
		val syncEnabled = settingsStore.calendarSyncEnabledKey.first()
		
		if (!syncEnabled || task.calendarEventId == null) {
			return
		}

		try {
			calendarHelper.deleteEvent(task.calendarEventId)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	/**
	 * Get list of available calendars.
	 */
	fun getAvailableCalendars(): List<CalendarHelper.CalendarInfo> {
		return try {
			calendarHelper.getCalendars()
		} catch (e: SecurityException) {
			// Calendar permission not granted
			emptyList()
		}
	}

	/**
	 * Check if calendar sync is configured.
	 */
	suspend fun isSyncConfigured(): Boolean {
		val syncEnabled = settingsStore.calendarSyncEnabledKey.first()
		val calendarId = settingsStore.selectedCalendarIdKey.first()
		return syncEnabled && calendarId != null
	}
}
