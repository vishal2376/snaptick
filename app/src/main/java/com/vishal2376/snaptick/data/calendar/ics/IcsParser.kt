package com.vishal2376.snaptick.data.calendar.ics

import java.io.Reader
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Very small iCalendar parser. Handles just enough of RFC 5545 for Snaptick's
 * import flow: VEVENT blocks with SUMMARY / DESCRIPTION / DTSTART / DTEND /
 * UID / optional `VALUE=DATE` for all-day events. RRULE and VTIMEZONE are
 * ignored — a recurring event is imported as its first occurrence only.
 *
 * Line folding (RFC 5545 §3.1) is honoured: a continuation line starts with
 * SPACE or TAB and is appended to the previous logical line.
 */
object IcsParser {

	private val dateTimeBasic = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
	private val dateTimeUtc = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
	private val dateOnly = DateTimeFormatter.ofPattern("yyyyMMdd")

	fun parse(reader: Reader): List<IcsEvent> {
		val logicalLines = unfold(reader.readText().lines())
		val events = mutableListOf<IcsEvent>()
		var inEvent = false
		var buffer = mutableMapOf<String, String>()

		for (line in logicalLines) {
			when {
				line.equals("BEGIN:VEVENT", ignoreCase = true) -> {
					inEvent = true
					buffer = mutableMapOf()
				}
				line.equals("END:VEVENT", ignoreCase = true) -> {
					if (inEvent) {
						buildEvent(buffer)?.let(events::add)
					}
					inEvent = false
				}
				inEvent -> {
					val colon = line.indexOf(':')
					if (colon > 0) {
						val key = line.substring(0, colon).uppercase()
						val value = line.substring(colon + 1)
						buffer[key] = value
					}
				}
			}
		}
		return events
	}

	private fun unfold(rawLines: List<String>): List<String> {
		val folded = mutableListOf<String>()
		for (line in rawLines) {
			if (line.isEmpty()) continue
			if ((line.startsWith(' ') || line.startsWith('\t')) && folded.isNotEmpty()) {
				folded[folded.lastIndex] = folded.last() + line.substring(1)
			} else {
				folded += line
			}
		}
		return folded
	}

	private fun buildEvent(fields: Map<String, String>): IcsEvent? {
		val summary = fields.entries
			.firstOrNull { it.key.startsWith("SUMMARY") }?.value ?: return null
		val dtStartKey = fields.keys.firstOrNull { it.startsWith("DTSTART") } ?: return null
		val dtEndKey = fields.keys.firstOrNull { it.startsWith("DTEND") }

		val allDay = dtStartKey.contains("VALUE=DATE", ignoreCase = true) &&
			!dtStartKey.contains("VALUE=DATE-TIME", ignoreCase = true)
		val start = parseDateTime(fields[dtStartKey]!!, allDay) ?: return null
		val end = dtEndKey?.let { parseDateTime(fields[it]!!, allDay) }
			?: if (allDay) start else start.plusHours(1)

		val uid = fields.entries.firstOrNull { it.key.startsWith("UID") }?.value
			?: UUID.randomUUID().toString()
		val description = fields.entries
			.firstOrNull { it.key.startsWith("DESCRIPTION") }?.value

		return IcsEvent(
			uid = uid,
			summary = summary,
			description = description,
			start = start,
			end = end,
			allDay = allDay
		)
	}

	private fun parseDateTime(raw: String, allDay: Boolean): LocalDateTime? = try {
		when {
			allDay -> LocalDate.parse(raw.trim(), dateOnly).atTime(LocalTime.MIDNIGHT)
			raw.endsWith("Z") -> LocalDateTime.parse(raw.trim(), dateTimeUtc)
				.atZone(ZoneId.of("UTC"))
				.withZoneSameInstant(ZoneId.systemDefault())
				.toLocalDateTime()
			else -> LocalDateTime.parse(raw.trim(), dateTimeBasic)
		}
	} catch (e: Exception) {
		null
	}
}
