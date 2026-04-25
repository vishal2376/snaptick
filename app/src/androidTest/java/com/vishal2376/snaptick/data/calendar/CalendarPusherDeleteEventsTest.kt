package com.vishal2376.snaptick.data.calendar

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.vishal2376.snaptick.data.local.TaskDatabase
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.util.SettingsStore
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime
import java.util.TimeZone

/**
 * Instrumented coverage for `CalendarPusher.deleteAllPushedEvents`. Used at
 * the moment the user disables calendar sync. Verifies that:
 *  1. Events Snaptick previously pushed are removed from CalendarContract.
 *  2. The corresponding `Task.calendarEventId` columns are cleared.
 *  3. Tasks that were never pushed (calendarEventId == null) are not touched.
 *
 * The test seeds a real event into the device's local CalendarContract via
 * the writable calendar discovered at setup. If the runtime has no writable
 * calendar (rare on emulators without a configured account), the test is
 * skipped via JUnit's Assume mechanism rather than failed - we don't want
 * CI to go red because a calendar provider isn't seeded.
 */
@RunWith(AndroidJUnit4::class)
class CalendarPusherDeleteEventsTest {

	@get:Rule val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
		Manifest.permission.READ_CALENDAR,
		Manifest.permission.WRITE_CALENDAR,
	)

	private lateinit var context: Context
	private lateinit var calendarRepo: CalendarRepository
	private lateinit var settings: SettingsStore
	private lateinit var db: TaskDatabase
	private lateinit var pusher: CalendarPusher
	private var seededEventId: Long? = null
	private var targetCalendarId: Long? = null

	@Before fun setUp() {
		context = ApplicationProvider.getApplicationContext()
		calendarRepo = CalendarRepository(context)
		settings = SettingsStore(context)
		db = Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java)
			.allowMainThreadQueries()
			.build()
		pusher = CalendarPusher(calendarRepo, db.taskDao(), settings)
	}

	@After fun tearDown() {
		// Best-effort cleanup if the test bailed mid-flight.
		seededEventId?.let { id ->
			runCatching {
				val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id)
				context.contentResolver.delete(uri, null, null)
			}
		}
		db.close()
	}

	@Test fun deleteAllPushedEvents_removesEvent_andClearsLink() = runBlocking {
		val writable = calendarRepo.getWritableCalendars()
		assumeTrue("no writable calendar present on this emulator", writable.isNotEmpty())
		val calendarId = writable.first().id
		targetCalendarId = calendarId

		// Seed an event directly via the resolver so we don't depend on
		// CalendarPusher.pushInsert under test.
		val now = LocalDate.now().atTime(LocalTime.of(10, 0))
		val startMs = now.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
		val values = ContentValues().apply {
			put(CalendarContract.Events.CALENDAR_ID, calendarId)
			put(CalendarContract.Events.TITLE, "Snaptick test event")
			put(CalendarContract.Events.DTSTART, startMs)
			put(CalendarContract.Events.DTEND, startMs + 60 * 60 * 1000)
			put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
			put(CalendarContract.Events.EVENT_END_TIMEZONE, TimeZone.getDefault().id)
			put(CalendarContract.Events.ALL_DAY, 0)
		}
		val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
		assertNotNull("failed to seed test event", uri)
		val eventId = uri!!.lastPathSegment!!.toLong()
		seededEventId = eventId

		// Two tasks: one with a stale link to the seeded event, one without.
		val linked = Task(
			id = 0, uuid = "linked", title = "linked",
			startTime = LocalTime.of(10, 0), endTime = LocalTime.of(11, 0),
			date = LocalDate.now(),
			calendarEventId = eventId,
		)
		val unlinked = Task(
			id = 0, uuid = "unlinked", title = "unlinked",
			startTime = LocalTime.of(12, 0), endTime = LocalTime.of(13, 0),
			date = LocalDate.now(),
			calendarEventId = null,
		)
		db.taskDao().insertTask(linked)
		db.taskDao().insertTask(unlinked)

		val all = db.taskDao().getAllTasksSnapshot()
		val deleted = pusher.deleteAllPushedEvents(all)

		assertTrue("expected at least one event deletion, got $deleted", deleted >= 1)

		// Calendar provider no longer returns the row.
		val cursor = context.contentResolver.query(
			CalendarContract.Events.CONTENT_URI,
			arrayOf(CalendarContract.Events._ID),
			"${CalendarContract.Events._ID} = ? AND ${CalendarContract.Events.DELETED} = 0",
			arrayOf(eventId.toString()),
			null,
		)
		val rows = cursor?.use { it.count } ?: 0
		assertTrue("event still present after deleteAllPushedEvents (rows=$rows)", rows == 0)
		seededEventId = null  // already cleaned up

		// Linked task's calendarEventId column must be cleared, unlinked stays null.
		val linkedAfter = db.taskDao().getTaskByUuid("linked")
		assertNotNull(linkedAfter)
		assertTrue("linked task's calendarEventId should be cleared", linkedAfter!!.calendarEventId == null)
		val unlinkedAfter = db.taskDao().getTaskByUuid("unlinked")
		assertTrue("unlinked task should stay unlinked", unlinkedAfter?.calendarEventId == null)
	}
}
