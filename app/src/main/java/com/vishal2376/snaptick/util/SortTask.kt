package com.vishal2376.snaptick.util

enum class SortTask(val displayText: String) {
	BY_TITLE_ASCENDING("Title (A-Z)"),
	BY_TITLE_DESCENDING("Title (Z-A)"),
	BY_CREATE_TIME_ASCENDING("Task Create Time (Latest at Bottom)"),
	BY_CREATE_TIME_DESCENDING("Task Create Time (Latest at Top)"),
	BY_PRIORITY_ASCENDING("Priority (Low to High)"),
	BY_PRIORITY_DESCENDING("Priority (High to Low)")
}