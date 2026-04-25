package com.vishal2376.snaptick.presentation.main.state

import com.vishal2376.snaptick.domain.model.BackupData
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.CalenderView
import com.vishal2376.snaptick.presentation.common.SortTask
import com.vishal2376.snaptick.presentation.common.SwipeBehavior
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

data class MainState(
	val buildVersion: String = "0.0",
	val buildVersionCode: Int = 1,
	val firstTimeOpened: Boolean = true,
	val showWhatsNew: Boolean = false,
	val theme: AppTheme = AppTheme.Amoled,
	val dynamicTheme: Boolean = false,
	val sortBy: SortTask = SortTask.BY_START_TIME_ASCENDING,
	val totalTaskDuration: Long = 0,
	val durationList: List<Long> = listOf(30, 60, 90, 0),
	val streak: Int = -1,
	val sleepTime: LocalTime = LocalTime.of(23, 59),
	val language: String = Locale.ENGLISH.language,
	val isWheelTimePicker: Boolean = true,
	val is24hourTimeFormat: Boolean = false,
	val calenderView: CalenderView = CalenderView.MONTHLY,
	val calenderDate: LocalDate? = null,
	val swipeBehaviour: SwipeBehavior = SwipeBehavior.DELETE,
	val calendarSyncEnabled: Boolean = false,
	val calendarSyncCalendarId: Long? = null,
	val importPreview: List<Task> = emptyList(),
	val onboardingCompleted: Boolean = true,
	val bootResolved: Boolean = false,
	/**
	 * Set when the user picks a backup .json. UI renders a confirmation dialog
	 * with the parsed task count. Cleared by ConfirmRestore (after wipe+insert)
	 * or CancelRestore.
	 */
	val pendingRestore: PendingRestore? = null,
)

data class PendingRestore(
	val data: BackupData,
	val taskCount: Int,
	val droppedCount: Int,
)
