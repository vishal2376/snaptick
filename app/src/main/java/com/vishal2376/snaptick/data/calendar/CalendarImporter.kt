package com.vishal2376.snaptick.data.calendar

import com.vishal2376.snaptick.data.calendar.ics.IcsEvent
import com.vishal2376.snaptick.data.calendar.ics.IcsParser
import com.vishal2376.snaptick.domain.model.Task
import java.io.Reader
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * One-shot conversion layer. Turns device-calendar events (via [CalendarRepository])
 * and iCalendar `.ics` files into Snaptick [Task]s. Never writes back to the
 * device calendar — the user imports explicitly and is expected to edit the
 * resulting tasks as they would any manually-added task.
 */
@Singleton
class CalendarImporter @Inject constructor(
	private val calendarRepository: CalendarRepository
) {

	/** Reads events from [calendarId] between [range] (inclusive), returns draft tasks. */
	fun previewFromDeviceCalendar(
		calendarId: Long,
		range: ClosedRange<LocalDate>
	): List<Task> {
		val start = range.start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
		val end = range.endInclusive.plusDays(1).atStartOfDay(ZoneId.systemDefault())
			.toInstant().toEpochMilli() - 1
		return calendarRepository.queryEvents(calendarId, start, end)
			.map { it.toTaskDraft() }
	}

	/** Parses a `.ics` stream and returns draft tasks. */
	fun previewFromIcs(reader: Reader): List<Task> =
		IcsParser.parse(reader).map { it.toTaskDraft() }

	private fun CalendarEvent.toTaskDraft(): Task = Task(
		id = 0,
		uuid = UUID.randomUUID().toString(),
		title = title,
		startTime = start.toLocalTime(),
		endTime = end.toLocalTime(),
		date = start.toLocalDate(),
		reminder = false,
		isRepeated = false,
		repeatWeekdays = "",
		pomodoroTimer = -1,
		priority = 0,
		calendarEventId = null
	)

	private fun IcsEvent.toTaskDraft(): Task = Task(
		id = 0,
		uuid = UUID.randomUUID().toString(),
		title = summary,
		startTime = start.toLocalTime(),
		endTime = end.toLocalTime(),
		date = start.toLocalDate(),
		reminder = false,
		isRepeated = false,
		repeatWeekdays = "",
		pomodoroTimer = -1,
		priority = 0,
		calendarEventId = null
	)
}
