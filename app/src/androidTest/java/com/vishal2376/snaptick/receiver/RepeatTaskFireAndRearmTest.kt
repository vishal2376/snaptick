package com.vishal2376.snaptick.receiver

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.WorkManagerTestInitHelper
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.NotificationHelper
import com.vishal2376.snaptick.worker.RescheduleSingleReminderWorker
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pins the chained "alarm fired → receiver → worker enqueued with correct
 * task id" path. This is the link that keeps a repeat task firing on the
 * next occurrence after the first fire. If the receiver fails to forward
 * the task id, the post-fire re-arm worker has nothing to look up and the
 * repeat task silently dies after the first reminder.
 *
 * The test installs a custom WorkerFactory so the enqueued
 * RescheduleSingleReminderWorker is intercepted; we capture its
 * WorkerParameters and assert the task id round-trips end-to-end.
 */
@RunWith(AndroidJUnit4::class)
class RepeatTaskFireAndRearmTest {

	@get:Rule val permissionRule: GrantPermissionRule = if (Build.VERSION.SDK_INT >= 33) {
		GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS)
	} else {
		GrantPermissionRule.grant()
	}

	private lateinit var context: Context
	private val testTaskId = 72_001
	private var capturedTaskId: Int = -1

	@Before fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		capturedTaskId = -1
		val factory = object : WorkerFactory() {
			override fun createWorker(
				appContext: Context,
				workerClassName: String,
				workerParameters: WorkerParameters,
			): ListenableWorker? {
				if (workerClassName == RescheduleSingleReminderWorker::class.java.name) {
					capturedTaskId = workerParameters.inputData.getInt(Constants.TASK_ID, -1)
					return RecordingNoopWorker(appContext, workerParameters)
				}
				return null
			}
		}
		WorkManagerTestInitHelper.initializeTestWorkManager(
			context,
			Configuration.Builder().setWorkerFactory(factory).build(),
		)
		// Notification channel must exist or `notify` is a silent no-op.
		NotificationHelper(context).createNotificationChannel()

		val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		nm.cancel(testTaskId)
	}

	@After fun tearDown() {
		val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		nm.cancel(testTaskId)
	}

	@Test fun receiver_forwardsTaskId_toRescheduleWorker_andPostsNotification() {
		val fireIntent = Intent().apply {
			putExtra(Constants.TASK_ID, testTaskId)
			putExtra(Constants.TASK_UUID, "fire-and-rearm")
			putExtra(Constants.TASK_TITLE, "Daily standup")
			putExtra(Constants.TASK_TIME, "09:00")
		}

		ReminderReceiver().onReceive(context, fireIntent)

		// (a) Notification was posted with the same id - confirms the user
		//     actually saw a reminder.
		val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		val active = pollFor { nm.activeNotifications.any { it.id == testTaskId } }
		assertNotNull(
			"notification not posted - downstream re-arm path will never execute",
			active.takeIf { it } ?: error("notification with id $testTaskId not posted"),
		)

		// (b) Worker received the same task id via inputData. Without this,
		//     the worker has no task to look up and the repeat task dies.
		val workerCaptured = pollFor { capturedTaskId == testTaskId }
		assertEquals(
			"reschedule worker did not receive the correct task id",
			testTaskId,
			capturedTaskId.takeIf { workerCaptured } ?: capturedTaskId,
		)
	}

	private fun pollFor(timeoutMs: Long = 3_000, predicate: () -> Boolean): Boolean {
		val deadline = System.currentTimeMillis() + timeoutMs
		while (System.currentTimeMillis() < deadline) {
			if (predicate()) return true
			Thread.sleep(50)
		}
		return predicate()
	}
}

/**
 * Trivial Worker the test factory hands back so the enqueued work succeeds
 * without doing real DB / scheduler I/O. We only care that the input data
 * was captured.
 */
private class RecordingNoopWorker(context: Context, params: WorkerParameters) :
	Worker(context, params) {
	override fun doWork(): Result = Result.success()
}
