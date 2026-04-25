package com.vishal2376.snaptick.data.local

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

	private val dbName = "migration-test-db"

	@get:Rule
	val helper: MigrationTestHelper = MigrationTestHelper(
		InstrumentationRegistry.getInstrumentation(),
		TaskDatabase::class.java,
		emptyList(),
		FrameworkSQLiteOpenHelperFactory()
	)

	@Test fun migrate1To2_renamesIsRepeatToIsRepeated_andPreservesData() {
		// Build v1 DB and insert a realistic row using the legacy `isRepeat` column.
		helper.createDatabase(dbName, 1).use { db ->
			db.execSQL(
				"""
				INSERT INTO task_table
					(id, uuid, title, isCompleted, startTime, endTime, reminder, isRepeat, repeatWeekdays, pomodoroTimer, date, priority)
				VALUES
					(1, 'u1', 'Morning Run', 0, '06:30', '07:15', 1, 1, '0,2,4', 900, '2026-04-23', 2),
					(2, 'u2', 'Lunch',       0, '12:00', '13:00', 0, 0, '',      -1,  '2026-04-23', 0)
				""".trimIndent()
			)
		}

		helper.runMigrationsAndValidate(dbName, 4, true, MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).use { db ->
			val cursor = db.query(
				"SELECT id, uuid, title, isCompleted, isRepeated, repeatWeekdays, pomodoroTimer, priority, calendarEventId FROM task_table ORDER BY id"
			)
			cursor.use {
				assertTrue("row 1 exists", it.moveToNext())
				assertEquals(1, it.getInt(0))
				assertEquals("u1", it.getString(1))
				assertEquals("Morning Run", it.getString(2))
				assertEquals(0, it.getInt(3))
				assertEquals(1, it.getInt(4)) // isRepeated carried from isRepeat
				assertEquals("0,2,4", it.getString(5))
				assertEquals(900, it.getInt(6))
				assertEquals(2, it.getInt(7))

				assertTrue("row 2 exists", it.moveToNext())
				assertEquals("Lunch", it.getString(2))
				assertEquals(0, it.getInt(4))

				assertFalse("no extra rows", it.moveToNext())
			}

			// The legacy column must no longer exist.
			val pragma = db.query("PRAGMA table_info(task_table)")
			val columnNames = buildList {
				pragma.use { while (it.moveToNext()) add(it.getString(1)) }
			}
			assertTrue("isRepeated column present", "isRepeated" in columnNames)
			assertFalse("legacy isRepeat column dropped", "isRepeat" in columnNames)
		}
	}

	@Test fun afterMigration_roomCanOpenDatabaseNormally() = runBlocking {
		// Seed a v1 DB, migrate through 1→2→3, then re-open via Room and confirm DAO queries work.
		helper.createDatabase(dbName, 1).use { db ->
			db.execSQL(
				"""
				INSERT INTO task_table
					(id, uuid, title, isCompleted, startTime, endTime, reminder, isRepeat, repeatWeekdays, pomodoroTimer, date, priority)
				VALUES (1, 'u1', 'After', 0, '09:00', '10:00', 0, 0, '', -1, '2026-04-23', 0)
				""".trimIndent()
			)
		}
		helper.runMigrationsAndValidate(dbName, 4, true, MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).close()

		val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
		val room = Room.databaseBuilder(ctx, TaskDatabase::class.java, dbName)
			.addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
			.build()
		try {
			val row = room.taskDao().getAllTasks().first().single()
			assertEquals("After", row.title)
			assertFalse(row.isRepeated)
		} finally {
			room.close()
		}
	}

	@Test fun migrate3To4_taskCompletionDao_roundTripsAfterMigration() = runBlocking {
		// Migrate a v3 db forward, then re-open via Room and exercise the DAO
		// just like production does: insert, read back, delete.
		helper.createDatabase(dbName, 3).close()
		helper.runMigrationsAndValidate(dbName, 4, true, MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).close()

		val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
		val room = Room.databaseBuilder(ctx, TaskDatabase::class.java, dbName)
			.addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
			.build()
		try {
			val dao = room.taskCompletionDao()
			assertFalse(dao.isCompleted("u1", "2026-04-25"))

			dao.insert(TaskCompletion(uuid = "u1", date = "2026-04-25"))
			assertTrue(dao.isCompleted("u1", "2026-04-25"))

			// Composite key: same uuid different date is independent.
			assertFalse(dao.isCompleted("u1", "2026-04-26"))

			dao.delete("u1", "2026-04-25")
			assertFalse(dao.isCompleted("u1", "2026-04-25"))

			// deleteAllForTask wipes every date for the uuid.
			dao.insert(TaskCompletion("u2", "2026-04-25"))
			dao.insert(TaskCompletion("u2", "2026-04-26"))
			dao.deleteAllForTask("u2")
			assertFalse(dao.isCompleted("u2", "2026-04-25"))
			assertFalse(dao.isCompleted("u2", "2026-04-26"))
		} finally {
			room.close()
		}
	}

	@Test fun migrate3To4_createsEmptyTaskCompletionsTable() {
		// Seed a v3 DB with a task, then migrate forward.
		helper.createDatabase(dbName, 3).use { db ->
			db.execSQL(
				"""
				INSERT INTO task_table
					(id, uuid, title, isCompleted, startTime, endTime, reminder, isRepeated, repeatWeekdays, pomodoroTimer, date, priority, calendarEventId)
				VALUES (1, 'u1', 'Run', 0, '06:00', '07:00', 1, 1, '0,2,4', -1, '2026-04-25', 1, NULL)
				""".trimIndent()
			)
		}

		helper.runMigrationsAndValidate(dbName, 4, true, MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).use { db ->
			// Existing task row preserved.
			db.query("SELECT id, title FROM task_table").use {
				assertTrue(it.moveToNext())
				assertEquals(1, it.getInt(0))
				assertEquals("Run", it.getString(1))
			}
			// New table exists and is empty.
			db.query("SELECT COUNT(*) FROM task_completions").use {
				assertTrue(it.moveToNext())
				assertEquals(0, it.getInt(0))
			}
			// Composite primary key check: insert + duplicate insert.
			db.execSQL("INSERT INTO task_completions(uuid, date) VALUES('u1','2026-04-25')")
			db.execSQL("INSERT OR IGNORE INTO task_completions(uuid, date) VALUES('u1','2026-04-25')")
			db.query("SELECT COUNT(*) FROM task_completions").use {
				assertTrue(it.moveToNext())
				assertEquals(1, it.getInt(0))
			}
		}
	}
}
