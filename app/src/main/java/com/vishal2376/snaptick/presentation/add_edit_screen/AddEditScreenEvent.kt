package com.vishal2376.snaptick.presentation.add_edit_screen

import com.vishal2376.snaptick.domain.model.Task

sealed class AddEditScreenEvent {
	data class onAddTaskClick(val task: Task) : AddEditScreenEvent()
}