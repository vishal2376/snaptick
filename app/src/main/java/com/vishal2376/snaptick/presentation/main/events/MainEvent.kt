package com.vishal2376.snaptick.presentation.main.events

sealed interface MainEvent {
	data class ShowToast(val message: String) : MainEvent
	data class OpenMail(val subject: String) : MainEvent
	data class CalendarSyncComplete(val count: Int) : MainEvent
	data class ImportComplete(val count: Int) : MainEvent
	data class IcsParsedReady(val count: Int) : MainEvent
	data class ImportFailed(val message: String) : MainEvent
	data object CalendarPermissionRequired : MainEvent
}
