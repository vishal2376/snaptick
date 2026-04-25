package com.vishal2376.snaptick.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import androidx.work.workDataOf
import com.vishal2376.snaptick.data.calendar.CalendarPusher
import com.vishal2376.snaptick.data.calendar.CalendarRepository
import com.vishal2376.snaptick.data.local.TaskDatabase
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.receiver.ReminderReceiver
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.ReminderScheduler
import com.vishal2376.snaptick.util.SettingsStore
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

/**
 * Walks the post-fire re-arm path. The Glance widget action chain
 * `AlarmManager fires → ReminderReceiver enqueues this worker → worker runs →
 * scheduler arms the next occurrence` is the only thing that keeps a repeat
 * task firing after the first time. This test pins the worker's contract:
 * given a task id in input data, look it up and call `scheduler.schedule(task)`.
 *
 * We don't have a clean public way to ask AlarmManager "is there a pending
 * intent for this id?", but `PendingIntent.getBroadcast` with `FLAG_NO_CREATE`
 * returns non-null exactly when a matching PendingIntent exists. That is
 * enough to confirm the scheduler was invoked.
 */
@RunWith(AndroidJUnit4::class)
class RescheduleSingleReminderWorkerTest {

	private lateinit var context: Context
	private lateinit var db: TaskDatabase
	private lateinit var repo: TaskRepository
	private lateinit var scheduler: ReminderScheduler
	private val testTaskId = 70_001  // unique within the test process

	@Before fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		WorkManagerTestInitHelper.initializeTestWorkManager(context, Configuration.Builder().build())
		db = Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java)
			.allowMainThreadQueries()
			.build()
		val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
		scheduler = ReminderScheduler(context, am)
		val settings = SettingsStore(context)
		val pusher = CalendarPusher(CalendarRepository(context), db.taskDao(), settings)
		repo = TaskRepository(db.taskDao(), db.taskCompletionDao(), context, pusher, scheduler)
		// Wipe any leftover pending intent from prior runs.
		scheduler.cancel(testTaskId)
	}

	@After fun tearDown() {
		scheduler.cancel(testTaskId)
		db.close()
	}

	@Test fun worker_armsAlarmForTaskInDb() = runBlocking {
		val seeded = Task(
			id = testTaskId, uuid = "single-rearm",
			title = "Repeat task",
			startTime = LocalTime.of(23, 59),
			endTime = LocalTime.of(23, 59),
			reminder = true,
			isRepeated = true, repeatWeekdays = "0,1,2,3,4,5,6",
			date = LocalDate.now(),
		)
		// Bypass repo.insertTask because that path also calls scheduler.schedule
		// itself; we want to isolate the worker's behaviour.
		db.taskDao().insertTask(seeded)

		val worker = TestListenableWorkerBuilder<RescheduleSingleReminderWorker>(context)
			.setInputData(workDataOf(Constants.TASK_ID to testTaskId))
			.setWorkerFactory(factory())
			.build()

		val result = worker.doWork()
		assertEquals(ListenableWorker.Result.success(), result)

		// PendingIntent.getBroadcast with FLAG_NO_CREATE returns non-null only
		// if the scheduler armed a PI with this task id.
		val pi = PendingIntent.getBroadcast(
			context,
			testTaskId,
			Intent(context, ReminderReceiver::class.java),
			PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
		)
		assertNotNull("worker did not arm an alarm for task $testTaskId", pi)
	}

	@Test fun worker_succeeds_silently_whenTaskMissing() = runBlocking {
		val worker = TestListenableWorkerBuilder<RescheduleSingleReminderWorker>(context)
			.setInputData(workDataOf(Constants.TASK_ID to 999_999))  // not in DB
			.setWorkerFactory(factory())
			.build()

		val result = worker.doWork()
		// Missing task is not an error - the user might have deleted it
		// between the alarm firing and this worker running.
		assertEquals(ListenableWorker.Result.success(), result)
	}

	@Test fun worker_failsCleanly_whenTaskIdInputMissing() = runBlocking {
		val worker = TestListenableWorkerBuilder<RescheduleSingleReminderWorker>(context)
			.setWorkerFactory(factory())
			.build()
		val result = worker.doWork()
		assertEquals(ListenableWorker.Result.failure(), result)
	}

	private fun factory(): WorkerFactory = object : WorkerFactory() {
		override fun createWorker(
			appContext: Context,
			workerClassName: String,
			workerParameters: WorkerParameters,
		): ListenableWorker = RescheduleSingleReminderWorker(
			appContext, workerParameters, repo, scheduler,
		)
	}
}
