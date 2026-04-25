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
import com.vishal2376.snaptick.data.calendar.CalendarPusher
import com.vishal2376.snaptick.data.calendar.CalendarRepository
import com.vishal2376.snaptick.data.local.TaskDatabase
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.receiver.ReminderReceiver
import com.vishal2376.snaptick.util.ReminderScheduler
import com.vishal2376.snaptick.util.SettingsStore
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

/**
 * The boot-recovery worker. Triggered by `SystemEventReceiver` on
 * BOOT_COMPLETED, TIME_SET, etc. Walks every task and re-arms the ones that
 * are still active (reminder=true AND not globally completed). Tasks that
 * got marked complete or had their reminder turned off must NOT get an
 * alarm armed - that would resurrect a reminder the user explicitly silenced.
 */
@RunWith(AndroidJUnit4::class)
class RescheduleAllRemindersWorkerTest {

	private lateinit var context: Context
	private lateinit var db: TaskDatabase
	private lateinit var repo: TaskRepository
	private lateinit var scheduler: ReminderScheduler

	// Use a stable id range to avoid collision with other instrumented tests
	// that share the same emulator's AlarmManager.
	private val activeId = 71_001
	private val completedId = 71_002
	private val noReminderId = 71_003

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

		// Clear any leftover alarms from prior runs so assertions are clean.
		listOf(activeId, completedId, noReminderId).forEach { scheduler.cancel(it) }
	}

	@After fun tearDown() {
		listOf(activeId, completedId, noReminderId).forEach { scheduler.cancel(it) }
		db.close()
	}

	@Test fun worker_armsActiveTask_butNotCompleted_orReminderOff() = runBlocking {
		fun task(id: Int, completed: Boolean, reminder: Boolean) = Task(
			id = id, uuid = "all-rearm-$id",
			title = "T$id",
			startTime = LocalTime.of(23, 59),
			endTime = LocalTime.of(23, 59),
			reminder = reminder, isCompleted = completed,
			isRepeated = true, repeatWeekdays = "0,1,2,3,4,5,6",
			date = LocalDate.now(),
		)
		// Insert via DAO directly so we skip the repo's auto-schedule path.
		db.taskDao().insertTask(task(activeId, completed = false, reminder = true))
		db.taskDao().insertTask(task(completedId, completed = true, reminder = true))
		db.taskDao().insertTask(task(noReminderId, completed = false, reminder = false))

		val worker = TestListenableWorkerBuilder<RescheduleAllRemindersWorker>(context)
			.setWorkerFactory(factory())
			.build()
		val result = worker.doWork()
		assertEquals(ListenableWorker.Result.success(), result)

		assertNotNull("active task must be armed", pendingIntentFor(activeId))
		assertNull("completed task must NOT be armed", pendingIntentFor(completedId))
		assertNull("reminder-off task must NOT be armed", pendingIntentFor(noReminderId))
	}

	@Test fun worker_succeedsOnEmptyDb() = runBlocking {
		val worker = TestListenableWorkerBuilder<RescheduleAllRemindersWorker>(context)
			.setWorkerFactory(factory())
			.build()
		assertEquals(ListenableWorker.Result.success(), worker.doWork())
	}

	private fun pendingIntentFor(taskId: Int): PendingIntent? = PendingIntent.getBroadcast(
		context,
		taskId,
		Intent(context, ReminderReceiver::class.java),
		PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
	)

	private fun factory(): WorkerFactory = object : WorkerFactory() {
		override fun createWorker(
			appContext: Context,
			workerClassName: String,
			workerParameters: WorkerParameters,
		): ListenableWorker = RescheduleAllRemindersWorker(
			appContext, workerParameters, repo,
		)
	}
}
