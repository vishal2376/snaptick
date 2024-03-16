package com.vishal2376.snaptick.presentation.add_edit_screen

import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.Priority
import java.time.LocalTime

sealed class AddEditScreenEvent {
	data class OnAddTaskClick(val task: Task) : AddEditScreenEvent()
	data class OnDeleteTaskClick(val task: Task) : AddEditScreenEvent()
	data class OnUpdateTitle(val title: String) : AddEditScreenEvent()
	data class OnUpdateStartTime(val time: LocalTime) : AddEditScreenEvent()
	data class OnUpdateEndTime(val time: LocalTime) : AddEditScreenEvent()
	data class OnUpdateReminder(val reminder: Boolean) : AddEditScreenEvent()
	data object ResetPomodoroTimer : AddEditScreenEvent()
	data class OnUpdateIsRepeated(val isRepeated: Boolean) : AddEditScreenEvent()
	data class OnUpdateRepeatWeekDays(val weekDays: String) : AddEditScreenEvent()
	data class OnUpdatePriority(val priority: Priority) : AddEditScreenEvent()
	data object OnUpdateTask : AddEditScreenEvent()

}