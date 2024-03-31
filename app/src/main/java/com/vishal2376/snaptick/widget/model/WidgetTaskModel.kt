package com.vishal2376.snaptick.widget.model

import com.vishal2376.snaptick.domain.model.Task
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * A secondary [Task] model, namely [WidgetTaskModel] as widget doesn't require all the
 * information, this [WidgetTaskModel] contains only the fields that are required by the widget.
 */
data class WidgetTaskModel(
	val id: Int,
	val title: String = "",
	val isCompleted: Boolean = false,
	val startTime: LocalTime = LocalTime.now(),
	val endTime: LocalTime = LocalTime.now(),
	val priority: Int = 0,
) {
	private val timerFormat: DateTimeFormatter
		get() = DateTimeFormatter.ofPattern("hh:mm a")

	val formattedTime: String
		get() {
			val startTimeFormat = timerFormat.format(startTime)
			val endTimeFormat = timerFormat.format(endTime)
			return "$startTimeFormat - $endTimeFormat"
		}
}
