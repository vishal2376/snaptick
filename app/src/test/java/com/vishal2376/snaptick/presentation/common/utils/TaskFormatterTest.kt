package com.vishal2376.snaptick.presentation.common.utils

import com.vishal2376.snaptick.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalTime

class TaskFormatterTest {

	private fun task(start: LocalTime, end: LocalTime) = Task(
		id = 1, uuid = "u", title = "t", startTime = start, endTime = end
	)

	@Test fun `formatTaskTime 12h format`() {
		val t = task(LocalTime.of(9, 0), LocalTime.of(10, 30))
		assertEquals("09:00 AM - 10:30 AM", formatTaskTime(t, is24HourFormat = false))
	}

	@Test fun `formatTaskTime 24h format`() {
		val t = task(LocalTime.of(13, 15), LocalTime.of(14, 45))
		assertEquals("13:15 - 14:45", formatTaskTime(t, is24HourFormat = true))
	}

	@Test fun `formatDuration for hours plus minutes`() {
		assertEquals("1h 30m", formatDuration(LocalTime.of(9, 0), LocalTime.of(10, 30)))
	}

	@Test fun `formatDuration exact single hour uses singular label`() {
		assertEquals("1 hour", formatDuration(LocalTime.of(9, 0), LocalTime.of(10, 0)))
	}

	@Test fun `formatDuration multiple hours uses plural label`() {
		assertEquals("2 hours", formatDuration(LocalTime.of(9, 0), LocalTime.of(11, 0)))
	}

	@Test fun `formatDuration minutes only`() {
		assertEquals("45 min", formatDuration(LocalTime.of(9, 0), LocalTime.of(9, 45)))
	}

	@Test fun `formatDurationTimestamp MMss when under one hour`() {
		assertEquals("00:59", formatDurationTimestamp(59))
	}

	@Test fun `formatDurationTimestamp HHmmss when over one hour`() {
		assertEquals("01:01:01", formatDurationTimestamp(3661))
	}

	@Test fun `formatWeekDays maps indices to short day names`() {
		assertEquals("Mon, Tue, Fri", formatWeekDays(listOf(0, 1, 4)))
	}

	@Test fun `formatWeekDays empty returns empty string`() {
		assertEquals("", formatWeekDays(emptyList()))
	}
}
