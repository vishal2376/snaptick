package com.vishal2376.snaptick

import android.app.Application
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.worker.RepeatTaskWorker
import dagger.hilt.android.HiltAndroidApp
import org.acra.ACRA
import org.acra.BuildConfig
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.DialogConfigurationBuilder
import org.acra.config.MailSenderConfigurationBuilder
import org.acra.data.StringFormat
import java.time.LocalTime
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class SnaptickApplication : Application() {
	override fun attachBaseContext(base: Context?) {
		super.attachBaseContext(base)
		ACRA.init(
			this, CoreConfigurationBuilder()
				.withBuildConfigClass(BuildConfig::class.java)
				.withReportFormat(StringFormat.JSON)
				.withPluginConfigurations(

					// Dialog configuration:
					DialogConfigurationBuilder()
						.withText(getString(R.string.dialog_text))
						.withTitle(getString(R.string.dialog_title))
						.withPositiveButtonText(getString(R.string.dialog_positive))
						.withNegativeButtonText(getString(R.string.dialog_negative))
						.build(),

					// Mail sender configuration:
					MailSenderConfigurationBuilder()
						.withMailTo(Constants.EMAIL)
						.withReportFileName("crash_report.txt")
						.withReportAsFile(true)
						.build()
				)
		)
	}

	override fun onCreate() {
		super.onCreate()

		initWorker()
	}

	private fun initWorker() {
		val maxTimeSec = LocalTime.MAX.toSecondOfDay()
		val currentTimeSec = LocalTime.now().toSecondOfDay()

		val delay = (maxTimeSec - currentTimeSec) + 30 // to execute after 12:00:30 AM

		if (delay > 0) {
			startRepeatWorker(delay)
		}
	}

	private fun startRepeatWorker(delay: Int) {
		// repeat task request
		val workRequest =
			PeriodicWorkRequest.Builder(RepeatTaskWorker::class.java, 1, TimeUnit.DAYS)
				.setInitialDelay(delay.toLong(), TimeUnit.SECONDS)
				.build()

		WorkManager.getInstance(applicationContext)
			.enqueueUniquePeriodicWork(
				"Repeat-Tasks",
				ExistingPeriodicWorkPolicy.KEEP,
				workRequest
			)
	}
}