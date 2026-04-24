package com.vishal2376.snaptick.util

import com.google.gson.GsonBuilder
import com.vishal2376.snaptick.domain.model.BackupData
import com.vishal2376.snaptick.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class BackupGsonTest {

	private val gson = GsonBuilder()
		.registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
		.registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
		.create()

	@Test fun backupData_roundTrip_preservesTasks() {
		val original = BackupData(
			tasks = listOf(
				Task(
					id = 1, uuid = "u1", title = "Run",
					isCompleted = false,
					startTime = LocalTime.of(6, 30),
					endTime = LocalTime.of(7, 15),
					reminder = true,
					isRepeated = true,
					repeatWeekdays = "0,2,4",
					pomodoroTimer = 900,
					date = LocalDate.of(2026, 4, 23),
					priority = 2
				),
				Task(
					id = 2, uuid = "u2", title = "Groceries",
					startTime = LocalTime.of(18, 0),
					endTime = LocalTime.of(18, 0),
					date = LocalDate.of(2026, 4, 24)
				)
			)
		)
		val json = gson.toJson(original)
		val decoded = gson.fromJson(json, BackupData::class.java)
		assertEquals(original, decoded)
	}

	@Test fun empty_backup_roundTrips() {
		val empty = BackupData(emptyList())
		val decoded = gson.fromJson(gson.toJson(empty), BackupData::class.java)
		assertEquals(empty, decoded)
	}

	@Test fun localDate_adapter_usesIsoFormat() {
		val d = LocalDate.of(2026, 1, 5)
		val json = gson.toJson(d)
		assertEquals("\"2026-01-05\"", json)
	}

	@Test fun localTime_adapter_usesIsoFormat() {
		val t = LocalTime.of(9, 30)
		val json = gson.toJson(t)
		assertEquals("\"09:30:00\"", json)
	}
}
