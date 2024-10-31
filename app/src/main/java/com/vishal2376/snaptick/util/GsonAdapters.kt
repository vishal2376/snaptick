package com.vishal2376.snaptick.util

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LocalDateAdapter : TypeAdapter<LocalDate>() {
	private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

	override fun write(out: JsonWriter, value: LocalDate) {
		out.value(value.format(formatter))
	}

	override fun read(input: JsonReader): LocalDate {
		return LocalDate.parse(input.nextString(), formatter)
	}
}

class LocalTimeAdapter : TypeAdapter<LocalTime>() {
	private val formatter = DateTimeFormatter.ISO_LOCAL_TIME

	override fun write(out: JsonWriter, value: LocalTime) {
		out.value(value.format(formatter))
	}

	override fun read(input: JsonReader): LocalTime {
		return LocalTime.parse(input.nextString(), formatter)
	}
}
