package com.vishal2376.snaptick.util

import android.content.Context
import androidx.annotation.StyleRes
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.AppTheme

/**
 * `DataStore` is asynchronous, but the splash screen theme has to be applied
 * before `installSplashScreen()` (a synchronous call site). We keep a tiny
 * `SharedPreferences` mirror of the current theme ordinal that `MainViewModel`
 * writes on every theme update; `MainActivity` reads it in <1 ms before the
 * splash theme is chosen.
 *
 * First install has no mirror entry, so we default to Amoled, which matches the
 * "first launch = dark amoled" requirement.
 */
object SplashThemeMirror {

	private const val PREFS = "snaptick_splash_prefs"
	private const val KEY_THEME_ORDINAL = "theme_ordinal"

	fun write(context: Context, theme: AppTheme) {
		context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
			.edit()
			.putInt(KEY_THEME_ORDINAL, theme.ordinal)
			.apply()
	}

	fun read(context: Context): AppTheme {
		val ordinal = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
			.getInt(KEY_THEME_ORDINAL, AppTheme.Amoled.ordinal)
		return AppTheme.entries.getOrElse(ordinal) { AppTheme.Amoled }
	}

	@StyleRes
	fun startingThemeRes(context: Context): Int = when (read(context)) {
		AppTheme.Light -> R.style.Theme_App_Starting_Light
		AppTheme.Dark -> R.style.Theme_App_Starting_Dark
		AppTheme.Amoled -> R.style.Theme_App_Starting_Amoled
	}
}
