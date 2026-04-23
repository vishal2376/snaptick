package com.vishal2376.snaptick.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.testing.WorkManagerTestInitHelper
import com.vishal2376.snaptick.data.local.TaskDatabase
import com.vishal2376.snaptick.domain.model.Task
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class ToggleTaskActionTest {

	private lateinit var context: Context
	private lateinit var db: TaskDatabase

	@Before fun setUp() = runBlocking {
		context = ApplicationProvider.getApplicationContext()
		WorkManagerTestInitHelper.initializeTestWorkManager(
			context,
			Configuration.Builder().build()
		)
		db = TaskDatabase.getInstance(context)
		db.taskDao().deleteAllTasks()
		db.taskDao().insertTask(
			Task(
				id = 1, uuid = "u1", title = "Task",
				isCompleted = false,
				startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0),
				date = LocalDate.now()
			)
		)
	}

	@After fun tearDown() = runBlocking {
		db.taskDao().deleteAllTasks()
	}

	@Test fun onAction_togglesTaskCompletion() = runBlocking {
		val params: ActionParameters = actionParametersOf(ToggleTaskAction.TaskIdKey to 1)
		val glanceId = object : GlanceId {}
		ToggleTaskAction().onAction(context, glanceId, params)

		val toggled = db.taskDao().getTaskById(1)
		assertTrue(toggled.isCompleted)

		// toggle again → back to false
		ToggleTaskAction().onAction(context, glanceId, params)
		val back = db.taskDao().getTaskById(1)
		assertFalse(back.isCompleted)
	}

	@Test fun onAction_noopForMissingParam() = runBlocking {
		val params: ActionParameters = actionParametersOf()
		val glanceId = object : GlanceId {}
		ToggleTaskAction().onAction(context, glanceId, params)
		// task 1 still not completed
		assertFalse(db.taskDao().getTaskById(1).isCompleted)
	}
}
