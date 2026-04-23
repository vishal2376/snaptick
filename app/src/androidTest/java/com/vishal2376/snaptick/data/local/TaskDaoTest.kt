package com.vishal2376.snaptick.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vishal2376.snaptick.domain.model.Task
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
class TaskDaoTest {

	private lateinit var db: TaskDatabase
	private lateinit var dao: TaskDao

	@Before fun setUp() {
		val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
		db = Room.inMemoryDatabaseBuilder(ctx, TaskDatabase::class.java)
			.allowMainThreadQueries()
			.build()
		dao = db.taskDao()
	}

	@After fun tearDown() { db.close() }

	private fun task(id: Int = 0, date: LocalDate = LocalDate.now(), repeated: Boolean = false) = Task(
		id = id, uuid = "u$id", title = "T$id",
		startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0),
		isRepeated = repeated, date = date
	)

	@Test fun insertAndGetById_roundtrip() = runBlocking {
		dao.insertTask(task(id = 1))
		val loaded = dao.getTaskById(1)
		assertEquals("T1", loaded.title)
	}

	@Test fun getTasksByDate_filtersToTheDate() = runBlocking {
		dao.insertTask(task(id = 1, date = LocalDate.now()))
		dao.insertTask(task(id = 2, date = LocalDate.now().plusDays(1)))
		val todays = dao.getTasksByDate(LocalDate.now().toString()).first()
		assertEquals(1, todays.size)
		assertEquals(1, todays.single().id)
	}

	@Test fun deleteAllTasks_empties() = runBlocking {
		dao.insertTask(task(id = 1))
		dao.insertTask(task(id = 2))
		dao.deleteAllTasks()
		val rows = dao.getAllTasks().first()
		assertTrue(rows.isEmpty())
	}

	@Test fun getLastRepeatedTasks_filtersRepeatedAndPast() = runBlocking {
		dao.insertTask(task(id = 1, date = LocalDate.now().minusDays(1), repeated = true))
		dao.insertTask(task(id = 2, date = LocalDate.now(), repeated = true))
		dao.insertTask(task(id = 3, date = LocalDate.now().minusDays(1), repeated = false))
		val result = dao.getLastRepeatedTasks(LocalDate.now().toString())
		assertEquals(listOf(1), result.map { it.id })
	}

	@Test fun updateTask_persistsChanges() = runBlocking {
		dao.insertTask(task(id = 1))
		dao.updateTask(task(id = 1).copy(title = "Updated"))
		assertEquals("Updated", dao.getTaskById(1).title)
	}

	@Test fun deleteTask_removesRow() = runBlocking {
		val t = task(id = 1)
		dao.insertTask(t)
		dao.deleteTask(t)
		assertTrue(dao.getAllTasks().first().isEmpty())
	}
}
