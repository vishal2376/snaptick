package com.vishal2376.snaptick.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vishal2376.snaptick.domain.model.Task

@Database(
	entities = [Task::class, TaskCompletion::class],
	version = 4,
)
abstract class TaskDatabase : RoomDatabase() {
	abstract fun taskDao(): TaskDao
	abstract fun taskCompletionDao(): TaskCompletionDao

	companion object {
		@Volatile
		private var INSTANCE: TaskDatabase? = null

		/**
		 * Get singleton instance of TaskDatabase.
		 * Used by widget actions that need database access outside of DI context.
		 */
		fun getInstance(context: Context): TaskDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					TaskDatabase::class.java,
					"local_db"
				)
					.fallbackToDestructiveMigration()
					.addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
					.build()
				INSTANCE = instance
				instance
			}
		}
	}
}