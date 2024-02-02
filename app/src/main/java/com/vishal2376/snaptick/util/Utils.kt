package com.vishal2376.snaptick.util

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.VibrationEffect
import android.os.Vibrator
import java.time.LocalTime

fun vibrateDevice(
	context: Context,
	duration: Long = 500,
	vibrationEffect: Int = VibrationEffect.DEFAULT_AMPLITUDE
) {
	val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator?

	if (vibrator?.hasVibrator() == true) {
		vibrator.vibrate(VibrationEffect.createOneShot(duration, vibrationEffect))
	}
}

fun getFreeTime(totalDuration: Long): String {
	val maxTime = LocalTime.MAX.toSecondOfDay()
	val currentTime = LocalTime.now().toSecondOfDay()

	val totalFreeDuration = maxTime - currentTime - totalDuration

	val hours = totalFreeDuration / 3600
	val minutes = (totalFreeDuration % 3600) / 60

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