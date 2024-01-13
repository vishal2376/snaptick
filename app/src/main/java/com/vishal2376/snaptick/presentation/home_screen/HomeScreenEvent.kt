package com.vishal2376.snaptick.presentation.home_screen

sealed class HomeScreenEvent {
	data class onCompleted(val id: Int) : HomeScreenEvent()
}