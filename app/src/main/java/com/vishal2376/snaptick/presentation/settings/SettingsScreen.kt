package com.vishal2376.snaptick.presentation.settings

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.MainActivity
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.infoDescTextStyle
import com.vishal2376.snaptick.presentation.main.action.MainAction
import com.vishal2376.snaptick.presentation.main.state.MainState
import com.vishal2376.snaptick.presentation.main.viewmodel.MainViewModel
import com.vishal2376.snaptick.presentation.settings.common.SettingCategoryItem
import com.vishal2376.snaptick.presentation.settings.components.CalendarSyncOptionComponent
import com.vishal2376.snaptick.presentation.settings.components.EventImportOptionComponent
import com.vishal2376.snaptick.presentation.settings.components.LanguageOptionComponent
import com.vishal2376.snaptick.presentation.settings.components.SettingsCategoryComponent
import com.vishal2376.snaptick.presentation.settings.components.SleepTimeOptionComponent
import com.vishal2376.snaptick.presentation.settings.components.PomodoroOptionComponent
import com.vishal2376.snaptick.presentation.settings.components.SoundOptionComponent
import com.vishal2376.snaptick.presentation.settings.components.SwipeActionOptionComponent
import com.vishal2376.snaptick.presentation.settings.components.ThemeOptionComponent
import com.vishal2376.snaptick.presentation.settings.components.TimePickerOptionComponent
import com.vishal2376.snaptick.presentation.settings.components.UpdateStatusComponent
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.util.openUrl
import com.vishal2376.snaptick.util.showToast
import com.vishal2376.snaptick.widget.receiver.WidgetReceiver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
	appState: MainState,
	onAction: (MainAction) -> Unit,
	onClickAbout: () -> Unit,
	onBack: () -> Unit,
	mainViewModel: MainViewModel? = null,
) {
	val context = LocalContext.current
	val sheetState = rememberModalBottomSheetState()
	var showBottomSheetById by remember { mutableIntStateOf(0) }
	val writableCalendars = appState.writableCalendars

	// Refresh writable calendars whenever the device-calendar sheet opens or
	// the user just toggled sync on (which itself triggers a permission grant
	// flow that lands back here with permission newly available).
	LaunchedEffect(showBottomSheetById, appState.calendarSyncEnabled) {
		if (showBottomSheetById == R.string.calendar_sync) {
			onAction(MainAction.RefreshWritableCalendars)
		}
	}

	val settingsAbout = listOf(
		SettingCategoryItem(
			title = stringResource(R.string.about),
			resId = R.drawable.ic_info,
			onClick = { onClickAbout() }
		),
		SettingCategoryItem(
			title = stringResource(R.string.support),
			resId = R.drawable.ic_support,
			onClick = {
				val repoUrl = Constants.GITHUB + "/snaptick#snaptick"
				openUrl(context, repoUrl)
			}
		),
		SettingCategoryItem(
			title = stringResource(R.string.check_for_updates),
			resId = R.drawable.ic_refresh,
			onClick = { onAction(MainAction.CheckForUpdates(ignoreThrottle = true)) }
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
		SettingCategoryItem(
			title = stringResource(R.string.sounds),
			resId = R.drawable.ic_clock,
			onClick = { showBottomSheetById = R.string.sounds }
		),
		SettingCategoryItem(
			title = stringResource(R.string.default_pomodoro_duration),
			resId = R.drawable.ic_timer,
			onClick = { showBottomSheetById = R.string.default_pomodoro_duration }
		),
		SettingCategoryItem(
			title = stringResource(R.string.add_widget),
			resId = R.drawable.ic_task_list,
			onClick = { requestPinWidget(context) }
		),
	)

	val settingsCalendar = listOf(
		SettingCategoryItem(
			title = stringResource(R.string.device_calendar),
			resId = R.drawable.ic_calendar_sync,
			onClick = { showBottomSheetById = R.string.calendar_sync }
		),
		SettingCategoryItem(
			title = stringResource(R.string.import_events),
			resId = R.drawable.ic_import,
			onClick = { showBottomSheetById = R.string.import_events }
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
									onAction(MainAction.UpdateDynamicTheme(it))
								},
								onSelect = {
									onAction(MainAction.UpdateAppTheme(it))
								})
						}

						R.string.language -> {
							LanguageOptionComponent(defaultLanguage = appState.language) {
								onAction(MainAction.UpdateLanguage(it))
							}
						}

						R.string.sleep_time -> {
							SleepTimeOptionComponent(defaultSleepTime = appState.sleepTime) {
								onAction(MainAction.UpdateSleepTime(it))
							}
						}

						R.string.time_picker -> {
							TimePickerOptionComponent(
								isWheelTimePicker = appState.isWheelTimePicker,
								is24HourTimeFormat = appState.is24hourTimeFormat,
								onSelect = {
									onAction(MainAction.UpdateTimePicker(it))
								},
								onSelectTimeFormat = {
									onAction(MainAction.UpdateTimeFormat(it))
								})
						}

						R.string.swipe_action -> {
							SwipeActionOptionComponent(
								selected = appState.swipeBehaviour,
								onSelect = {
									onAction(MainAction.UpdateSwipeBehaviour(it))
								})
						}

						R.string.sounds -> {
							SoundOptionComponent(
								enabled = appState.soundEnabled,
								onToggle = { onAction(MainAction.UpdateSoundEnabled(it)) },
							)
						}

						R.string.default_pomodoro_duration -> {
							PomodoroOptionComponent(
								selectedMins = appState.defaultPomodoroDuration,
								onSelect = { onAction(MainAction.UpdateDefaultPomodoroDuration(it)) },
							)
						}

						R.string.calendar_sync -> {
							CalendarSyncOptionComponent(
								enabled = appState.calendarSyncEnabled,
								selectedCalendarId = appState.calendarSyncCalendarId,
								writableCalendars = writableCalendars,
								onEnabledChange = { onAction(MainAction.SetCalendarSyncEnabled(it)) },
								onCalendarSelected = { onAction(MainAction.SetCalendarSyncTarget(it)) },
								onSyncAllNow = { onAction(MainAction.SyncAllTasksNow) },
							)
						}

						R.string.import_events -> {
							val activity = context as? MainActivity
							EventImportOptionComponent(
								previewTasks = appState.importPreview,
								onPickIcsFile = {
									activity?.icsPickerLauncher?.launch(
										Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
											addCategory(Intent.CATEGORY_OPENABLE)
											type = "*/*"
											putExtra(
												Intent.EXTRA_MIME_TYPES,
												arrayOf("text/calendar", "application/octet-stream")
											)
										}
									)
								},
								onImport = { picked -> onAction(MainAction.ImportTasks(picked)) },
								onClearPreview = { onAction(MainAction.ClearImportPreview) },
							)
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
				UpdateStatusComponent(
					checking = appState.updateCheckInFlight,
					failed = appState.updateCheckFailed,
					updateAvailable = appState.updateAvailable,
					lastCheckedAt = appState.lastUpdateCheckAt,
					onOpenUpdate = {
						appState.updateAvailable?.htmlUrl?.let { openUrl(context, it) }
						onAction(MainAction.DismissUpdateStatus)
					},
					onRetry = { onAction(MainAction.CheckForUpdates(ignoreThrottle = true)) },
					onDismiss = { onAction(MainAction.DismissUpdateStatus) },
				)
				SettingsCategoryComponent(
					categoryTitle = stringResource(R.string.general_settings),
					categoryList = settingsGeneral
				)
				SettingsCategoryComponent(
					categoryTitle = stringResource(R.string.calendar_sync),
					categoryList = settingsCalendar
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

private fun requestPinWidget(context: android.content.Context) {
	// requestPinAppWidget needs API 26+, which matches our minSdk.
	val appWidgetManager = AppWidgetManager.getInstance(context)
	if (!appWidgetManager.isRequestPinAppWidgetSupported) {
		showToast(context, context.getString(R.string.add_widget_unsupported), Toast.LENGTH_LONG)
		return
	}
	val provider = ComponentName(context, WidgetReceiver::class.java)
	val callbackFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
		PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
	} else {
		PendingIntent.FLAG_UPDATE_CURRENT
	}
	val callback = PendingIntent.getBroadcast(
		context,
		0,
		Intent(),
		callbackFlags,
	)
	appWidgetManager.requestPinAppWidget(provider, null, callback)
}