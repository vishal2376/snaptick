package com.vishal2376.snaptick.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vishal2376.snaptick.domain.model.Task

@Database(
	entities = [Task::class],
	version = 2,
)
abstract class TaskDatabase : RoomDatabase() {
	abstract fun taskDao(): TaskDao
}