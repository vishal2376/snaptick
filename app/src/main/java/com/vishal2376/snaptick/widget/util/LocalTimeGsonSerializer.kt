package com.vishal2376.snaptick.widget.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * A Serializer for [LocalTime] as gson cannot directly convert [LocalTime] objects to Json
 * a serialization class is added which should be added with the [GsonBuilder] to convert objects
 * properly
 */
object LocalTimeGsonSerializer
	: JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

	private val formatter = DateTimeFormatter.ISO_TIME

	override fun serialize(
		src: LocalTime?,
		typeOfSrc: Type?,
		context: JsonSerializationContext?
	): JsonElement {
		val formattedTime = formatter.format(src)
		return JsonPrimitive(formattedTime)
	}

	override fun deserialize(
		json: JsonElement?,
		typeOfT: Type?,
		context: JsonDeserializationContext?
	): LocalTime {
		val timeASString = json?.asString
		return LocalTime.parse(timeASString, formatter)
	}

}

