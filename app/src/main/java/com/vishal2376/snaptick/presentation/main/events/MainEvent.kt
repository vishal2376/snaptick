package com.vishal2376.snaptick.presentation.main.events

sealed interface MainEvent {
	data class ShowToast(val message: String) : MainEvent
	data class OpenMail(val subject: String) : MainEvent
}
