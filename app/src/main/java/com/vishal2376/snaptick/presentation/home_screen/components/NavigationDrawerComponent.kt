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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.NavDrawerItem
import com.vishal2376.snaptick.presentation.common.fontRobotoMono
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.infoTextStyle
import com.vishal2376.snaptick.presentation.main.MainEvent
import com.vishal2376.snaptick.presentation.main.MainState

@Composable
fun NavigationDrawerComponent(
	appState: MainState,
	onMainEvent: (MainEvent) -> Unit,
	onClickThisWeek: () -> Unit,
	onClickSettings: () -> Unit,
	onClickBackup: () -> Unit,
	onClickRestore: () -> Unit
) {

	val context = LocalContext.current

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.fillMaxHeight()
			.fillMaxWidth(0.8f)
			.verticalScroll(rememberScrollState())
			.padding(vertical = 64.dp)
	) {

		Spacer(modifier = Modifier.height(50.dp))
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Image(
				painter = painterResource(if (appState.theme == AppTheme.Amoled) R.drawable.app_logo_amoled else R.drawable.app_logo),
				contentDescription = null,
				modifier = Modifier.size(64.dp),
			)
			Spacer(modifier = Modifier.height(16.dp))
			Text(
				text = stringResource(R.string.app_name),
				style = h2TextStyle,
				color = MaterialTheme.colorScheme.onBackground
			)
			Text(
				text = stringResource(R.string.buildVersion, appState.buildVersion),
				fontFamily = fontRobotoMono,
				fontSize = 15.sp,
				fontWeight = FontWeight.Bold,
				color = MaterialTheme.colorScheme.onBackground
			)
		}

		Divider(
			modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
			color = MaterialTheme.colorScheme.primaryContainer
		)

		Column {

			NavDrawerItemUI(
				icon = Icons.Default.CalendarMonth,
				label = stringResource(R.string.this_week)
			) { onClickThisWeek() }

			Spacer(modifier = Modifier.height(8.dp))

			NavDrawerItemUI(
				icon = Icons.Default.Settings,
				label = stringResource(R.string.settings)
			) { onClickSettings() }

			Spacer(modifier = Modifier.height(8.dp))

			NavDrawerItemUI(
				icon = Icons.Default.Backup,
				label = "Backup"
			) { onClickBackup() }

			Spacer(modifier = Modifier.height(8.dp))

			NavDrawerItemUI(
				icon = Icons.Default.Restore,
				label = "Restore"
			) { onClickRestore() }

			Divider(
				modifier = Modifier.padding(vertical = 10.dp),
				color = MaterialTheme.colorScheme.primaryContainer
			)

			NavDrawerItem.entries.forEach {
				NavDrawerItemUI(icon = it.icon, label = stringResource(id = it.stringId)) {
					onMainEvent(MainEvent.OnClickNavDrawerItem(context, it))
				}
				Spacer(modifier = Modifier.height(8.dp))
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
			modifier = Modifier.size(28.dp),
			imageVector = icon,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onBackground
		)
		Text(text = label, style = infoTextStyle, color = MaterialTheme.colorScheme.onBackground)
	}
}

@Preview(widthDp = 350)
@Composable
fun NavigationDrawerComponentPreview() {
	NavigationDrawerComponent(MainState(), {}, {}, {}, {}, {})
}