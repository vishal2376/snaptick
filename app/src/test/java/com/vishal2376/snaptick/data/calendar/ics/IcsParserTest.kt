package com.vishal2376.snaptick.data.calendar.ics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.StringReader
import java.time.LocalDateTime

class IcsParserTest {

	@Test fun parses_single_VEVENT_with_dtstart_and_dtend() {
		val ics = """
			BEGIN:VCALENDAR
			VERSION:2.0
			BEGIN:VEVENT
			UID:abc@example.com
			SUMMARY:Run
			DTSTART:20260423T090000
			DTEND:20260423T100000
			DESCRIPTION:Morning run
			END:VEVENT
			END:VCALENDAR
		""".trimIndent()

		val events = IcsParser.parse(StringReader(ics))
		assertEquals(1, events.size)
		val e = events.single()
		assertEquals("abc@example.com", e.uid)
		assertEquals("Run", e.summary)
		assertEquals(LocalDateTime.of(2026, 4, 23, 9, 0), e.start)
		assertEquals(LocalDateTime.of(2026, 4, 23, 10, 0), e.end)
		assertEquals("Morning run", e.description)
		assertEquals(false, e.allDay)
	}

	@Test fun parses_all_day_event() {
		val ics = """
			BEGIN:VEVENT
			UID:1
			SUMMARY:Holiday
			DTSTART;VALUE=DATE:20260504
			DTEND;VALUE=DATE:20260505
			END:VEVENT
		""".trimIndent()
		val e = IcsParser.parse(StringReader(ics)).single()
		assertEquals("Holiday", e.summary)
		assertEquals(true, e.allDay)
		assertEquals(LocalDateTime.of(2026, 5, 4, 0, 0), e.start)
	}

	@Test fun parses_multiple_events() {
		val ics = """
			BEGIN:VEVENT
			UID:1
			SUMMARY:A
			DTSTART:20260101T100000
			DTEND:20260101T110000
			END:VEVENT
			BEGIN:VEVENT
			UID:2
			SUMMARY:B
			DTSTART:20260102T100000
			DTEND:20260102T113000
			END:VEVENT
		""".trimIndent()
		val events = IcsParser.parse(StringReader(ics))
		assertEquals(2, events.size)
		assertEquals(listOf("A", "B"), events.map { it.summary })
	}

	@Test fun skips_malformed_event_without_DTSTART() {
		val ics = """
			BEGIN:VEVENT
			SUMMARY:Orphan
			END:VEVENT
		""".trimIndent()
		assertTrue(IcsParser.parse(StringReader(ics)).isEmpty())
	}

	@Test fun defaults_end_to_one_hour_after_start_when_missing() {
		val ics = """
			BEGIN:VEVENT
			UID:1
			SUMMARY:NoEnd
			DTSTART:20260101T100000
			END:VEVENT
		""".trimIndent()
		val e = IcsParser.parse(StringReader(ics)).single()
		assertEquals(LocalDateTime.of(2026, 1, 1, 11, 0), e.end)
	}

	@Test fun handles_line_folding() {
		val ics = "BEGIN:VEVENT\r\nUID:1\r\nSUMMARY:Very long\r\n title continuation\r\nDTSTART:20260101T100000\r\nDTEND:20260101T110000\r\nEND:VEVENT\r\n"
		val e = IcsParser.parse(StringReader(ics)).single()
		assertEquals("Very longtitle continuation", e.summary)
	}
}
