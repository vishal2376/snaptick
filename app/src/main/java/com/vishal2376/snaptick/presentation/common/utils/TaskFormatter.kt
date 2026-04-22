package com.vishal2376.snaptick.presentation.common.utils

import com.vishal2376.snaptick.domain.model.Task
import java.time.LocalTime

private val weekdayShortNames = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

fun formatTaskTime(task: Task, is24HourFormat: Boolean = false): String {
	val dtf = if (is24HourFormat) Formatters.time24h else Formatters.time12h
	return "${task.startTime.format(dtf)} - ${task.endTime.format(dtf)}"
}

fun formatDuration(task: Task): String = formatDuration(task.getDuration())

fun formatDuration(startTime: LocalTime, endTime: LocalTime): String {
	val seconds = (endTime.toSecondOfDay() - startTime.toSecondOfDay()).coerceAtLeast(0).toLong()
	return formatDuration(seconds)
}

fun formatDuration(durationSeconds: Long): String {
	val hours = (durationSeconds / 3600).toInt()
	val minutes = ((durationSeconds % 3600) / 60).toInt()
	val hoursLabel = if (hours == 1) "hour" else "hours"

	return when {
		hours > 0 && minutes > 0 -> String.format("%dh %02dm", hours, minutes)
		hours > 0 -> String.format("%d $hoursLabel", hours)
		else -> String.format("%d min", minutes)
	}
}

fun formatDurationTimestamp(durationSeconds: Long): String {
	val hours = durationSeconds / 3600
	val minutes = (durationSeconds % 3600) / 60
	val seconds = durationSeconds % 60

	return if (hours == 0L) {
		String.format("%02d:%02d", minutes, seconds)
	} else {
		String.format("%02d:%02d:%02d", hours, minutes, seconds)
	}
}

fun formatWeekDays(weekDayIndices: List<Int>): String =
	weekDayIndices.joinToString(", ") { weekdayShortNames[it] }
