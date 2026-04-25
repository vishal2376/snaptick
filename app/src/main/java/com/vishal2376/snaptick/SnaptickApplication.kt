package com.vishal2376.snaptick

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.vishal2376.snaptick.worker.RescheduleAllRemindersWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import org.acra.ACRA
import org.acra.ReportField
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

		// ACRA init is gated on a build-time email destination. The repo carries
		// no personal email; CI / local dev injects it via the `acraEmail` Gradle
		// property. When unset (forks, contributor builds), ACRA stays off.
		val acraEmail = BuildConfig.ACRA_EMAIL
		if (acraEmail.isBlank()) return

		ACRA.init(
			this, CoreConfigurationBuilder()
				.withBuildConfigClass(org.acra.BuildConfig::class.java)
				.withReportFormat(StringFormat.JSON)
				// Strict allowlist: only ship fields needed to triage a crash.
				// USER_COMMENT, USER_EMAIL, LOGCAT, SHARED_PREFERENCES, SETTINGS_*
				// are intentionally omitted - they tend to leak task content via
				// log lines or copy-pasted comments.
				.withReportContent(
					ReportField.STACK_TRACE,
					ReportField.APP_VERSION_NAME,
					ReportField.APP_VERSION_CODE,
					ReportField.PACKAGE_NAME,
					ReportField.ANDROID_VERSION,
					ReportField.PHONE_MODEL,
					ReportField.PRODUCT,
					ReportField.BUILD_CONFIG,
					ReportField.AVAILABLE_MEM_SIZE,
					ReportField.TOTAL_MEM_SIZE,
				)
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
						.withMailTo(acraEmail)
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
