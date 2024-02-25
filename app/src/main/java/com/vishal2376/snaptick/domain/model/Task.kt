package com.vishal2376.snaptick.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.vishal2376.snaptick.domain.converters.LocalDateConverter
import com.vishal2376.snaptick.domain.converters.LocalTimeConverter
import com.vishal2376.snaptick.domain.converters.WeekdayConverter
import com.vishal2376.snaptick.presentation.common.Weekday
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "task_table")
@TypeConverters(
	LocalTimeConverter::class,
	LocalDateConverter::class,
	WeekdayConverter::class
)
data class Task(
	@PrimaryKey(autoGenerate = true) val id: Int = 0,
	val uuid: String,
	val title: String = "",
	val isCompleted: Boolean = false,
	val startTime: LocalTime = LocalTime.now(),
	val endTime: LocalTime = LocalTime.now(),
	val reminder: Boolean = false,
	val isRepeat: Boolean = false,
	val repeatWeekdays: List<Weekday>,
	val pomodoroTimer: Long = 0L,
	val date: LocalDate = LocalDate.now(),
	val priority: Int = 0,
) {
	fun getFormattedTime(): String {
		val dtf = DateTimeFormatter.ofPattern("hh:mm a")
		val startTimeFormat = startTime.format(dtf)
		val endTimeFormat = endTime.format(dtf)
		return "$startTimeFormat - $endTimeFormat"
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

	fun getDurationTimeStamp(duration: Long): String {
		val hours = duration / 3600
		val minutes = (duration % 3600) / 60
		val seconds = duration % 60

		return if (hours == 0L) {
			String.format(
				"%02d:%02d",
				minutes,
				seconds
			)
		} else {
			String.format(
				"%02d:%02d:%02d",
				hours,
				minutes,
				seconds
			)
		}
	}

	fun getFormattedDuration(): String {
		val taskDuration = getDuration()

		val hours = taskDuration / 3600
		val minutes = (taskDuration % 3600) / 60

		if (hours > 0) {
			//show in hours
			if (minutes > 0) {
				val timeDuration = hours + (minutes / 60f)
				return String.format("%.1f hrs", timeDuration)
			} else {
				if (hours == 1L) return "1 hour"
				return "$hours hours"
			}
		} else {
			//show in minutes
			return "$minutes min"
		}

	}
}