package com.vishal2376.snaptick.presentation.home_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.NavDrawerItem
import com.vishal2376.snaptick.presentation.common.fontRobotoMono
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.main.MainEvent
import com.vishal2376.snaptick.ui.theme.AppTheme
import com.vishal2376.snaptick.ui.theme.Blue

@Composable
fun NavigationDrawerComponent(
	appTheme: AppTheme,
	buildVersionCode: String,
	onMainEvent: (MainEvent) -> Unit,
	onClickThisWeek: () -> Unit
) {

	val context = LocalContext.current

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier = Modifier
			.fillMaxHeight()
			.fillMaxWidth(0.8f)
			.padding(vertical = 64.dp)
	) {

		Spacer(modifier = Modifier.height(50.dp))
		Column(
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Image(
				painter = painterResource(if (appTheme == AppTheme.Amoled) R.drawable.app_logo_amoled else R.drawable.app_logo),
				contentDescription = null,
				modifier = Modifier.size(64.dp),
			)
			Spacer(modifier = Modifier.height(16.dp))
			Text(
				text = stringResource(R.string.app_name),
				style = h2TextStyle,
				color = MaterialTheme.colorScheme.onPrimary
			)
			Text(
				text = stringResource(R.string.buildVersion, buildVersionCode),
				fontFamily = fontRobotoMono,
				fontSize = 15.sp,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onPrimary
			)
		}
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Text(
				text = stringResource(R.string.amoled_theme),
				style = h3TextStyle,
				color = MaterialTheme.colorScheme.onPrimary
			)
			Switch(
				checked = appTheme == AppTheme.Amoled,
				onCheckedChange = {
					onMainEvent(MainEvent.ToggleAmoledTheme(it, context))
				},
				colors = SwitchDefaults.colors(
					checkedThumbColor = Blue,
					checkedTrackColor = MaterialTheme.colorScheme.secondary,
					uncheckedTrackColor = MaterialTheme.colorScheme.secondary
				)
			)
		}



		Divider(thickness = 2.dp, color = MaterialTheme.colorScheme.secondary)

		Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
			NavDrawerItemUI(
				icon = Icons.Default.CalendarMonth,
				label = "This Week"
			) { onClickThisWeek() }

			NavDrawerItem.entries.forEach {
				NavDrawerItemUI(icon = it.icon, label = it.display) {
					onMainEvent(MainEvent.OnClickNavDrawerItem(context, it))
				}
			}
		}

	}

}

@Composable
fun NavDrawerItemUI(icon: ImageVector, label: String, onClick: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onClick() }
			.padding(32.dp, 8.dp),
		horizontalArrangement = Arrangement.spacedBy(10.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			modifier = Modifier.size(32.dp),
			imageVector = icon,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onPrimary
		)
		Text(text = label, style = taskTextStyle, color = MaterialTheme.colorScheme.onPrimary)
	}
}

@Preview(widthDp = 350)
@Composable
fun NavigationDrawerComponentPreview() {
	NavigationDrawerComponent(AppTheme.Amoled, "0.1", {}, {})
}