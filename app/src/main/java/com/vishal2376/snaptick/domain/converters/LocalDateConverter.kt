package com.vishal2376.snaptick.domain.converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateConverter {
	private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

	@TypeConverter
	@JvmStatic
	fun fromLocalDate(value: LocalDate?): String? {
		return value?.format(formatter)
	}

	@TypeConverter
	@JvmStatic
	fun toLocalDate(value: String?): LocalDate? {
		return value?.let { LocalDate.parse(it, formatter) }
	}
}
