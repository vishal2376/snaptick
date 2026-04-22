package com.vishal2376.snaptick.presentation.common.utils

import java.time.format.DateTimeFormatter

object Formatters {
	val time24h: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
	val time12h: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
	val timePicker24h: DateTimeFormatter = DateTimeFormatter.ofPattern("HH : mm")
	val timePicker12h: DateTimeFormatter = DateTimeFormatter.ofPattern("hh : mm a")
	val dayOfMonth: DateTimeFormatter = DateTimeFormatter.ofPattern("dd")
	val dayMonth: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM")
	val dayMonthYear: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM, yyyy")
	val isoDate: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
}
