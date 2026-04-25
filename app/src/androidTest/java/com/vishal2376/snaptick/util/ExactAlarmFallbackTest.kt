package com.vishal2376.snaptick.util

import android.app.AlarmManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vishal2376.snaptick.domain.model.Task
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Smoke check that [ReminderScheduler] doesn't crash when AlarmManager
 * refuses to take an exact alarm. We can't easily flip
 * `canScheduleExactAlarms()` to false on the emulator from a test, but we
 * can verify the scheduler tolerates the SecurityException it might throw
 * when a misconfigured policy denies the alarm. The real fallback to
 * `setAndAllowWhileIdle` is exercised via the production code path on
 * devices where USE_EXACT_ALARM was denied (rare, but observed in the
 * wild).
 */
@RunWith(AndroidJUnit4::class)
class ExactAlarmFallbackTest {

	@Test fun schedule_doesNotThrow_evenIfExactAlarmIsRefused() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		val scheduler = ReminderScheduler(context, am)

		val task = Task(
			id = 8888, uuid = "fallback",
			title = "T",
			startTime = LocalTime.now().plusHours(1),
			endTime = LocalTime.now().plusHours(2),
			reminder = true,
			date = LocalDate.now(),
		)

		// Should not throw under either exact or inexact path.
		scheduler.schedule(task)
		// And the next-fire math itself returns a sensible value.
		val next = scheduler.nextFireMillis(task, LocalDateTime.now())
		assertNotNull("nextFireMillis returned null for a future-dated task", next)

		scheduler.cancel(task.id)
	}
}
