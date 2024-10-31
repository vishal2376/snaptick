package com.vishal2376.snaptick

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.vishal2376.snaptick.presentation.common.CustomSnackBar
import com.vishal2376.snaptick.presentation.navigation.AppNavigation
import com.vishal2376.snaptick.presentation.viewmodels.TaskViewModel
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	private val taskViewModel by viewModels<TaskViewModel>()
	private lateinit var notificationHelper: NotificationHelper
	lateinit var backupPickerLauncher: ActivityResultLauncher<Intent>
	lateinit var restorePickerLauncher: ActivityResultLauncher<Intent>


	override fun onCreate(savedInstanceState: Bundle?) {
		// init splash screen
		installSplashScreen()

		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		// create notification channel
		notificationHelper = NotificationHelper(applicationContext)
		notificationHelper.createNotificationChannel()

		// load app state
		taskViewModel.loadAppState(applicationContext)

		// Launchers for backup and restore
		backupPickerLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
				if (result.resultCode == RESULT_OK) {
					result.data?.data?.let { uri ->
						taskViewModel.createBackup(
							uri,
							taskViewModel.backupData.value,
							applicationContext
						)
					}
				}
			}

		restorePickerLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
				if (result.resultCode == RESULT_OK) {
					result.data?.data?.let { uri ->
						taskViewModel.loadBackup(uri, applicationContext)
					}
				}
			}

		setContent {
			SnaptickTheme(theme = taskViewModel.appState.theme) {
				AppNavigation(taskViewModel = taskViewModel)
				CustomSnackBar()
			}
		}
	}
}