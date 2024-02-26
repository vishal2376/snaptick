package com.vishal2376.snaptick.domain.converters

import androidx.room.TypeConverter
import com.vishal2376.snaptick.presentation.common.Weekday

object WeekdayConverter {
	@TypeConverter
	fun fromWeekdays(weekdays: List<Weekday>): String {
		return weekdays.joinToString(",")
	}

	@TypeConverter
	fun toWeekdays(weekdaysString: String): List<Weekday> {
		val weekdays = weekdaysString.split(",").map { it.trim() }
		return weekdays.map { Weekday.valueOf(it) }
	}
}
