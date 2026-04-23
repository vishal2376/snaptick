package com.vishal2376.snaptick.presentation.add_edit_screen.action

import com.vishal2376.snaptick.presentation.common.Priority
import java.time.LocalDate
import java.time.LocalTime

sealed interface AddEditAction {
	data class UpdateTitle(val title: String) : AddEditAction
	data class UpdateStartTime(val time: LocalTime) : AddEditAction
	data class UpdateEndTime(val time: LocalTime) : AddEditAction
	data class UpdateDate(val date: LocalDate) : AddEditAction
	data class UpdateReminder(val enabled: Boolean) : AddEditAction
	data class UpdateAllDay(val enabled: Boolean) : AddEditAction
	data class UpdateRepeated(val enabled: Boolean) : AddEditAction
	data class UpdateRepeatWeekDays(val weekDays: String) : AddEditAction
	data class UpdatePriority(val priority: Priority) : AddEditAction
	data class UpdateDurationMinutes(val minutes: Long) : AddEditAction
	data object SaveTask : AddEditAction
	data object UpdateTask : AddEditAction
	data object DeleteTask : AddEditAction
	data object ResetPomodoroTimer : AddEditAction
}
