package com.nullstudiosapp.snaptick.di

import android.content.Context
import androidx.room.Room
import com.nullstudiosapp.snaptick.data.local.MIGRATION_1_2
import com.nullstudiosapp.snaptick.data.local.TaskDao
import com.nullstudiosapp.snaptick.data.local.TaskDatabase
import com.nullstudiosapp.snaptick.data.repositories.TaskRepository
import com.nullstudiosapp.snaptick.domain.interactor.AppWidgetInteractor
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
			.addMigrations(MIGRATION_1_2)
			.build()
	}

	@Provides
	@Singleton
	fun providesTaskDao(db: TaskDatabase): TaskDao {
		return db.taskDao()
	}

	@Provides
	@Singleton
	fun providesTaskRepository(
		dao: TaskDao,
		widgetInteract: AppWidgetInteractor
	): TaskRepository {
		return TaskRepository(dao, widgetInteract)
	}

}