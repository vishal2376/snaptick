package com.vishal2376.snaptick.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.vishal2376.snaptick.domain.converters.LocalDateConverter
import com.vishal2376.snaptick.domain.converters.LocalTimeConverter
import com.vishal2376.snaptick.util.Constants
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "task_table")
@TypeConverters(
	LocalTimeConverter::class,
	LocalDateConverter::class
)
data class Task(
	@PrimaryKey(autoGenerate = true) val id: Int = 0,
	val uuid: String,
	val title: String = "",
	val isCompleted: Boolean = false,
	val startTime: LocalTime = LocalTime.now(),
	val endTime: LocalTime = LocalTime.now(),
	val reminder: Boolean = false,
	val isRepeated: Boolean = false,
	val repeatWeekdays: String = "",
	val pomodoroTimer: Int = -1,
	val date: LocalDate = LocalDate.now(),
	val priority: Int = 0,
	val calendarEventId: Long? = null,
) {
	fun isAllDayTaskEnabled(): Boolean {
		return startTime == endTime
	}

	fun getRepeatWeekList(): List<Int> {
		return if (repeatWeekdays.isEmpty())
			emptyList()
		else
			repeatWeekdays.split(",")
				.map { it.toInt() }
	}

	fun isValidPomodoroSession(timeLeft: Long): Boolean {
		return (getDuration() - timeLeft) >= Constants.MIN_VALID_POMODORO_SESSION * 60
	}

	fun getDuration(checkPastTask: Boolean = false): Long {
		val startTimeSec = startTime.toSecondOfDay()
		val endTimeSec = endTime.toSecondOfDay()
		val currentTimeSec = LocalTime.now().toSecondOfDay()

		return when {
			checkPastTask -> {
				when {
					currentTimeSec > endTimeSec -> 0
					currentTimeSec in (startTimeSec + 1)..<endTimeSec -> (endTimeSec - currentTimeSec).toLong()
					else -> (endTimeSec - startTimeSec).coerceAtLeast(0).toLong()
				}
			}

			else -> (endTimeSec - startTimeSec).coerceAtLeast(0).toLong()
		}
	}
}
