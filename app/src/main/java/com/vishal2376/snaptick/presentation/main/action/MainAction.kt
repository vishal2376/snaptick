package com.vishal2376.snaptick.presentation.main.action

import android.net.Uri
import com.vishal2376.snaptick.domain.model.BackupData
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
	data class UpdateTotalTaskDuration(val durationSeconds: Long) : MainAction
	data class OnClickNavDrawerItem(val item: NavDrawerItem) : MainAction
	data class CreateBackup(val uri: Uri, val backupData: BackupData) : MainAction
	data class LoadBackup(val uri: Uri) : MainAction
	data class SetCalendarSyncEnabled(val enabled: Boolean) : MainAction
	data class SetCalendarSyncTarget(val calendarId: Long) : MainAction
	data class ImportTasks(val tasks: List<com.vishal2376.snaptick.domain.model.Task>) : MainAction
	data class ParseIcsFile(val uri: Uri) : MainAction
	data object ClearImportPreview : MainAction
	data object SyncAllTasksNow : MainAction
}
