package com.vishal2376.snaptick.domain.converters

import androidx.room.TypeConverter
import java.time.LocalTime

object LocalTimeConverter {
	@TypeConverter
	@JvmStatic
	fun fromString(value: String?): LocalTime? {
		return value?.let { LocalTime.parse(it) }
	}

	@TypeConverter
	@JvmStatic
	fun toString(localTime: LocalTime?): String? {
		return localTime?.toString()
	}
}