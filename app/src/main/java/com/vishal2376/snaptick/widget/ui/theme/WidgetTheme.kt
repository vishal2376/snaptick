package com.vishal2376.snaptick.widget.ui.theme

import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.glance.GlanceComposable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.ui.theme.AmoledDarkColorScheme
import com.vishal2376.snaptick.ui.theme.DarkColorScheme
import com.vishal2376.snaptick.ui.theme.LightColorScheme

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