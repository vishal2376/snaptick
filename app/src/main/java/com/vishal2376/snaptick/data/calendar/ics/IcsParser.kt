package com.vishal2376.snaptick.data.calendar.ics

import java.io.BufferedReader
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
 * ignored (a recurring event is imported as its first occurrence only).
 *
 * Line folding (RFC 5545 §3.1) is honoured: a continuation line starts with
 * SPACE or TAB and is appended to the previous logical line.
 *
 * **Hardened against hostile files.** The parser:
 * - Streams the input line-by-line via [BufferedReader.useLines] so the whole
 *   file never has to fit in memory.
 * - Rejects unfolded logical lines longer than [MAX_LOGICAL_LINE_BYTES] (8 KiB).
 *   A single SUMMARY can't legitimately exceed that.
 * - Stops parsing once [MAX_EVENTS] VEVENTs have been collected. The caller
 *   sees [ParseResult.events] with the first chunk and [ParseResult.truncated]
 *   set to true so it can surface a "imported first N events" warning.
 *
 * Size-of-file checks (e.g. the SAF URI's `length`) are the caller's job; this
 * parser only enforces per-event invariants.
 */
object IcsParser {

	const val MAX_EVENTS = 5_000
	private const val MAX_LOGICAL_LINE_BYTES = 8 * 1024  // 8 KiB

	private val dateTimeBasic = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
	private val dateTimeUtc = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
	private val dateOnly = DateTimeFormatter.ofPattern("yyyyMMdd")

	data class ParseResult(
		val events: List<IcsEvent>,
		val truncated: Boolean,
	)

	/**
	 * Streaming parse. Compatible with the legacy entry point [parse] which
	 * just unwraps the events list and discards the truncated flag.
	 */
	fun parseStream(reader: Reader): ParseResult {
		val events = mutableListOf<IcsEvent>()
		var inEvent = false
		var buffer = mutableMapOf<String, String>()
		var pendingLogical = StringBuilder()
		var truncated = false

		fun flushLogical() {
			if (pendingLogical.isEmpty()) return
			val line = pendingLogical.toString()
			pendingLogical = StringBuilder()
			when {
				line.equals("BEGIN:VEVENT", ignoreCase = true) -> {
					inEvent = true
					buffer = mutableMapOf()
				}
				line.equals("END:VEVENT", ignoreCase = true) -> {
					if (inEvent) buildEvent(buffer)?.let(events::add)
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

		val br = if (reader is BufferedReader) reader else BufferedReader(reader)
		br.useLines { lines ->
			for (raw in lines) {
				if (events.size >= MAX_EVENTS) {
					truncated = true
					break
				}
				if (raw.isEmpty()) continue

				if (raw.startsWith(' ') || raw.startsWith('\t')) {
					// Continuation: glue onto the in-progress logical line.
					if (pendingLogical.length + raw.length > MAX_LOGICAL_LINE_BYTES) {
						// Drop pathologically long folded lines instead of OOMing.
						pendingLogical = StringBuilder()
						continue
					}
					pendingLogical.append(raw.substring(1))
				} else {
					flushLogical()
					if (raw.length > MAX_LOGICAL_LINE_BYTES) continue
					pendingLogical.append(raw)
				}
			}
			// Flush any trailing logical line.
			flushLogical()
		}
		return ParseResult(events = events, truncated = truncated)
	}

	/** Backwards-compatible API: just returns the events. Drops truncation flag. */
	fun parse(reader: Reader): List<IcsEvent> = parseStream(reader).events

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
