package com.vishal2376.snaptick

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vishal2376.snaptick.presentation.common.CustomSnackBar
import com.vishal2376.snaptick.presentation.main.action.MainAction
import com.vishal2376.snaptick.presentation.main.viewmodel.MainViewModel
import com.vishal2376.snaptick.presentation.navigation.AppNavigation
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.util.BackupManager
import com.vishal2376.snaptick.util.NotificationHelper
import com.vishal2376.snaptick.widget.presentation.EXTRA_NAVIGATE_TO
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	private val mainViewModel by viewModels<MainViewModel>()

	@Inject
	lateinit var backupManager: BackupManager

	private lateinit var notificationHelper: NotificationHelper
	lateinit var backupPickerLauncher: ActivityResultLauncher<Intent>
	lateinit var restorePickerLauncher: ActivityResultLauncher<Intent>

	/** Navigation destination from widget intent */
	var widgetNavigateTo: String? = null
		private set

	override fun onCreate(savedInstanceState: Bundle?) {
		installSplashScreen()

		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		handleWidgetIntent(intent)

		notificationHelper = NotificationHelper(applicationContext)
		notificationHelper.createNotificationChannel()

		backupPickerLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
				if (result.resultCode == RESULT_OK) {
					result.data?.data?.let { uri: Uri ->
						mainViewModel.onAction(
							MainAction.CreateBackup(uri, mainViewModel.backupData.value)
						)
					}
				}
			}

		restorePickerLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
				if (result.resultCode == RESULT_OK) {
					result.data?.data?.let { uri: Uri ->
						mainViewModel.onAction(MainAction.LoadBackup(uri))
					}
				}
			}

		setContent {
			val mainState by mainViewModel.state.collectAsStateWithLifecycle()
			SnaptickTheme(
				theme = mainState.theme,
				dynamicColor = mainState.dynamicTheme
			) {
				AppNavigation(
					mainViewModel = mainViewModel,
					startDestination = widgetNavigateTo
				)
				CustomSnackBar()
			}
		}
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		handleWidgetIntent(intent)
	}

	private fun handleWidgetIntent(intent: Intent?) {
		widgetNavigateTo = intent?.getStringExtra(EXTRA_NAVIGATE_TO)
	}
}
