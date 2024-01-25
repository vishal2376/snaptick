package com.vishal2376.snaptick.util

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.VibrationEffect
import android.os.Vibrator

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