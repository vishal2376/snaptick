package com.vishal2376.snaptick.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

	fun setLocale(context: Context, language: String = "en"):Context {
		val locale = Locale(language)
		Locale.setDefault(locale)

		val resources = context.resources
		val configuration = Configuration(resources.configuration)
		configuration.setLocale(locale)

		return context.createConfigurationContext(configuration)


	}
}