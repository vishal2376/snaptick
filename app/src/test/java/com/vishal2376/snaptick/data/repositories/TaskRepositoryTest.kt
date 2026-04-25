package com.vishal2376.snaptick.data.repositories

import android.content.Context
import com.vishal2376.snaptick.data.calendar.CalendarPusher
import com.vishal2376.snaptick.data.local.TaskCompletion
import com.vishal2376.snaptick.data.local.TaskCompletionDao
import com.vishal2376.snaptick.data.local.TaskDao
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.util.ReminderScheduler
import com.vishal2376.snaptick.widget.worker.WidgetUpdateWorker
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

/**
 * Unit-tests TaskRepository's CRUD lifecycle hooks introduced in the
 * reminder revamp + the per-date completion methods introduced for repeat
 * task virtualization.
 *
 * The Repository owns scheduling: insert/update/delete must always cancel
 * any prior alarm and re-schedule when the task is still active. The widget
 * worker side-effect is stubbed via mockkObject so we don't need a real
 * WorkManager.
 */
class TaskRepositoryTest {

	private lateinit var dao: TaskDao
	private lateinit var completionDao: TaskCompletionDao
	private lateinit var context: Context
	private lateinit var calendarPusher: CalendarPusher
	private lateinit var scheduler: ReminderScheduler
	private lateinit var repo: TaskRepository

	private fun task(
		id: Int = 1,
		uuid: String = "u$id",
		reminder: Boolean = true,
		isCompleted: Boolean = false,
		isRepeated: Boolean = false,
	) = Task(
		id = id, uuid = uuid, title = "T",
		startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0),
		reminder = reminder, isCompleted = isCompleted,
		isRepeated = isRepeated, repeatWeekdays = if (isRepeated) "0,1,2,3,4" else "",
		date = LocalDate.now(),
	)

	@Before fun setUp() {
		dao = mockk(relaxed = true)
		completionDao = mockk(relaxed = true)
		context = mockk(relaxed = true)
		calendarPusher = mockk(relaxed = true)
		scheduler = mockk(relaxed = true)

		// Stub the static WidgetUpdateWorker.enqueueWorker side-effect so we
		// don't need a real WorkManager in unit tests.
		mockkObject(WidgetUpdateWorker.Companion)
		justRun { WidgetUpdateWorker.enqueueWorker(any(), any()) }

		justRun { scheduler.schedule(any()) }
		justRun { scheduler.cancel(any()) }
		coJustRun { calendarPusher.pushInsert(any()) }
		coJustRun { calendarPusher.pushUpdate(any()) }
		coJustRun { calendarPusher.pushDelete(any()) }
		coJustRun { dao.insertTask(any()) }
		coJustRun { dao.updateTask(any()) }
		coJustRun { dao.deleteTask(any()) }
		coJustRun { dao.deleteAllTasks() }
		coJustRun { completionDao.insert(any()) }
		coJustRun { completionDao.delete(any(), any()) }
		coJustRun { completionDao.deleteAllForTask(any()) }

		repo = TaskRepository(dao, completionDao, context, calendarPusher, scheduler)
	}

	@After fun tearDown() {
		unmockkObject(WidgetUpdateWorker.Companion)
	}

	@Test fun `insertTask schedules reminder and pushes to calendar`() = runTest {
		val t = task(id = 0)
		coEvery { dao.getTaskByUuid(t.uuid) } returns t.copy(id = 1)

		repo.insertTask(t)

		coVerify(exactly = 1) { dao.insertTask(t) }
		verify(exactly = 1) { scheduler.schedule(match { it.id == 1 }) }
		coVerify(exactly = 1) { calendarPusher.pushInsert(any()) }
	}

	@Test fun `updateTask cancels old alarm before persisting then reschedules`() = runTest {
		val t = task(id = 7)
		repo.updateTask(t)

		// Order matters: cancel first so a stale alarm can't fire while we update.
		verify(exactly = 1) { scheduler.cancel(7) }
		coVerify(exactly = 1) { dao.updateTask(t) }
		verify(exactly = 1) { scheduler.schedule(t) }
	}

	@Test fun `updateTask with reminder off still calls schedule (scheduler is the policy)`() = runTest {
		// The repository always delegates to the scheduler. The scheduler's
		// internal contract is what decides "no alarm needed for this task";
		// the repository doesn't second-guess it.
		val t = task(id = 5, reminder = false)
		repo.updateTask(t)
		verify(exactly = 1) { scheduler.cancel(5) }
		verify(exactly = 1) { scheduler.schedule(t) }
	}

	@Test fun `deleteTask cancels alarm and clears all completions for that uuid`() = runTest {
		val t = task(id = 3, uuid = "u-bye")
		repo.deleteTask(t)
		verify(exactly = 1) { scheduler.cancel(3) }
		coVerify(exactly = 1) { completionDao.deleteAllForTask("u-bye") }
		coVerify(exactly = 1) { dao.deleteTask(t) }
		coVerify(exactly = 1) { calendarPusher.pushDelete(t) }
	}

	@Test fun `markCompletedForDate writes a TaskCompletion row`() = runTest {
		repo.markCompletedForDate("u1", LocalDate.of(2026, 5, 4))
		coVerify(exactly = 1) {
			completionDao.insert(TaskCompletion(uuid = "u1", date = "2026-05-04"))
		}
	}

	@Test fun `unmarkCompletedForDate deletes the row by uuid + date`() = runTest {
		repo.unmarkCompletedForDate("u1", LocalDate.of(2026, 5, 4))
		coVerify(exactly = 1) { completionDao.delete("u1", "2026-05-04") }
	}

	@Test fun `isCompletedOn delegates to the DAO`() = runTest {
		coEvery { completionDao.isCompleted("u1", "2026-05-04") } returns true
		assertTrue(repo.isCompletedOn("u1", LocalDate.of(2026, 5, 4)))
	}

	@Test fun `isCompletedOn returns false when DAO returns false`() = runTest {
		coEvery { completionDao.isCompleted("u1", "2026-05-04") } returns false
		assertFalse(repo.isCompletedOn("u1", LocalDate.of(2026, 5, 4)))
	}

	@Test fun `deletePushedCalendarEvents delegates to the pusher and returns its count`() = runTest {
		val tasks = listOf(
			task(id = 1).copy(calendarEventId = 100L),
			task(id = 2).copy(calendarEventId = null),
			task(id = 3).copy(calendarEventId = 102L),
		)
		coEvery { dao.getAllTasksSnapshot() } returns tasks
		coEvery { calendarPusher.deleteAllPushedEvents(tasks) } returns 2

		assertEquals(2, repo.deletePushedCalendarEvents())
		coVerify(exactly = 1) { calendarPusher.deleteAllPushedEvents(tasks) }
	}

	@Test fun `rescheduleAllReminders skips completed and reminder-off tasks`() = runTest {
		val active = task(id = 1, reminder = true, isCompleted = false)
		val noReminder = task(id = 2, reminder = false)
		val completed = task(id = 3, reminder = true, isCompleted = true)
		coEvery { dao.getAllTasksSnapshot() } returns listOf(active, noReminder, completed)
		justRun { scheduler.rescheduleAll(any()) }

		repo.rescheduleAllReminders()

		verify(exactly = 1) { scheduler.rescheduleAll(listOf(active)) }
	}
}
