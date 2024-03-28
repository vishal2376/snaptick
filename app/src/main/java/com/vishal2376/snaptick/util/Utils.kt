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


fun getFormattedDuration(
	startTime: LocalTime,
	endTime: LocalTime
): String {
	val taskDuration = endTime.toSecondOfDay() - startTime.toSecondOfDay()

	val hours = taskDuration / 3600
	val minutes = (taskDuration % 3600) / 60

	val hoursString = if (hours == 1) "hour" else "hours"

	return when {
		hours > 0 && minutes > 0 -> String.format("%dh %02dm", hours, minutes)
		hours > 0 -> String.format("%d $hoursString", hours)
		else -> String.format("%dmin", minutes)
	}

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

fun shareApp(context: Context) {
	val shareIntent = Intent(Intent.ACTION_SEND)
	shareIntent.type = "text/plain"
	var shareMessage = context.getString(R.string.tag_line)
	shareMessage += Constants.PLAY_STORE_BASE_URL + context.packageName + "\n\n"
	shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
	shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
	context.startActivity(Intent.createChooser(shareIntent, "Share This App"))
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

fun openUrl(context: Context, urlString: String) {
	val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
	context.startActivity(intent)
}