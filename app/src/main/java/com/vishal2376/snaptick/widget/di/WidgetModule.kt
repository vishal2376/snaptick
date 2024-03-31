package com.vishal2376.snaptick.widget.di

import android.content.Context
import com.vishal2376.snaptick.domain.interactor.AppWidgetInteractor
import com.vishal2376.snaptick.widget.interactor.AppWidgetInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WidgetModule {

	@Provides
	@Singleton
	fun providesGlanceInterceptor(
		@ApplicationContext context: Context
	): AppWidgetInteractor = AppWidgetInteractorImpl(context)
}