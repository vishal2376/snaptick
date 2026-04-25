package com.vishal2376.snaptick.di

import android.app.AlarmManager
import android.content.Context
import androidx.room.Room
import com.vishal2376.snaptick.data.calendar.CalendarPusher
import com.vishal2376.snaptick.data.local.MIGRATION_1_2
import com.vishal2376.snaptick.data.local.MIGRATION_2_3
import com.vishal2376.snaptick.data.local.MIGRATION_3_4
import com.vishal2376.snaptick.data.local.TaskCompletionDao
import com.vishal2376.snaptick.data.local.TaskDao
import com.vishal2376.snaptick.data.local.TaskDatabase
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.util.ReminderScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

	@Provides
	@Singleton
	fun providesLocalDatabase(@ApplicationContext context: Context): TaskDatabase {
		return Room.databaseBuilder(context, TaskDatabase::class.java, "local_db")
			.fallbackToDestructiveMigration()
			.addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
			.build()
	}

	@Provides
	@Singleton
	fun providesTaskDao(db: TaskDatabase): TaskDao {
		return db.taskDao()
	}

	@Provides
	@Singleton
	fun providesTaskCompletionDao(db: TaskDatabase): TaskCompletionDao {
		return db.taskCompletionDao()
	}

	@Provides
	@Singleton
	fun providesAlarmManager(@ApplicationContext context: Context): AlarmManager {
		return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
	}

	@Provides
	@Singleton
	fun providesTaskRepository(
		dao: TaskDao,
		completionDao: TaskCompletionDao,
		@ApplicationContext context: Context,
		calendarPusher: CalendarPusher,
		reminderScheduler: ReminderScheduler,
	): TaskRepository {
		return TaskRepository(dao, completionDao, context, calendarPusher, reminderScheduler)
	}

}
