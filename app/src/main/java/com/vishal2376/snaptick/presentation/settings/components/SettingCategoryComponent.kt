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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.common.settingItemTextStyle
import com.vishal2376.snaptick.presentation.settings.common.SettingCategoryItem
import com.vishal2376.snaptick.ui.theme.SnaptickTheme


@Composable
fun SettingsCategoryComponent(categoryTitle: String, categoryList: List<SettingCategoryItem>) {
	Column(
		modifier = Modifier.padding(16.dp, 0.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		if (categoryTitle.isNotEmpty()) {
			Text(
				modifier = Modifier.padding(start = 8.dp),
				text = categoryTitle,
				style = infoDescTextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
		}
		Column(modifier = Modifier.clip(RoundedCornerShape(8.dp))) {
			categoryList.forEachIndexed { index, item ->
				CategoryItemComponent(
					title = item.title,
					resId = item.resId,
					onClick = item.onClick
				)
				if (index != categoryList.lastIndex) {
					Divider(color = MaterialTheme.colorScheme.background)
				}
			}
		}
	}
}


@Composable
fun CategoryItemComponent(title: String, resId: Int, onClick: () -> Unit) {
	val icon = painterResource(id = resId)

	Row(modifier = Modifier
		.fillMaxWidth()
		.clickable { onClick() }
		.background(MaterialTheme.colorScheme.primaryContainer)
		.padding(16.dp, 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Icon(
			modifier = Modifier.size(20.dp),
			painter = icon,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onBackground
		)
		Text(
			modifier = Modifier.weight(1f),
			text = title,
			style = settingItemTextStyle,
			color = MaterialTheme.colorScheme.onBackground
		)
		Icon(
			modifier = Modifier.size(16.dp),
			imageVector = Icons.Default.ArrowForwardIos,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onBackground
		)
	}
}


@Preview
@Composable
fun SettingsCategoryComponentPreview() {
	val generalCategoryList = listOf(
		SettingCategoryItem("Theme", R.drawable.ic_theme),
		SettingCategoryItem("Language", R.drawable.ic_translate)
	)
	SnaptickTheme {
		SettingsCategoryComponent("General", generalCategoryList)
	}
}