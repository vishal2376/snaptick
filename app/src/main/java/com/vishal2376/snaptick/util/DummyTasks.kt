package com.vishal2376.snaptick.util

import com.vishal2376.snaptick.domain.model.Task
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

object DummyTasks {

	fun dummyTasks(count: Int = 5): List<Task> = buildList {
		for (i in 1..count) {
			add(
				Task(
					id = i,
					uuid = "$i",
					title = "Task $i",
					isCompleted = Random.nextBoolean(),
					startTime = LocalTime.now(),
					endTime = LocalTime.now().plusHours(Random.nextLong(1, 4)),
					reminder = Random.nextBoolean(),
					isRepeated = Random.nextBoolean(),
					repeatWeekdays = "",
					pomodoroTimer = -1,
					priority = Random.nextInt(3),
					date = LocalDate.now()
				)
			)
		}
	}
}
