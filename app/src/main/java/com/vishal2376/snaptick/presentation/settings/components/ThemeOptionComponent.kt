package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.ui.theme.Black200
import com.vishal2376.snaptick.ui.theme.Black500
import com.vishal2376.snaptick.ui.theme.Blue200
import com.vishal2376.snaptick.ui.theme.Blue500
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.ui.theme.White200
import com.vishal2376.snaptick.ui.theme.White500

data class ThemeColor(
	val primaryColor: Color,
	val secondaryColor: Color,
	val textColor: Color,
)

@Composable
fun ThemeOptionComponent(
	defaultTheme: AppTheme,
	dynamicTheme: Boolean,
	onSelect: (AppTheme) -> Unit,
	onChangedDynamicTheme: (Boolean) -> Unit
) {
	var selectedOption by remember { mutableStateOf(defaultTheme) }
	var isDynamicThemeEnabled by remember { mutableStateOf(dynamicTheme) }

	val themeList = listOf(
		ThemeColor(White500, White200, Black500),
		ThemeColor(Blue500, Blue200, White500),
		ThemeColor(Black500, Black200, White500)
	)

	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 24.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			AppTheme.entries.forEach {
				ThemeItemComponent(
					title = it.name,
					theme = themeList[it.ordinal],
					isSelected = selectedOption == it,
					modifier = Modifier.weight(1f)
				) {
					onSelect(it)
					selectedOption = it
				}
			}
		}
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(24.dp, 0.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				imageVector = Icons.Default.Palette,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onBackground
			)
			Text(
				modifier = Modifier
					.weight(1f)
					.padding(start = 4.dp),
				text = stringResource(R.string.material_you),
				style = h2TextStyle,
				color = MaterialTheme.colorScheme.onBackground
			)

			Switch(
				checked = isDynamicThemeEnabled,
				onCheckedChange = {
					onChangedDynamicTheme(it)
					isDynamicThemeEnabled = it
				},
				colors = SwitchDefaults.colors(
					checkedThumbColor = MaterialTheme.colorScheme.primary,
					checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
					uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer,
					checkedBorderColor = MaterialTheme.colorScheme.primary
				)
			)
		}
	}
}

@Composable
fun ThemeItemComponent(
	title: String,
	theme: ThemeColor,
	isSelected: Boolean,
	modifier: Modifier,
	onClick: () -> Unit
) {
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(4.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.clip(RoundedCornerShape(8.dp))
				.clickable { onClick() }
				.background(theme.primaryColor),
			contentAlignment = Alignment.Center
		) {
			Text(
				text = title,
				style = h3TextStyle,
				color = theme.textColor,
				modifier = Modifier.padding(4.dp, 16.dp)
			)
		}

		if (isSelected) {

			val animValue = remember { Animatable(initialValue = 0f) }

			LaunchedEffect(Unit) {
				animValue.animateTo(1f, tween(300))
			}

			Box(
				modifier = Modifier
					.width(40.dp * animValue.value)
					.height(4.dp)
					.background(theme.textColor, RoundedCornerShape(8.dp))
			)
		}
	}
}

@Preview
@Composable
fun ThemeOptionComponentPreview() {
	SnaptickTheme {
		ThemeOptionComponent(
			defaultTheme = AppTheme.Light,
			true,
			onChangedDynamicTheme = {},
			onSelect = {})
	}
}