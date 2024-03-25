package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.infoTextStyle
import com.vishal2376.snaptick.presentation.settings.common.SettingCategoryItem
import com.vishal2376.snaptick.ui.theme.SnaptickTheme


@Composable
fun SettingsCategoryComponent(categoryTitle: String, categoryList: List<SettingCategoryItem>) {
	Column(modifier = Modifier.padding(16.dp),verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Text(
			modifier = Modifier.padding(start = 8.dp),
			text = categoryTitle,
			style = infoDescTextStyle,
			color = MaterialTheme.colorScheme.onSecondary
		)
		Column(modifier = Modifier.clip(RoundedCornerShape(8.dp))) {
			categoryList.forEachIndexed { index, item ->
				CategoryItemComponent(title = item.title, icon = item.icon, onClick = item.onClick)
				if (index != categoryList.lastIndex) {
					Divider(color = MaterialTheme.colorScheme.primary)
				}
			}
		}
	}
}


@Composable
fun CategoryItemComponent(title: String, icon: ImageVector, onClick: () -> Unit) {
	Row(modifier = Modifier
		.fillMaxWidth()
		.clickable { onClick() }
		.background(MaterialTheme.colorScheme.secondary)
		.padding(16.dp, 8.dp),

		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Icon(
			modifier = Modifier.size(20.dp),
			imageVector = icon,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onPrimary
		)
		Text(
			modifier = Modifier.weight(1f),
			text = title,
			style = infoTextStyle,
			color = MaterialTheme.colorScheme.onPrimary
		)
		Icon(
			modifier = Modifier.size(16.dp),
			imageVector = Icons.Default.ArrowForwardIos,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onPrimary
		)
	}
}


@Preview
@Composable
fun SettingsCategoryComponentPreview() {
	val generalCategoryList = listOf(
		SettingCategoryItem("Theme", Icons.Default.FormatPaint),
		SettingCategoryItem("Language", Icons.Default.Translate)
	)
	SnaptickTheme {
		SettingsCategoryComponent("General", generalCategoryList)
	}
}