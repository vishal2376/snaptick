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

val DarkColorScheme = darkColorScheme(
	primary = Blue500,
	secondary = Blue200,
	background = Blue500,
)

val AmoledDarkColorScheme = darkColorScheme(
	primary = Black500,
	secondary = Black200,
	background = Black500,
)

private val LightColorScheme = lightColorScheme(
	primary = Purple40,
	secondary = PurpleGrey40,
	tertiary = Pink40

	/* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

enum class AppTheme {
	Light, Dark, Amoled
}

@Composable
fun SnaptickTheme(
	darkTheme: Boolean = false,
	// Dynamic color is available on Android 12+
	dynamicColor: Boolean = false,
	theme: AppTheme = AppTheme.Dark,
	content: @Composable () -> Unit
) {
	val colorScheme = when (theme) {
//		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//			val context = LocalContext.current
//			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//		}

		AppTheme.Dark -> DarkColorScheme
		AppTheme.Amoled -> AmoledDarkColorScheme

		else -> LightColorScheme
	}
	val view = LocalView.current
	if (!view.isInEditMode) {
		SideEffect {
			val window = (view.context as Activity).window
			window.statusBarColor = colorScheme.primary.toArgb()
			WindowCompat.getInsetsController(
				window,
				view
			).isAppearanceLightStatusBars = darkTheme
		}
	}

	MaterialTheme(
		colorScheme = colorScheme,
		typography = Typography,
		content = content
	)
}