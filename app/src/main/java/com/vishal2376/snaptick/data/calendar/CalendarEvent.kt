package com.vishal2376.snaptick.data.calendar

import java.time.LocalDateTime

/**
 * Plain value type representing a CalendarContract event. Translates cleanly
 * to/from Snaptick's Task domain model at the repository boundary.
 */
data class CalendarEvent(
	val id: Long? = null,
	val calendarId: Long,
	val title: String,
	val description: String? = null,
	val start: LocalDateTime,
	val end: LocalDateTime,
	val allDay: Boolean = false,
	val timezone: String = java.util.TimeZone.getDefault().id
)
