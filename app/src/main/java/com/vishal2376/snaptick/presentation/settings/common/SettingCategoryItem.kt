package com.vishal2376.snaptick.presentation.settings.common

data class SettingCategoryItem(
	val title: String,
	val resId: Int,
	val onClick: () -> Unit = {}
)
