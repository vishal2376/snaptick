package com.vishal2376.snaptick.widget.worker

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
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.util.SettingsStore
import com.vishal2376.snaptick.widget.state.WidgetStateDefinition
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class WidgetUpdateWorkerTest {

	private lateinit var context: Context
	private lateinit var db: TaskDatabase
	private lateinit var repo: TaskRepository
	private lateinit var settings: SettingsStore

	@Before fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		WorkManagerTestInitHelper.initializeTestWorkManager(context, Configuration.Builder().build())
		db = Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java)
			.allowMainThreadQueries()
			.build()
		settings = SettingsStore(context)
		val pusher = CalendarPusher(CalendarRepository(context), db.taskDao(), settings)
		repo = TaskRepository(db.taskDao(), context, pusher)
	}

	@After fun tearDown() { db.close() }

	private fun today(title: String, completed: Boolean) = Task(
		id = 0, uuid = "u-$title", title = title, isCompleted = completed,
		startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0),
		date = LocalDate.now()
	)

	private fun factory() = object : WorkerFactory() {
		override fun createWorker(
			appContext: Context,
			workerClassName: String,
			workerParameters: WorkerParameters
		): ListenableWorker = WidgetUpdateWorker(appContext, workerParameters, repo, settings)
	}

	@Test fun doWork_writesOnlyIncompleteTasksToWidgetState() = runBlocking {
		repo.insertTask(today("Stand up", completed = false))
		repo.insertTask(today("Lunch", completed = false))
		repo.insertTask(today("Old meeting", completed = true))

		val worker = TestListenableWorkerBuilder<WidgetUpdateWorker>(context)
			.setWorkerFactory(factory())
			.build()

		val result = worker.doWork()
		assertEquals(ListenableWorker.Result.success().javaClass, result.javaClass)

		val dataStore = WidgetStateDefinition.getDataStore(context, "unused")
		val state = dataStore.data.first()
		val titles = state.tasks.map { it.title }
		assertEquals("Expected two incomplete tasks", 2, titles.size)
		assertTrue("Stand up" in titles)
		assertTrue("Lunch" in titles)
		assertTrue("Completed task should be excluded", "Old meeting" !in titles)
	}
}
