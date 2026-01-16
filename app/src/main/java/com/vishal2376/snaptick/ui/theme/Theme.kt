package com.vishal2376.snaptick.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.vishal2376.snaptick.presentation.common.AppTheme

val DarkColorScheme = darkColorScheme(
	primary = Blue,
	onPrimary = Blue500,
	background = Blue500,
	onBackground = White500,
	primaryContainer = Blue200,
	onPrimaryContainer = LightGray,
)

val AmoledDarkColorScheme = darkColorScheme(
	primary = Blue,
	onPrimary = Blue500,
	background = Black500,
	onBackground = White500,
	primaryContainer = Black200,
	onPrimaryContainer = LightGray,
)

val LightColorScheme = lightColorScheme(
	primary = Blue,
	onPrimary = Blue500,
	background = White500,
	onBackground = Black500,
	primaryContainer = White200,
	onPrimaryContainer = DarkGray,
)


@Composable
fun SnaptickTheme(
	theme: AppTheme = AppTheme.Amoled,
	dynamicColor: Boolean = false,
	content: @Composable () -> Unit
) {
	val colorScheme = when {
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			val context = LocalContext.current
			if (theme == AppTheme.Light)
				dynamicLightColorScheme(context)
			else
				dynamicDarkColorScheme(context)
		}

		theme == AppTheme.Light -> LightColorScheme
		theme == AppTheme.Dark -> DarkColorScheme
		theme == AppTheme.Amoled -> AmoledDarkColorScheme
		else -> DarkColorScheme
	}
	val view = LocalView.current
	if (!view.isInEditMode) {
		SideEffect {
			val window = (view.context as Activity).window
			window.statusBarColor = colorScheme.background.toArgb()
			WindowCompat.getInsetsController(
				window,
				view
			).isAppearanceLightStatusBars = theme == AppTheme.Light
		}
	}

	MaterialTheme(
		colorScheme = colorScheme,
		typography = Typography,
		content = content
	)
}