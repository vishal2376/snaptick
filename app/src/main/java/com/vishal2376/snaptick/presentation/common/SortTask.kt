package com.vishal2376.snaptick.presentation.common

import com.vishal2376.snaptick.R

enum class SortTask(val stringId: Int) {
	BY_PRIORITY_ASCENDING(R.string.priority_low_to_high),
	BY_PRIORITY_DESCENDING(R.string.priority_high_to_low),
	BY_START_TIME_ASCENDING(R.string.start_time_latest_at_bottom),
	BY_START_TIME_DESCENDING(R.string.start_time_latest_at_top),
	BY_CREATE_TIME_ASCENDING(R.string.creation_time_latest_at_bottom),
	BY_CREATE_TIME_DESCENDING(R.string.creation_time_latest_at_top),
}