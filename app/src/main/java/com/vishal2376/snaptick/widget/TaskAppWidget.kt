package com.vishal2376.snaptick.widget

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.material3.ColorProviders
import androidx.glance.state.GlanceStateDefinition
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.ui.theme.AmoledDarkColorScheme
import com.vishal2376.snaptick.ui.theme.DarkColorScheme
import com.vishal2376.snaptick.ui.theme.LightColorScheme
import com.vishal2376.snaptick.widget.model.WidgetState
import com.vishal2376.snaptick.widget.presentation.SnaptickTaskWidget
import com.vishal2376.snaptick.widget.state.WidgetStateDefinition

/**
 * Snaptick App Widget that displays today's tasks.
 * Syncs with app settings for time format, theme, and dynamic theming.
 */
class TaskAppWidget : GlanceAppWidget() {

	override val stateDefinition: GlanceStateDefinition<WidgetState> = WidgetStateDefinition

	override suspend fun provideGlance(context: Context, id: GlanceId) {
		provideContent {
			val state = currentState<WidgetState>()

			WidgetThemeWrapper(
				theme = state.theme,
				useDynamicTheme = state.useDynamicTheme
			) {
				SnaptickTaskWidget(
					tasks = state.tasks,
					is24HourFormat = state.is24HourFormat
				)
			}
		}
	}
}

/**
 * Widget theme wrapper that supports:
 * - Static themes (Light, Dark, Amoled) from app settings
 * - Dynamic theming on Android 12+ when enabled
 */
@Composable
private fun WidgetThemeWrapper(
	theme: AppTheme,
	useDynamicTheme: Boolean,
	content: @Composable () -> Unit
) {
	val colorScheme = when {
		// Use dynamic colors on Android 12+ if enabled
		useDynamicTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			// GlanceTheme will automatically use system dynamic colors
			null
		}
		// Otherwise use static theme from app settings
		else -> when (theme) {
			AppTheme.Light -> LightColorScheme
			AppTheme.Dark -> DarkColorScheme
			AppTheme.Amoled -> AmoledDarkColorScheme
		}
	}

	if (colorScheme != null) {
		GlanceTheme(
			colors = ColorProviders(colorScheme),
			content = content
		)
	} else {
		// Use default GlanceTheme which applies dynamic colors on Android 12+
		GlanceTheme(content = content)
	}
}