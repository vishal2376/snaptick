package com.vishal2376.snaptick.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
	override fun migrate(db: SupportSQLiteDatabase) {
		// Add new columns
		db.execSQL("ALTER TABLE task_table ADD COLUMN repeatWeekdays TEXT NOT NULL DEFAULT ''")
		db.execSQL("ALTER TABLE task_table ADD COLUMN pomodoroTimer INTEGER NOT NULL DEFAULT 0")

		// Convert existing data for the new columns
		db.execSQL("UPDATE task_table SET repeatWeekdays = ''")
		db.execSQL("UPDATE task_table SET pomodoroTimer = 0")
	}
}