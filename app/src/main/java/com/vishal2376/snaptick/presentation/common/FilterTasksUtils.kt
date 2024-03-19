package com.vishal2376.snaptick.presentation.common

import com.vishal2376.snaptick.domain.model.Task
import java.time.LocalDate
import java.time.YearMonth

fun getTasksByMonth(tasks: List<Task>, month: YearMonth = YearMonth.now()): List<Task> {
	return tasks.filter { task ->
		YearMonth.from(task.date) == month
	}
}

fun getTasksByDate(tasks: List<Task>, date: LocalDate = LocalDate.now()): List<Task> {
	return tasks.filter { task ->
		task.date == date
	}
}
