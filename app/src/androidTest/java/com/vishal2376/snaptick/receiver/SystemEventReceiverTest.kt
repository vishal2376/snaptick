package com.vishal2376.snaptick.receiver

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Boot recovery contract: when [SystemEventReceiver] receives any of the
 * system events that can drop pending alarms (BOOT_COMPLETED, TIME_SET,
 * TIMEZONE_CHANGED, MY_PACKAGE_REPLACED, LOCKED_BOOT_COMPLETED), it must
 * enqueue the unique [com.vishal2376.snaptick.worker.RescheduleAllRemindersWorker]
 * job so reminders can be re-armed after the system state shifted.
 */
@RunWith(AndroidJUnit4::class)
class SystemEventReceiverTest {

	private lateinit var context: Context

	@Before fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		val cfg = Configuration.Builder()
			.setMinimumLoggingLevel(android.util.Log.DEBUG)
			.build()
		WorkManagerTestInitHelper.initializeTestWorkManager(context, cfg)
	}

	@Test fun bootCompleted_enqueuesRescheduleWorker() {
		dispatch(Intent.ACTION_BOOT_COMPLETED)
		assertWorkEnqueued()
	}

	@Test fun timeSet_enqueuesRescheduleWorker() {
		dispatch(Intent.ACTION_TIME_CHANGED)
		assertWorkEnqueued()
	}

	@Test fun timezoneChanged_enqueuesRescheduleWorker() {
		dispatch(Intent.ACTION_TIMEZONE_CHANGED)
		assertWorkEnqueued()
	}

	@Test fun packageReplaced_enqueuesRescheduleWorker() {
		dispatch(Intent.ACTION_MY_PACKAGE_REPLACED)
		assertWorkEnqueued()
	}

	@Test fun unrelatedAction_doesNotEnqueueWorker() {
		dispatch("android.intent.action.SOMETHING_ELSE_ENTIRELY")
		val infos = WorkManager.getInstance(context)
			.getWorkInfosForUniqueWork(SystemEventReceiver.UNIQUE_WORK_NAME)
			.get()
		assertTrue(
			"no reschedule work should be enqueued for unrelated actions",
			infos.isEmpty(),
		)
	}

	private fun dispatch(action: String) {
		SystemEventReceiver().onReceive(context, Intent(action))
	}

	private fun assertWorkEnqueued() {
		// The receiver's contract is to *enqueue* the unique work entry; whether
		// the worker subsequently completes is a separate concern (and depends
		// on Hilt-injected dependencies that we don't wire up here). So we
		// only assert "an entry exists" and not its terminal state - it could
		// be ENQUEUED, RUNNING, SUCCEEDED, or FAILED depending on test timing.
		val infos = WorkManager.getInstance(context)
			.getWorkInfosForUniqueWork(SystemEventReceiver.UNIQUE_WORK_NAME)
			.get()
		assertTrue("reschedule work not enqueued", infos.isNotEmpty())
	}
}
