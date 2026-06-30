package com.vishal2376.snaptick.presentation.main.action

import android.net.Uri
import com.vishal2376.snaptick.domain.model.BackupData
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.CalenderView
import com.vishal2376.snaptick.presentation.common.NavDrawerItem
import com.vishal2376.snaptick.presentation.common.SortTask
import com.vishal2376.snaptick.presentation.common.SwipeBehavior
import java.time.LocalDate
import java.time.LocalTime

sealed interface MainAction {
	data class UpdateAppTheme(val theme: AppTheme) : MainAction
	data class UpdateDynamicTheme(val isEnabled: Boolean) : MainAction
	data class UpdateTimePicker(val isWheelTimePicker: Boolean) : MainAction
	data class UpdateTimeFormat(val is24Hour: Boolean) : MainAction
	data class UpdateSleepTime(val sleepTime: LocalTime) : MainAction
	data class UpdateLanguage(val language: String) : MainAction
	data class UpdateSortByTask(val sortTask: SortTask) : MainAction
	data class UpdateCalenderView(val calenderView: CalenderView) : MainAction
	data class UpdateCalenderDate(val date: LocalDate?) : MainAction
	data class UpdateShowWhatsNew(val show: Boolean) : MainAction
	data class UpdateFirstTimeOpened(val isFirstTimeOpened: Boolean) : MainAction
	data class UpdateBuildVersionCode(val versionCode: Int) : MainAction
	data class UpdateSwipeBehaviour(val swipeBehaviour: SwipeBehavior) : MainAction
	data class OnClickNavDrawerItem(val item: NavDrawerItem) : MainAction
	data class CreateBackup(val uri: Uri, val backupData: BackupData) : MainAction

	// Stage 1: parse + validate, stage on MainState.pendingRestore. No db writes.
	data class PreviewBackup(val uri: Uri) : MainAction

	// Stage 2: user confirmed; wipe + insert.
	data object ConfirmRestore : MainAction

	// Stage 2 alt: user dismissed; drop the pending preview.
	data object CancelRestore : MainAction

	@Deprecated(
		"Restore is now two-stage. Dispatch PreviewBackup followed by ConfirmRestore.",
		ReplaceWith("MainAction.PreviewBackup(uri)")
	)
	data class LoadBackup(val uri: Uri) : MainAction
	data class SetCalendarSyncEnabled(val enabled: Boolean) : MainAction
	data class SetCalendarSyncTarget(val calendarId: Long) : MainAction
	data class ImportTasks(val tasks: List<Task>) : MainAction
	data class ParseIcsFile(val uri: Uri) : MainAction
	data class ImportIcsFile(val uri: Uri) : MainAction
	data object ClearImportPreview : MainAction
	data object SyncAllTasksNow : MainAction
	data object CompleteOnboarding : MainAction
	data class UpdateSoundEnabled(val enabled: Boolean) : MainAction
	data class UpdateDefaultPomodoroDuration(val mins: Int) : MainAction
	data class CheckForUpdates(val ignoreThrottle: Boolean = false) : MainAction
	data object DismissUpdateBanner : MainAction
	data object DismissUpdateStatus : MainAction
	data object RefreshWritableCalendars : MainAction
}
