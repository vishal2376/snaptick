package com.nullstudiosapp.snaptick.presentation.settings.common

data class SettingCategoryItem(
	val title: String,
	val resId: Int,
	val onClick: () -> Unit = {}
)
