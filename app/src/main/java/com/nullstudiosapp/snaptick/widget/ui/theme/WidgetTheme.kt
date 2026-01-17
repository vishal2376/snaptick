package com.nullstudiosapp.snaptick.widget.ui.theme

import androidx.compose.runtime.Composable
import androidx.glance.GlanceComposable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import com.nullstudiosapp.snaptick.presentation.common.AppTheme
import com.nullstudiosapp.snaptick.ui.theme.AmoledDarkColorScheme
import com.nullstudiosapp.snaptick.ui.theme.DarkColorScheme
import com.nullstudiosapp.snaptick.ui.theme.LightColorScheme

@Composable
@GlanceComposable
fun SnaptickWidgetTheme(
	theme: AppTheme = AppTheme.Amoled,
	content: @Composable () -> Unit
) {
	val colorScheme = when (theme) {
		AppTheme.Light -> LightColorScheme
		AppTheme.Dark -> DarkColorScheme
		AppTheme.Amoled -> AmoledDarkColorScheme
	}

	GlanceTheme(
		colors = ColorProviders(colorScheme),
		content = content
	)
}