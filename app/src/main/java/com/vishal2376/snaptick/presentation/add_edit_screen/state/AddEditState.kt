package com.vishal2376.snaptick.presentation.add_edit_screen.state

import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.Priority
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AddEditState(
	val taskId: Int = 0,
	val uuid: String = UUID.randomUUID().toString(),
	val title: String = "",
	val startTime: LocalTime = LocalTime.now(),
	val endTime: LocalTime = LocalTime.now().plusHours(1),
	val date: LocalDate = LocalDate.now(),
	val reminder: Boolean = true,
	val isRepeated: Boolean = false,
	val isAllDay: Boolean = false,
	val repeatWeekdays: String = "",
	val priority: Priority = Priority.LOW,
	val duration: Long = 60,
	val pomodoroTimer: Int = -1,
	val isCompleted: Boolean = false,
	val timeUpdateTick: Int = 0
) {
	fun toTask(): Task = Task(
		id = taskId,
		uuid = uuid,
		title = title.trim(),
		isCompleted = isCompleted,
		startTime = startTime,
		endTime = if (isAllDay) startTime else endTime,
		reminder = reminder,
		isRepeated = isRepeated,
		repeatWeekdays = repeatWeekdays,
		pomodoroTimer = pomodoroTimer,
		date = date,
		priority = priority.ordinal
	)

	companion object {
		fun fromTask(task: Task): AddEditState = AddEditState(
			taskId = task.id,
			uuid = task.uuid,
			title = task.title,
			startTime = task.startTime,
			endTime = task.endTime,
			date = task.date,
			reminder = task.reminder,
			isRepeated = task.isRepeated,
			isAllDay = task.isAllDayTaskEnabled(),
			repeatWeekdays = task.repeatWeekdays,
			priority = Priority.entries.getOrElse(task.priority) { Priority.LOW },
			duration = task.getDuration() / 60,
			pomodoroTimer = task.pomodoroTimer,
			isCompleted = task.isCompleted
		)
	}
}
