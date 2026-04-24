package com.vishal2376.snaptick.worker

import android.content.Context
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
import com.vishal2376.snaptick.util.SettingsStore
import com.vishal2376.snaptick.domain.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class RepeatTaskWorkerTest {

	private lateinit var context: Context
	private lateinit var db: TaskDatabase
	private lateinit var repo: TaskRepository

	@Before fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		WorkManagerTestInitHelper.initializeTestWorkManager(context, Configuration.Builder().build())
		db = Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java)
			.allowMainThreadQueries()
			.build()
		val settings = SettingsStore(context)
		val pusher = CalendarPusher(CalendarRepository(context), db.taskDao(), settings)
		repo = TaskRepository(db.taskDao(), context, pusher)
	}

	@After fun tearDown() { db.close() }

	private fun factory() = object : WorkerFactory() {
		override fun createWorker(
			appContext: Context,
			workerClassName: String,
			workerParameters: WorkerParameters
		): ListenableWorker = RepeatTaskWorker(appContext, workerParameters, repo)
	}

	@Test fun doWork_createsTodayInstanceForRepeatedTask_matchingCurrentDayOfWeek() = runBlocking {
		val yesterday = LocalDate.now().minusDays(1)
		val todayWeekday = (LocalDate.now().dayOfWeek.value - 1).toString() // Mon=0…Sun=6

		repo.insertTask(
			Task(
				id = 0, uuid = "rep-1", title = "Repeating",
				startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0),
				reminder = false, isRepeated = true,
				repeatWeekdays = todayWeekday,
				date = yesterday
			)
		)

		val worker = TestListenableWorkerBuilder<RepeatTaskWorker>(context)
			.setWorkerFactory(factory())
			.build()

		val result = worker.doWork()
		assertEquals(ListenableWorker.Result.success().javaClass, result.javaClass)

		val all = repo.getAllTasks().first()
		// Original row is marked isRepeated = false; a new repeated task exists for today.
		val originals = all.filter { it.uuid == "rep-1" && it.date == yesterday }
		assertEquals(1, originals.size)
		assertFalse("Original row should be flipped to isRepeated=false", originals.single().isRepeated)

		val todays = all.filter { it.date == LocalDate.now() }
		assertEquals(1, todays.size)
		assertTrue("Today's copy should keep isRepeated=true", todays.single().isRepeated)
		assertEquals("Repeating", todays.single().title)
	}

	@Test fun doWork_skipsRepeatedTaskWhoseWeekdaysDoNotIncludeToday() = runBlocking {
		val yesterday = LocalDate.now().minusDays(1)
		// Pick a weekday that is NOT today.
		val notToday = ((LocalDate.now().dayOfWeek.value - 1 + 3) % 7).toString()

		repo.insertTask(
			Task(
				id = 0, uuid = "rep-skip", title = "Skip me",
				startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0),
				isRepeated = true, repeatWeekdays = notToday, date = yesterday
			)
		)

		val worker = TestListenableWorkerBuilder<RepeatTaskWorker>(context)
			.setWorkerFactory(factory())
			.build()

		val result = worker.doWork()
		assertEquals(ListenableWorker.Result.success().javaClass, result.javaClass)

		val todays = repo.getAllTasks().first().filter { it.date == LocalDate.now() }
		assertTrue("No today task should be created", todays.isEmpty())
	}
}
