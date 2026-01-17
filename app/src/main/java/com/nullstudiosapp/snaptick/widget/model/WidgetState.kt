package com.nullstudiosapp.snaptick.widget.model

import com.nullstudiosapp.snaptick.domain.model.Task
import com.nullstudiosapp.snaptick.presentation.common.AppTheme

/**
 * Represents the complete state for the Snaptick widget.
 * This state is persisted via DataStore and updated by WidgetUpdateWorker.
 */
data class WidgetState(
	val tasks: List<Task> = emptyList(),
	val is24HourFormat: Boolean = false,
	val theme: AppTheme = AppTheme.Amoled,
	val useDynamicTheme: Boolean = false
)
