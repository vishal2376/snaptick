package com.vishal2376.snaptick.util

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
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

	Toast.makeText(context, "Vibrate Device : 500ms", Toast.LENGTH_SHORT).show()
}

fun getFormattedFreeTime(totalDuration: Long): String {
	val totalFreeDuration = LocalTime.MAX.toSecondOfDay().toLong() - totalDuration

	val hours = totalFreeDuration / 3600
	val minutes = (totalFreeDuration % 3600) / 60
	val freeTime = hours + (minutes / 60f)
	val freeTimeText = String.format("%.1f hours", freeTime)
	if (freeTimeText.endsWith(".0")) {
		freeTimeText.substring(0, freeTimeText.length - 2)
	}
	return freeTimeText
}