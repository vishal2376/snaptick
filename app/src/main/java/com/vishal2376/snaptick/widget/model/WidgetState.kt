package com.vishal2376.snaptick.widget.model

import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.AppTheme

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
