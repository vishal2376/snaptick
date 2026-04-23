package com.vishal2376.snaptick.util

import com.vishal2376.snaptick.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class ValidationTest {

	private fun task(
		title: String = "Sample",
		start: LocalTime = LocalTime.of(9, 0),
		end: LocalTime = LocalTime.of(10, 0),
		date: LocalDate = LocalDate.now()
	) = Task(
		id = 1, uuid = "u", title = title,
		startTime = start, endTime = end, date = date
	)

	@Test fun `empty title is invalid`() {
		val (valid, msg) = checkValidTask(task = task(title = "   "))
		assertFalse(valid)
		assertEquals("Title can't be empty", msg)
	}

	@Test fun `duration below MIN_ALLOWED_DURATION is invalid when not all day`() {
		val t = task(start = LocalTime.of(9, 0), end = LocalTime.of(9, 2))
		val (valid, _) = checkValidTask(task = t, isTaskAllDay = false)
		assertFalse(valid)
	}

	@Test fun `short duration allowed when all day`() {
		val t = task(start = LocalTime.of(9, 0), end = LocalTime.of(9, 0))
		val (valid, _) = checkValidTask(task = t, isTaskAllDay = true)
		assertTrue(valid)
	}

	@Test fun `past date is invalid`() {
		val t = task(date = LocalDate.now().minusDays(1))
		val (valid, msg) = checkValidTask(task = t)
		assertFalse(valid)
		assertEquals("Past dates are not allowed", msg)
	}

	@Test fun `future date short circuits as valid`() {
		val t = task(date = LocalDate.now().plusDays(1), start = LocalTime.of(9, 0), end = LocalTime.of(10, 0))
		val (valid, msg) = checkValidTask(task = t)
		assertTrue(valid)
		assertEquals("Future Task", msg)
	}
}
