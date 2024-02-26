package com.vishal2376.snaptick.di

import android.content.Context
import androidx.room.Room
import com.vishal2376.snaptick.data.local.RoomMigration
import com.vishal2376.snaptick.data.local.TaskDao
import com.vishal2376.snaptick.data.local.TaskDatabase
import com.vishal2376.snaptick.data.repositories.TaskRepository
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
			.addMigrations(RoomMigration)
			.build()
	}

	@Provides
	@Singleton
	fun providesTaskDao(db: TaskDatabase): TaskDao {
		return db.taskDao()
	}

	@Provides
	@Singleton
	fun providesTaskRepository(dao: TaskDao): TaskRepository {
		return TaskRepository(dao)
	}

}