package com.vishal2376.snaptick.widget.components

import androidx.compose.runtime.Composable
import androidx.glance.GlanceComposable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import com.vishal2376.snaptick.ui.theme.DarkColorScheme

@Composable
@GlanceComposable
fun SnaptickWidgetTheme(
	content: @Composable () -> Unit
) {
	GlanceTheme(
		colors = ColorProviders(DarkColorScheme),
		content = content
	)
}