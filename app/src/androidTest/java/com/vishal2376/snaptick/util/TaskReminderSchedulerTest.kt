package com.vishal2376.snaptick.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.vishal2376.snaptick.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class TaskReminderSchedulerTest {

	private lateinit var context: Context
	private lateinit var scheduler: TaskReminderScheduler

	@Before fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		val cfg = Configuration.Builder().setMinimumLoggingLevel(android.util.Log.DEBUG).build()
		WorkManagerTestInitHelper.initializeTestWorkManager(context, cfg)
		scheduler = TaskReminderScheduler(context)
	}

	private fun task(uuid: String, reminder: Boolean = true, futureHours: Long = 2) = Task(
		id = 1, uuid = uuid, title = "T",
		startTime = LocalTime.now().plusHours(futureHours),
		endTime = LocalTime.now().plusHours(futureHours + 1),
		reminder = reminder, date = LocalDate.now()
	)

	@Test fun schedule_enqueuesWorkTaggedWithUuid() {
		scheduler.schedule(task(uuid = "tag-123"))
		val infos = WorkManager.getInstance(context).getWorkInfosByTag("tag-123").get()
		assertEquals(1, infos.size)
		assertTrue(infos.single().state == WorkInfo.State.ENQUEUED || infos.single().state == WorkInfo.State.RUNNING)
	}

	@Test fun schedule_doesNothing_whenReminderOff() {
		scheduler.schedule(task(uuid = "tag-noop", reminder = false))
		val infos = WorkManager.getInstance(context).getWorkInfosByTag("tag-noop").get()
		assertTrue(infos.isEmpty())
	}

	@Test fun cancel_cancelsTaggedWork() {
		scheduler.schedule(task(uuid = "tag-cancel"))
		scheduler.cancel("tag-cancel")
		val infos = WorkManager.getInstance(context).getWorkInfosByTag("tag-cancel").get()
		assertTrue(infos.all { it.state == WorkInfo.State.CANCELLED })
	}
}
