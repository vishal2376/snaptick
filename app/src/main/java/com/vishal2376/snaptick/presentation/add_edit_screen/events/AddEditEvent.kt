package com.vishal2376.snaptick.presentation.add_edit_screen.events

sealed interface AddEditEvent {
	data object TaskSaved : AddEditEvent
	data object TaskUpdated : AddEditEvent
	data object TaskDeleted : AddEditEvent
}
