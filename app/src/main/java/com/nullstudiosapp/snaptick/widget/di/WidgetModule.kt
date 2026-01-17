package com.nullstudiosapp.snaptick.widget.di

import android.content.Context
import com.nullstudiosapp.snaptick.domain.interactor.AppWidgetInteractor
import com.nullstudiosapp.snaptick.util.SettingsStore
import com.nullstudiosapp.snaptick.widget.interactor.AppWidgetInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides widget-related dependencies.
 * Replaces the old WidgetModule from widget_old package.
 */
@Module
@InstallIn(SingletonComponent::class)
object WidgetModule {

	@Provides
	@Singleton
	fun providesSettingsStore(
		@ApplicationContext context: Context
	): SettingsStore = SettingsStore(context)

	@Provides
	@Singleton
	fun providesAppWidgetInteractor(
		@ApplicationContext context: Context
	): AppWidgetInteractor = AppWidgetInteractorImpl(context)
}

