package com.vishal2376.snaptick.domain.converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateConverter {

	@TypeConverter
	@JvmStatic
	fun fromString(value: String?): LocalDate? {
		return value?.let { LocalDate.parse(it) }
	}

	@TypeConverter
	@JvmStatic
	fun toString(value: LocalDate?): String? {
		val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
		return value?.format(formatter)
	}
}
