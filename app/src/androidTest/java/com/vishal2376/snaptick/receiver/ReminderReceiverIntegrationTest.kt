package com.vishal2376.snaptick.receiver

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vishal2376.snaptick.util.Constants
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifies the alarm-fire path produces an actual notification via
 * [android.app.NotificationManager]. Constructs a fake "alarm-fired"
 * intent with the same extras [com.vishal2376.snaptick.util.ReminderScheduler]
 * builds, dispatches to the receiver directly, then asserts the notification
 * is visible.
 */
@RunWith(AndroidJUnit4::class)
class ReminderReceiverIntegrationTest {

	private lateinit var context: Context
	private lateinit var nm: NotificationManager
	private val taskId = 9999

	@Before fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		nm.cancel(taskId)
	}

	@After fun tearDown() {
		nm.cancel(taskId)
	}

	@Test fun onReceive_postsNotification_withMatchingId() {
		val intent = Intent().apply {
			putExtra(Constants.TASK_ID, taskId)
			putExtra(Constants.TASK_UUID, "test-uuid")
			putExtra(Constants.TASK_TITLE, "Integration test reminder")
			putExtra(Constants.TASK_TIME, "10:00 AM")
		}

		ReminderReceiver().onReceive(context, intent)

		// Some emulators surface the notification asynchronously; poll briefly.
		val active = pollFor { nm.activeNotifications.any { it.id == taskId } }
		assertTrue("notification with id $taskId not posted within timeout", active)
	}

	@Test fun onReceive_isNoOp_whenTaskIdMissing() {
		val before = nm.activeNotifications.size
		ReminderReceiver().onReceive(context, Intent())
		// No new notification should appear.
		Thread.sleep(200)
		val after = nm.activeNotifications.size
		assertTrue("unexpected notification posted", after <= before)
	}

	private fun pollFor(timeoutMs: Long = 2_000, predicate: () -> Boolean): Boolean {
		val deadline = System.currentTimeMillis() + timeoutMs
		while (System.currentTimeMillis() < deadline) {
			if (predicate()) return true
			Thread.sleep(50)
		}
		return predicate()
	}
}
