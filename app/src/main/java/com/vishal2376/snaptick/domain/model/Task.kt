package com.vishal2376.snaptick.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.vishal2376.snaptick.domain.converters.LocalTimeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "task_table")
@TypeConverters(LocalTimeConverter::class)
data class Task(
	@PrimaryKey(autoGenerate = true) val id: Int = 0,
	val title: String,
	val isCompleted: Boolean,
	val startTime: LocalTime,
	val endTime: LocalTime
) {
	fun getFormattedTime(): String {
		val startTimeFormat = formatTime(startTime)
		val endTimeFormat = formatTime(endTime)
		return "$startTimeFormat - $endTimeFormat"
	}

	private fun formatTime(time: LocalTime): String {
		val dtf = DateTimeFormatter.ofPattern("hh:mm a")
		return time.format(dtf)
	}
}