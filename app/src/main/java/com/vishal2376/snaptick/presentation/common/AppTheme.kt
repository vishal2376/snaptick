package com.vishal2376.snaptick.presentation.common

enum class AppTheme {
	Light, Dark, Amoled;

	companion object {
		fun fromIndex(index: Int): AppTheme {
			return when (index) {
				0 -> Light
				1 -> Dark
				2 -> Amoled
				else -> throw IllegalArgumentException("Invalid theme index: $index")
			}
		}
	}

}
