package com.vishal2376.snaptick

import android.app.Application
import android.content.Context
import com.vishal2376.snaptick.util.Constants
import dagger.hilt.android.HiltAndroidApp
import org.acra.ACRA
import org.acra.BuildConfig
import org.acra.config.CoreConfigurationBuilder
import org.acra.config.DialogConfigurationBuilder
import org.acra.config.MailSenderConfigurationBuilder
import org.acra.data.StringFormat

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
}