package com.vishal2376.snaptick.util

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import com.vishal2376.snaptick.R
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

fun updateLocale(context: Context, selectedLocale: String) {
	val updatedContext = LocaleHelper.setLocale(context, selectedLocale)
	context.resources.updateConfiguration(
		updatedContext.resources.configuration,
		updatedContext.resources.displayMetrics
	)
}

fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
	Toast.makeText(context, message, duration).show()
}

fun getFreeTime(totalDuration: Long, sleepTime: LocalTime): String {
	val maxTime = sleepTime.toSecondOfDay()
	val currentTime = LocalTime.now().toSecondOfDay()

	val totalFreeDuration = maxTime - currentTime - totalDuration

	val hours = (totalFreeDuration / 3600).toInt()
	val minutes = ((totalFreeDuration % 3600) / 60).toInt()


	val hoursString = if (hours == 1) "hour" else "hours"

	return when {
		hours > 0 && minutes > 0 -> String.format("%dh %02dm", hours, minutes)
		hours > 0 -> String.format("%d $hoursString", hours)
		else -> String.format("%d min", minutes)
	}
}

fun openMail(context: Context, title: String) {
	val subject = "${context.getString(R.string.app_name)}: $title"
	val uriBuilder = StringBuilder("mailto:" + Uri.encode(Constants.EMAIL))
	uriBuilder.append("?subject=" + Uri.encode(subject))
	val uriString = uriBuilder.toString()

	val intentTitle = "Send $title"
	val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(uriString))
	context.startActivity(Intent.createChooser(intent, intentTitle))
}

private val ALLOWED_URL_SCHEMES: Set<String> = setOf("https")

/**
 * Opens [urlString] in the user's default browser. Only `https://` URIs are
 * accepted; everything else (file://, intent://, content://, custom schemes)
 * is silently dropped to keep this helper from being abused as a generic
 * deeplink launcher if a future feature ever feeds a dynamic URL through it.
 */
fun openUrl(context: Context, urlString: String) {
	val uri = runCatching { Uri.parse(urlString) }.getOrNull() ?: return
	if (uri.scheme?.lowercase() !in ALLOWED_URL_SCHEMES) return
	context.startActivity(Intent(Intent.ACTION_VIEW, uri))
}
