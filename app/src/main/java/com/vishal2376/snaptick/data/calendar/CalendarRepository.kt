package com.vishal2376.snaptick.data.calendar

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin wrapper around Android's `CalendarContract` content provider.
 *
 * Every call:
 * - Returns `null` / empty list when the required runtime permission is not granted.
 *   Callers must request the permission separately; this class never prompts.
 * - Catches `SecurityException` as a defensive guard against permission revocation
 *   mid-operation and reports it via the returned value, not a thrown exception.
 *
 * All `ContentResolver` calls are synchronous; callers should dispatch to an IO
 * context themselves.
 */
@Singleton
class CalendarRepository @Inject constructor(
	@ApplicationContext private val context: Context
) {

	private val resolver get() = context.contentResolver

	// ───────── Permission helpers ─────────

	fun hasReadPermission(): Boolean =
		ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) ==
			PackageManager.PERMISSION_GRANTED

	fun hasWritePermission(): Boolean =
		ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) ==
			PackageManager.PERMISSION_GRANTED

	// ───────── Calendars ─────────

	/** Returns every calendar the current user can write to. */
	fun getWritableCalendars(): List<CalendarInfo> {
		if (!hasReadPermission()) return emptyList()
		val projection = arrayOf(
			CalendarContract.Calendars._ID,
			CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
			CalendarContract.Calendars.ACCOUNT_NAME,
			CalendarContract.Calendars.ACCOUNT_TYPE,
			CalendarContract.Calendars.CALENDAR_COLOR,
			CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL
		)
		val selection =
			"${CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL} >= ? AND ${CalendarContract.Calendars.VISIBLE} = 1"
		val selectionArgs = arrayOf(CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR.toString())

		return try {
			resolver.query(
				CalendarContract.Calendars.CONTENT_URI,
				projection, selection, selectionArgs, null
			)?.use { cursor ->
				buildList {
					while (cursor.moveToNext()) {
						add(
							CalendarInfo(
								id = cursor.getLong(0),
								displayName = cursor.getString(1).orEmpty(),
								accountName = cursor.getString(2).orEmpty(),
								accountType = cursor.getString(3).orEmpty(),
								colorArgb = cursor.getInt(4)
							)
						)
					}
				}
			} ?: emptyList()
		} catch (e: SecurityException) {
			emptyList()
		}
	}

	// ───────── Events ─────────

	/** Inserts an event. Returns the new row id, or `null` if the insert failed. */
	fun insertEvent(event: CalendarEvent): Long? {
		if (!hasWritePermission()) return null
		val values = ContentValues().apply {
			put(CalendarContract.Events.CALENDAR_ID, event.calendarId)
			put(CalendarContract.Events.TITLE, event.title)
			applyTimingFields(event)
			event.description?.let { put(CalendarContract.Events.DESCRIPTION, it) }
		}
		return try {
			resolver.insert(CalendarContract.Events.CONTENT_URI, values)
				?.lastPathSegment?.toLongOrNull()
		} catch (e: SecurityException) {
			null
		}
	}

	fun updateEvent(eventId: Long, event: CalendarEvent): Boolean {
		if (!hasWritePermission()) return false
		val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
		val values = ContentValues().apply {
			put(CalendarContract.Events.TITLE, event.title)
			applyTimingFields(event)
			event.description?.let { put(CalendarContract.Events.DESCRIPTION, it) }
		}
		return try {
			resolver.update(uri, values, null, null) > 0
		} catch (e: SecurityException) {
			false
		}
	}

	/**
	 * Writes timing fields the way Google / Exchange sync adapters expect.
	 *
	 * - All-day events: `EVENT_TIMEZONE` MUST be `UTC` and `DTSTART` / `DTEND`
	 *   must be midnight in UTC, otherwise Google Calendar marks the event as
	 *   a sync error after it reaches the server.
	 * - Timed events: write a non-null `EVENT_END_TIMEZONE` so providers stop
	 *   complaining about a missing end-zone.
	 * - `DTEND` for timed events is forced to be at least one minute after
	 *   `DTSTART` so providers do not reject zero-length events.
	 */
	private fun ContentValues.applyTimingFields(event: CalendarEvent) {
		put(CalendarContract.Events.ALL_DAY, if (event.allDay) 1 else 0)
		if (event.allDay) {
			val startUtc = event.start.toLocalDate().atStartOfDay(ZoneOffset.UTC)
				.toInstant().toEpochMilli()
			val endUtc = event.end.toLocalDate().atStartOfDay(ZoneOffset.UTC)
				.toInstant().toEpochMilli()
			val safeEnd = if (endUtc <= startUtc) startUtc + 86_400_000L else endUtc
			put(CalendarContract.Events.DTSTART, startUtc)
			put(CalendarContract.Events.DTEND, safeEnd)
			put(CalendarContract.Events.EVENT_TIMEZONE, "UTC")
			put(CalendarContract.Events.EVENT_END_TIMEZONE, "UTC")
		} else {
			val startMs = event.start.toEpochMillis()
			val endMs = event.end.toEpochMillis()
			val safeEnd = if (endMs <= startMs) startMs + 60_000L else endMs
			put(CalendarContract.Events.DTSTART, startMs)
			put(CalendarContract.Events.DTEND, safeEnd)
			put(CalendarContract.Events.EVENT_TIMEZONE, event.timezone)
			put(CalendarContract.Events.EVENT_END_TIMEZONE, event.timezone)
		}
	}

	fun deleteEvent(eventId: Long): Boolean {
		if (!hasWritePermission()) return false
		val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
		return try {
			resolver.delete(uri, null, null) > 0
		} catch (e: SecurityException) {
			false
		}
	}

	/** Reads events from [calendarId] within the inclusive date range. */
	fun queryEvents(
		calendarId: Long,
		startMillis: Long,
		endMillis: Long
	): List<CalendarEvent> {
		if (!hasReadPermission()) return emptyList()
		val projection = arrayOf(
			CalendarContract.Events._ID,
			CalendarContract.Events.TITLE,
			CalendarContract.Events.DESCRIPTION,
			CalendarContract.Events.DTSTART,
			CalendarContract.Events.DTEND,
			CalendarContract.Events.ALL_DAY,
			CalendarContract.Events.EVENT_TIMEZONE
		)
		val selection =
			"${CalendarContract.Events.CALENDAR_ID} = ? AND " +
				"${CalendarContract.Events.DTSTART} >= ? AND " +
				"${CalendarContract.Events.DTSTART} <= ? AND " +
				"${CalendarContract.Events.DELETED} = 0"
		val selectionArgs =
			arrayOf(calendarId.toString(), startMillis.toString(), endMillis.toString())

		return try {
			resolver.query(
				CalendarContract.Events.CONTENT_URI,
				projection, selection, selectionArgs,
				"${CalendarContract.Events.DTSTART} ASC"
			)?.use { cursor ->
				buildList {
					while (cursor.moveToNext()) {
						add(
							CalendarEvent(
								id = cursor.getLong(0),
								calendarId = calendarId,
								title = cursor.getString(1).orEmpty(),
								description = cursor.getString(2),
								start = cursor.getLong(3).toLocalDateTime(),
								end = cursor.getLong(4).toLocalDateTime(),
								allDay = cursor.getInt(5) == 1,
								timezone = cursor.getString(6) ?: TimeZone.getDefault().id
							)
						)
					}
				}
			} ?: emptyList()
		} catch (e: SecurityException) {
			emptyList()
		}
	}

	private fun LocalDateTime.toEpochMillis(): Long =
		this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

	private fun Long.toLocalDateTime(): LocalDateTime =
		LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(this), ZoneId.systemDefault())
}
