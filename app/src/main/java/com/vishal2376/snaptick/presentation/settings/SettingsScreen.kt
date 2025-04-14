package com.vishal2376.snaptick.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.main.MainEvent
import com.vishal2376.snaptick.presentation.main.MainState
import com.vishal2376.snaptick.presentation.settings.common.SettingCategoryItem
import com.vishal2376.snaptick.presentation.settings.components.LanguageOptionComponent
import com.vishal2376.snaptick.presentation.settings.components.SettingsCategoryComponent
import com.vishal2376.snaptick.presentation.settings.components.SleepTimeOptionComponent
import com.vishal2376.snaptick.presentation.settings.components.SwipeActionOptionComponent
import com.vishal2376.snaptick.presentation.settings.components.ThemeOptionComponent
import com.vishal2376.snaptick.presentation.settings.components.TimePickerOptionComponent
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.openUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
	appState: MainState,
	onEvent: (MainEvent) -> Unit,
	onClickAbout: () -> Unit,
	onBack: () -> Unit
) {
	val context = LocalContext.current
	val sheetState = rememberModalBottomSheetState()
	val scope = rememberCoroutineScope()
	var showBottomSheetById by remember { mutableIntStateOf(0) }

	val settingsAbout = listOf(
		SettingCategoryItem(title = stringResource(R.string.about),
			resId = R.drawable.ic_info,
			onClick = { onClickAbout() }
		),
		SettingCategoryItem(title = stringResource(R.string.support),
			resId = R.drawable.ic_support,
			onClick = {
				val repoUrl = Constants.GITHUB + "/snaptick#snaptick"
				openUrl(context, repoUrl)
			}
		),
	)

	val settingsGeneral = listOf(
		SettingCategoryItem(
			title = stringResource(R.string.theme),
			resId = R.drawable.ic_theme,
			onClick = { showBottomSheetById = R.string.theme }
		),
		SettingCategoryItem(
			title = stringResource(R.string.language),
			resId = R.drawable.ic_translate,
			onClick = { showBottomSheetById = R.string.language }
		),
		SettingCategoryItem(
			title = stringResource(R.string.sleep_time),
			resId = R.drawable.ic_moon,
			onClick = { showBottomSheetById = R.string.sleep_time }
		),
		SettingCategoryItem(
			title = stringResource(R.string.time_picker),
			resId = R.drawable.ic_clock,
			onClick = { showBottomSheetById = R.string.time_picker }
		),
		SettingCategoryItem(
			title = stringResource(R.string.swipe_action),
			resId = R.drawable.ic_swipe_left,
			onClick = { showBottomSheetById = R.string.swipe_action }
		),
	)

	val settingsFollow = listOf(
		SettingCategoryItem(
			title = stringResource(R.string.twitter),
			resId = R.drawable.ic_twitter,
			onClick = { openUrl(context, Constants.TWITTER) }
		),
		SettingCategoryItem(
			title = stringResource(R.string.github),
			resId = R.drawable.ic_github,
			onClick = { openUrl(context, Constants.GITHUB) }
		),
		SettingCategoryItem(
			title = stringResource(R.string.linkedin),
			resId = R.drawable.ic_linkedin,
			onClick = { openUrl(context, Constants.LINKEDIN) }
		),
		SettingCategoryItem(
			title = stringResource(R.string.instagram),
			resId = R.drawable.ic_instagram,
			onClick = { openUrl(context, Constants.INSTAGRAM) }
		),
	)

	Scaffold(topBar = {
		TopAppBar(
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = MaterialTheme.colorScheme.background,
			),
			title = {
				Text(
					text = stringResource(R.string.settings),
					style = h1TextStyle
				)
			},
			navigationIcon = {
				IconButton(onClick = { onBack() }) {
					Icon(
						imageVector = Icons.Rounded.ArrowBack,
						contentDescription = null
					)
				}
			},
		)
	}) { innerPadding ->

		if (showBottomSheetById != 0) {
			ModalBottomSheet(
				onDismissRequest = { showBottomSheetById = 0 },
				sheetState = sheetState,
				containerColor = MaterialTheme.colorScheme.primaryContainer,
			) {
				Box(modifier = Modifier.padding(16.dp)) {
					when (showBottomSheetById) {
						R.string.theme -> {
							ThemeOptionComponent(
								defaultTheme = appState.theme,
								dynamicTheme = appState.dynamicTheme,
								onChangedDynamicTheme = {
									onEvent(MainEvent.UpdateDynamicTheme(it, context))
								},
								onSelect = {
									onEvent(MainEvent.UpdateAppTheme(it, context))
								})
						}

						R.string.language -> {
							LanguageOptionComponent(defaultLanguage = appState.language) {
								onEvent(MainEvent.UpdateLanguage(it, context))
							}
						}

						R.string.sleep_time -> {
							SleepTimeOptionComponent(defaultSleepTime = appState.sleepTime) {
								onEvent(MainEvent.UpdateSleepTime(it, context))
							}
						}

						R.string.time_picker -> {
							TimePickerOptionComponent(
								isWheelTimePicker = appState.isWheelTimePicker,
								is24HourTimeFormat = appState.is24hourTimeFormat,
								onSelect = {
									onEvent(MainEvent.UpdateTimePicker(it, context))
								},
								onSelectTimeFormat = {
									onEvent(MainEvent.UpdateTimeFormat(it, context))
								})
						}

						R.string.swipe_action -> {
							SwipeActionOptionComponent(
								selected = appState.swipeBehaviour,
								onSelect = {
									onEvent(MainEvent.UpdateSwipeBehaviour(it, context))
								})
						}
					}
				}
			}
		}

		Column(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize(),
			verticalArrangement = Arrangement.SpaceBetween,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Column(
				modifier = Modifier.verticalScroll(rememberScrollState()),
				verticalArrangement = Arrangement.spacedBy(24.dp)
			) {
				SettingsCategoryComponent(
					categoryTitle = "",
					categoryList = settingsAbout
				)
				SettingsCategoryComponent(
					categoryTitle = stringResource(R.string.general_settings),
					categoryList = settingsGeneral
				)
				SettingsCategoryComponent(
					categoryTitle = stringResource(R.string.follow_developer),
					categoryList = settingsFollow
				)
			}

			Text(
				modifier = Modifier.padding(8.dp),
				text = stringResource(R.string.made_with_by_vishal_singh),
				style = infoDescTextStyle,
				color = MaterialTheme.colorScheme.onPrimaryContainer
			)
		}
	}
}

@Preview
@Composable
fun SettingsScreenPreview() {
	SnaptickTheme(theme = AppTheme.Amoled) {
		SettingsScreen(MainState(), {}, {}, {})
	}
}