package com.vishal2376.snaptick.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object RoomMigration : Migration(1, 2) {
	override fun migrate(db: SupportSQLiteDatabase) {
		// Add new columns to the 'tasks' table
		db.execSQL("ALTER TABLE task_table ADD COLUMN repeatWeekdays TEXT NOT NULL DEFAULT ''")
		db.execSQL("ALTER TABLE task_table ADD COLUMN pomodoroTimer INTEGER NOT NULL DEFAULT 0")

		// Convert existing data for the new columns
		db.execSQL("UPDATE task_table SET repeatWeekdays = ''")
		db.execSQL("UPDATE task_table SET pomodoroTimer = 0")
	}
}