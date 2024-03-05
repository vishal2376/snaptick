package com.vishal2376.snaptick.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
	primary = Blue500,
	secondary = Blue200,
	background = Blue500,
	onPrimary = White500,
	onSecondary = LightGray,
)

private val AmoledDarkColorScheme = darkColorScheme(
	primary = Black500,
	secondary = Black200,
	background = Black500,
	onPrimary = White500,
	onSecondary = LightGray,
)

private val LightColorScheme = lightColorScheme(
	primary = White500,
	secondary = White200,
	background = White500,
	onPrimary = Black500,
	onSecondary = DarkGray,
)

enum class AppTheme {
	Light, Dark, Amoled
}

@Composable
fun SnaptickTheme(
	theme: AppTheme = AppTheme.Dark,
	dynamicColor: Boolean = false,
	content: @Composable () -> Unit
) {
	val colorScheme = when (theme) {
//		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//			val context = LocalContext.current
//			if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//		}
		AppTheme.Light -> LightColorScheme
		AppTheme.Dark -> DarkColorScheme
		AppTheme.Amoled -> AmoledDarkColorScheme
	}
	val view = LocalView.current
	if (!view.isInEditMode) {
		SideEffect {
			val window = (view.context as Activity).window
			window.statusBarColor = colorScheme.primary.toArgb()
			WindowCompat.getInsetsController(
				window,
				view
			).isAppearanceLightStatusBars = true
		}
	}

	MaterialTheme(
		colorScheme = colorScheme,
		typography = Typography,
		content = content
	)
}