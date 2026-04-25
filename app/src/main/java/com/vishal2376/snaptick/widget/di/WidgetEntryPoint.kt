package com.vishal2376.snaptick.widget.di

import com.vishal2376.snaptick.data.repositories.TaskRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt entry point for code paths that don't run inside an `@AndroidEntryPoint`
 * scope - notably Glance `ActionCallback` instances. Resolved at call time
 * via `EntryPointAccessors.fromApplication(context)`.
 *
 * The widget needs full `TaskRepository` access so completion toggles from
 * the widget go through the same path as the home screen: per-date completion
 * for repeat templates, alarm cancel/reschedule for one-offs.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
	fun taskRepository(): TaskRepository
}
