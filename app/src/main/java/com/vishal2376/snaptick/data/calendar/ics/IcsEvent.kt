package com.vishal2376.snaptick.data.calendar.ics

import java.time.LocalDateTime

/** Minimal representation of a parsed iCalendar VEVENT. */
data class IcsEvent(
	val uid: String,
	val summary: String,
	val description: String? = null,
	val start: LocalDateTime,
	val end: LocalDateTime,
	val allDay: Boolean = false
)
