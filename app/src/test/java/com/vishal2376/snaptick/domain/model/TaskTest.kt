package com.vishal2376.snaptick.domain.model

import com.vishal2376.snaptick.util.Constants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class TaskTest {

	private fun task(
		start: LocalTime = LocalTime.of(9, 0),
		end: LocalTime = LocalTime.of(10, 0),
		repeatWeekdays: String = "",
		date: LocalDate = LocalDate.now(),
	) = Task(
		id = 1,
		uuid = "u",
		title = "t",
		startTime = start,
		endTime = end,
		repeatWeekdays = repeatWeekdays,
		date = date
	)

	@Test fun `duration returns seconds between start and end`() {
		val d = task(LocalTime.of(9, 0), LocalTime.of(10, 30)).getDuration()
		assertEquals(90 * 60L, d)
	}

	@Test fun `duration is zero when end before start`() {
		val d = task(LocalTime.of(10, 0), LocalTime.of(9, 0)).getDuration()
		assertEquals(0L, d)
	}

	@Test fun `isAllDayTaskEnabled true when start equals end`() {
		val t = task(LocalTime.of(8, 0), LocalTime.of(8, 0))
		assertTrue(t.isAllDayTaskEnabled())
	}

	@Test fun `isAllDayTaskEnabled false when start differs from end`() {
		assertFalse(task().isAllDayTaskEnabled())
	}

	@Test fun `getRepeatWeekList returns empty when repeatWeekdays blank`() {
		assertEquals(emptyList<Int>(), task(repeatWeekdays = "").getRepeatWeekList())
	}

	@Test fun `getRepeatWeekList parses comma separated indices`() {
		assertEquals(listOf(0, 2, 4), task(repeatWeekdays = "0,2,4").getRepeatWeekList())
	}

	@Test fun `isValidPomodoroSession requires at least MIN_VALID seconds elapsed`() {
		val t = task(LocalTime.of(9, 0), LocalTime.of(9, 30)) // 1800s total
		val minSessionSec = Constants.MIN_VALID_POMODORO_SESSION * 60
		// timeLeft such that elapsed (total - timeLeft) == minSessionSec → valid
		val validTimeLeft = 1800L - minSessionSec
		assertTrue(t.isValidPomodoroSession(validTimeLeft))
		// elapsed just below min → invalid
		assertFalse(t.isValidPomodoroSession(validTimeLeft + 1))
	}
}
