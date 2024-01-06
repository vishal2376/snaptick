package com.vishal2376.snaptick.domain.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vishal2376.snaptick.domain.model.Reminder

object ReminderListConverter {
	@TypeConverter
	fun fromReminderList(reminderList: List<Reminder>): String {
		val gson = Gson()
		return gson.toJson(reminderList)
	}

	@TypeConverter
	fun toReminderList(reminderListString: String): List<Reminder> {
		val gson = Gson()
		val type = object : TypeToken<List<Reminder>>() {}.type
		return gson.fromJson(
			reminderListString,
			type
		)
	}
}