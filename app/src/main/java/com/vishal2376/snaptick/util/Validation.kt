package com.vishal2376.snaptick.util

import com.vishal2376.snaptick.domain.model.Task
import java.time.LocalTime

fun checkValidTask(task: Task, totalTasksDuration: Long = 0): Pair<Boolean, String> {
	val maxTime = LocalTime.MAX.toSecondOfDay()
	val currentTime = LocalTime.now().toSecondOfDay()
	val freeTime = maxTime - currentTime - totalTasksDuration
	val formattedFreeTime = getFreeTime(totalTasksDuration)

	val currentStartTime = task.startTime.toSecondOfDay()
	val currentEndTime = task.endTime.toSecondOfDay()
	val currentDuration = task.getDuration()

	if (task.title.isEmpty()) {
		return Pair(false, "Title can't be empty")
	}

	if (currentDuration >= freeTime) {
		return Pair(false, "Invalid Duration! You have only $formattedFreeTime remaining.")
	}

	if (currentStartTime <= currentTime) {
		return Pair(false, "Start time cannot be in the past.")
	}

	if (currentDuration < Constants.MIN_ALLOWED_DURATION * 60) {
		return Pair(false, "Task should be at least ${Constants.MIN_ALLOWED_DURATION} minutes.")
	}

	return Pair(true, "Valid Task")
}