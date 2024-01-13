package com.vishal2376.snaptick.presentation.add_edit_screen

import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.util.Priority
import java.time.LocalTime

sealed class AddEditScreenEvent {
	data class OnAddTaskClick(val task: Task) : AddEditScreenEvent()
	data class OnDeleteTaskClick(val task: Task) : AddEditScreenEvent()
	data class OnUpdateTitle(val title: String) : AddEditScreenEvent()
	data class OnUpdateStartTime(val time: LocalTime) : AddEditScreenEvent()
	data class OnUpdateEndTime(val time: LocalTime) : AddEditScreenEvent()
	data class OnUpdateReminder(val reminder: Boolean) : AddEditScreenEvent()
	data class OnUpdatePriority(val priority: Priority) : AddEditScreenEvent()
	class OnUpdateTask() : AddEditScreenEvent()

}