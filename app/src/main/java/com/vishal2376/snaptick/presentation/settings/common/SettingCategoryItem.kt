package com.vishal2376.snaptick.presentation.settings.common

import androidx.compose.ui.graphics.vector.ImageVector

data class SettingCategoryItem(
	val title: String,
	val icon: ImageVector,
	val onClick: () -> Unit = {}
)
