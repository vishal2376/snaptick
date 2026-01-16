package com.vishal2376.snaptick.data.calendar

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import com.vishal2376.snaptick.domain.model.Task
import java.time.ZoneId
import java.util.TimeZone

/**
 * Helper class for interacting with the Android Calendar Provider.
 * Provides CRUD operations for calendar events and calendar queries.
 */
class CalendarHelper(private val context: Context) {

	data class CalendarInfo(
		val id: Long,
		val name: String,
		val accountName: String,
		val isPrimary: Boolean
	)

	/**
	 * Get list of available calendars on the device.
	 */
	fun getCalendars(): List<CalendarInfo> {
		val calendars = mutableListOf<CalendarInfo>()
		
		val projection = arrayOf(
			CalendarContract.Calendars._ID,
			CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
			CalendarContract.Calendars.ACCOUNT_NAME,
			CalendarContract.Calendars.IS_PRIMARY
		)

		val cursor: Cursor? = context.contentResolver.query(
			CalendarContract.Calendars.CONTENT_URI,
			projection,
			null,
			null,
			null
		)

		cursor?.use {
			val idIndex = it.getColumnIndex(CalendarContract.Calendars._ID)
			val nameIndex = it.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
			val accountIndex = it.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)
			val primaryIndex = it.getColumnIndex(CalendarContract.Calendars.IS_PRIMARY)

			while (it.moveToNext()) {
				calendars.add(
					CalendarInfo(
						id = it.getLong(idIndex),
						name = it.getString(nameIndex) ?: "Unknown",
						accountName = it.getString(accountIndex) ?: "",
						isPrimary = it.getInt(primaryIndex) == 1
					)
				)
			}
		}

		return calendars
	}

	/**
	 * Create a calendar event from a task.
	 * @return The event ID if successful, null otherwise.
	 */
	fun createEventFromTask(task: Task, calendarId: Long): Long? {
		val startMillis = task.date
			.atTime(task.startTime)
			.atZone(ZoneId.systemDefault())
			.toInstant()
			.toEpochMilli()

		val endMillis = task.date
			.atTime(task.endTime)
			.atZone(ZoneId.systemDefault())
			.toInstant()
			.toEpochMilli()

		val values = ContentValues().apply {
			put(CalendarContract.Events.CALENDAR_ID, calendarId)
			put(CalendarContract.Events.TITLE, task.title)
			put(CalendarContract.Events.DESCRIPTION, "Created by Snaptick")
			put(CalendarContract.Events.DTSTART, startMillis)
			put(CalendarContract.Events.DTEND, endMillis)
			put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
			// Store task UUID in custom field for sync
			put(CalendarContract.Events.CUSTOM_APP_URI, "snaptick://task/${task.uuid}")
		}

		return try {
			val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
			uri?.lastPathSegment?.toLongOrNull()
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}
	}

	/**
	 * Update an existing calendar event from a task.
	 */
	fun updateEventFromTask(task: Task, eventId: Long): Boolean {
		val startMillis = task.date
			.atTime(task.startTime)
			.atZone(ZoneId.systemDefault())
			.toInstant()
			.toEpochMilli()

		val endMillis = task.date
			.atTime(task.endTime)
			.atZone(ZoneId.systemDefault())
			.toInstant()
			.toEpochMilli()

		val values = ContentValues().apply {
			put(CalendarContract.Events.TITLE, task.title)
			put(CalendarContract.Events.DTSTART, startMillis)
			put(CalendarContract.Events.DTEND, endMillis)
		}

		return try {
			val updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
			val rows = context.contentResolver.update(updateUri, values, null, null)
			rows > 0
		} catch (e: Exception) {
			e.printStackTrace()
			false
		}
	}

	/**
	 * Delete a calendar event.
	 */
	fun deleteEvent(eventId: Long): Boolean {
		return try {
			val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
			val rows = context.contentResolver.delete(deleteUri, null, null)
			rows > 0
		} catch (e: Exception) {
			e.printStackTrace()
			false
		}
	}

	/**
	 * Check if an event exists.
	 */
	fun eventExists(eventId: Long): Boolean {
		val projection = arrayOf(CalendarContract.Events._ID)
		val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
		
		val cursor = context.contentResolver.query(uri, projection, null, null, null)
		return cursor?.use { it.count > 0 } ?: false
	}
}
