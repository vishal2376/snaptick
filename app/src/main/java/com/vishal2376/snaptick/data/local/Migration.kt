package com.vishal2376.snaptick.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v1 → v2 migration.
 *
 * v1 (legacy) shipped the column as `isRepeat` and already contained
 * `repeatWeekdays` and `pomodoroTimer` columns. v2 renames the flag to
 * `isRepeated`. `ALTER TABLE RENAME COLUMN` is only available on SQLite
 * 3.25+ (Android 11 / API 30+), and this app's `minSdk` is 26, so we use the
 * classic "create-new-table, copy rows, drop, rename" pattern which is safe
 * on every supported runtime.
 *
 * If this migration is ever edited, update `app/schemas/` and
 * `data/local/MigrationTest.kt` in the same commit.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
	override fun migrate(db: SupportSQLiteDatabase) {
		db.execSQL(
			"""
			CREATE TABLE IF NOT EXISTS `task_table_new` (
				`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
				`uuid` TEXT NOT NULL,
				`title` TEXT NOT NULL,
				`isCompleted` INTEGER NOT NULL,
				`startTime` TEXT NOT NULL,
				`endTime` TEXT NOT NULL,
				`reminder` INTEGER NOT NULL,
				`isRepeated` INTEGER NOT NULL,
				`repeatWeekdays` TEXT NOT NULL,
				`pomodoroTimer` INTEGER NOT NULL,
				`date` TEXT NOT NULL,
				`priority` INTEGER NOT NULL
			)
			""".trimIndent()
		)

		db.execSQL(
			"""
			INSERT INTO `task_table_new`
				(id, uuid, title, isCompleted, startTime, endTime, reminder, isRepeated, repeatWeekdays, pomodoroTimer, date, priority)
			SELECT
				id, uuid, title, isCompleted, startTime, endTime, reminder, isRepeat, repeatWeekdays, pomodoroTimer, date, priority
			FROM `task_table`
			""".trimIndent()
		)

		db.execSQL("DROP TABLE `task_table`")
		db.execSQL("ALTER TABLE `task_table_new` RENAME TO `task_table`")
	}
}

/**
 * v2 → v3 migration. Adds a nullable `calendarEventId` column that links a task
 * to an event row in the device's CalendarContract provider. Pre-existing rows
 * get `NULL`, meaning "not mirrored yet"; the CalendarPusher will populate them
 * on the next push.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
	override fun migrate(db: SupportSQLiteDatabase) {
		db.execSQL("ALTER TABLE `task_table` ADD COLUMN `calendarEventId` INTEGER DEFAULT NULL")
	}
}

/**
 * v3 → v4 migration. Adds the `task_completions` table that backs per-occurrence
 * completion for repeating tasks. The `Task.isCompleted` column stays as-is and
 * keeps owning one-off task completion. Existing rows are untouched.
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
	override fun migrate(db: SupportSQLiteDatabase) {
		db.execSQL(
			"""
			CREATE TABLE IF NOT EXISTS `task_completions` (
				`uuid` TEXT NOT NULL,
				`date` TEXT NOT NULL,
				PRIMARY KEY(`uuid`, `date`)
			)
			""".trimIndent()
		)
	}
}
