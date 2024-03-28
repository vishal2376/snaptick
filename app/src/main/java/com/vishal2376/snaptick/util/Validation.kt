package com.vishal2376.snaptick.util

import com.vishal2376.snaptick.domain.model.Task
import java.time.LocalDate
import java.time.LocalTime

fun checkValidTask(
	task: Task,
	totalTasksDuration: Long = 0,
	sleepTime: LocalTime = LocalTime.MAX
): Pair<Boolean, String> {
	val maxTime = sleepTime.toSecondOfDay()
	val currentTime = LocalTime.now().toSecondOfDay()
	val freeTime = maxTime - currentTime - totalTasksDuration
	val formattedFreeTime = getFreeTime(totalTasksDuration, sleepTime)

	val currentDuration = task.getDuration(checkPastTask = true)
	val startTimeSec = task.startTime.toSecondOfDay()

	if (task.title.trim().isEmpty()) {
		return Pair(false, "Title can't be empty")
	}

	if (currentDuration >= freeTime) {
		return Pair(false, "Invalid Duration! You have only $formattedFreeTime remaining.")
	}

	if (task.getDuration() < Constants.MIN_ALLOWED_DURATION * 60) {
		return Pair(false, "Task should be at least ${Constants.MIN_ALLOWED_DURATION} minutes.")
	}

	if (task.date < LocalDate.now()) {
		return Pair(false, "Past dates are not allowed")
	}

//	if (task.reminder) {
//		if (startTimeSec < currentTime && !task.isRepeated) {
//			return Pair(false, "Cannot set a reminder for past time")
//		}
//	}
	return Pair(true, "Valid Task")
}