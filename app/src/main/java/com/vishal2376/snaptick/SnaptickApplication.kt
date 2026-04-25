package com.vishal2376.snaptick

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.worker.RescheduleAllRemindersWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import org.acra.ACRA
import org.acra.BuildConfig
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.DialogConfigurationBuilder
import org.acra.config.MailSenderConfigurationBuilder
import org.acra.data.StringFormat
import javax.inject.Inject

@HiltAndroidApp
class SnaptickApplication : Application(), Configuration.Provider {

	@Inject
	lateinit var workerFactory: HiltWorkerFactory

	override val workManagerConfiguration: Configuration
		get() = Configuration.Builder()
			.setWorkerFactory(workerFactory)
			.setMinimumLoggingLevel(Log.INFO)
			.setExecutor(Dispatchers.Default.asExecutor())
			.build()

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
		ensureRemindersArmed()
	}

	/**
	 * On every cold start, kick a one-shot worker that walks the DB and
	 * re-arms next-fire alarms. Cheap (single DB read) and covers the case
	 * where AlarmManager dropped pending alarms (force-stop, "Clear data" of
	 * the system Settings provider, etc.).
	 *
	 * Reboot, package replace, and time/timezone changes are handled by
	 * `SystemEventReceiver`; this is the safety net for everything else.
	 */
	private fun ensureRemindersArmed() {
		val request = OneTimeWorkRequestBuilder<RescheduleAllRemindersWorker>().build()
		WorkManager.getInstance(applicationContext).enqueueUniqueWork(
			UNIQUE_BACKFILL_WORK_NAME,
			ExistingWorkPolicy.KEEP,
			request,
		)
	}

	companion object {
		private const val UNIQUE_BACKFILL_WORK_NAME = "snaptick.reminder-backfill"
	}
}
