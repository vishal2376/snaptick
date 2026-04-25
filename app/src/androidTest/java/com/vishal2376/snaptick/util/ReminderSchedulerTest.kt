package com.vishal2376.snaptick.util

import android.app.AlarmManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vishal2376.snaptick.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Verifies the next-fire math in [ReminderScheduler] without going to
 * AlarmManager. The pure `nextFireMillis(task, now)` function is deterministic
 * and timezone-aware; it's the unit we care about.
 */
@RunWith(AndroidJUnit4::class)
class ReminderSchedulerTest {

	private lateinit var context: Context
	private lateinit var scheduler: ReminderScheduler

	@Before fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		scheduler = ReminderScheduler(context, am)
	}

	private fun oneOff(date: LocalDate, time: LocalTime) = Task(
		id = 1, uuid = "u", title = "T",
		startTime = time, endTime = time.plusHours(1),
		reminder = true, date = date,
	)

	private fun repeat(weekdays: String, time: LocalTime) = Task(
		id = 2, uuid = "u2", title = "R",
		startTime = time, endTime = time.plusHours(1),
		reminder = true, isRepeated = true, repeatWeekdays = weekdays,
		date = LocalDate.now(),
	)

	@Test fun oneOff_inFuture_returnsLocalEpochMillis() {
		val now = LocalDateTime.of(2026, 5, 1, 9, 0)
		val task = oneOff(LocalDate.of(2026, 5, 1), LocalTime.of(15, 30))
		val expected = LocalDateTime.of(2026, 5, 1, 15, 30)
			.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
		assertEquals(expected, scheduler.nextFireMillis(task, now))
	}

	@Test fun oneOff_inPast_returnsNull() {
		val now = LocalDateTime.of(2026, 5, 1, 16, 0)
		val task = oneOff(LocalDate.of(2026, 5, 1), LocalTime.of(15, 30))
		assertNull(scheduler.nextFireMillis(task, now))
	}

	@Test fun repeat_today_inFuture_picksToday() {
		// 2026-05-04 is Monday → dayOfWeek=0
		val now = LocalDateTime.of(2026, 5, 4, 9, 0)
		val task = repeat(weekdays = "0,2,4", time = LocalTime.of(15, 30))
		val expected = LocalDateTime.of(2026, 5, 4, 15, 30)
			.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
		assertEquals(expected, scheduler.nextFireMillis(task, now))
	}

	@Test fun repeat_today_inPast_picksNextWeekdayInSet() {
		// Monday 16:00; weekdays = MON, WED, FRI → next is WED
		val now = LocalDateTime.of(2026, 5, 4, 16, 0)
		val task = repeat(weekdays = "0,2,4", time = LocalTime.of(15, 30))
		val expected = LocalDateTime.of(2026, 5, 6, 15, 30) // Wed
			.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
		assertEquals(expected, scheduler.nextFireMillis(task, now))
	}

	@Test fun repeat_emptyWeekdays_returnsNull() {
		val now = LocalDateTime.of(2026, 5, 4, 9, 0)
		val task = repeat(weekdays = "", time = LocalTime.of(15, 30))
		assertNull(scheduler.nextFireMillis(task, now))
	}

	@Test fun repeat_singleWeekday_picksNextWeekOccurrence() {
		// Tuesday now, only Friday in set → next Friday
		val now = LocalDateTime.of(2026, 5, 5, 9, 0)
		val task = repeat(weekdays = "4", time = LocalTime.of(15, 30))
		val expected = LocalDateTime.of(2026, 5, 8, 15, 30) // Fri
			.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
		assertEquals(expected, scheduler.nextFireMillis(task, now))
	}

	@Test fun cancel_isIdempotent_whenNoAlarmExists() {
		// Should not throw.
		scheduler.cancel(taskId = 999)
		assertTrue(true)
	}

	@Test fun schedule_setsAlarmReachableViaCancel() {
		val task = oneOff(LocalDate.now().plusDays(1), LocalTime.of(10, 0))
		scheduler.schedule(task)
		// Idempotent re-schedule should also work.
		scheduler.schedule(task)
		scheduler.cancel(task.id)
		// No assertion target; verifying no crash and exact-alarm path completes.
		assertNotNull(task)
	}
}
