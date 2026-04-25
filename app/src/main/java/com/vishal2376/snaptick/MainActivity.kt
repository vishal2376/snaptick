package com.vishal2376.snaptick

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vishal2376.snaptick.presentation.common.CustomSnackBar
import com.vishal2376.snaptick.presentation.main.action.MainAction
import com.vishal2376.snaptick.presentation.main.viewmodel.MainViewModel
import com.vishal2376.snaptick.presentation.navigation.AppNavigation
import com.vishal2376.snaptick.presentation.navigation.Routes
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import com.vishal2376.snaptick.util.BackupManager
import com.vishal2376.snaptick.util.NotificationHelper
import com.vishal2376.snaptick.util.SplashThemeMirror
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
	lateinit var calendarPermissionLauncher: ActivityResultLauncher<Array<String>>
	lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
	lateinit var icsPickerLauncher: ActivityResultLauncher<Intent>

	/** Navigation destination from widget intent */
	var widgetNavigateTo: String? = null
		private set

	/** Most recently picked `.ics` file URI; consumed by the import sheet. */
	var lastPickedIcsUri: Uri? = null

	/** When true, next .ics pick auto-imports all events instead of showing preview. */
	var pendingIcsAutoImport: Boolean = false

	/** Compose-observable notification-permission state. */
	val notificationGrantedState = mutableStateOf(false)

	override fun onCreate(savedInstanceState: Bundle?) {
		setTheme(SplashThemeMirror.startingThemeRes(this))
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

		calendarPermissionLauncher =
			registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
				val granted = grants.values.all { it }
				if (granted) {
					mainViewModel.onAction(MainAction.SetCalendarSyncEnabled(true))
				}
			}

		notificationPermissionLauncher =
			registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
				notificationGrantedState.value = granted
			}

		notificationGrantedState.value =
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
						PackageManager.PERMISSION_GRANTED
			} else true

		icsPickerLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
				if (result.resultCode == RESULT_OK) {
					result.data?.data?.let { uri: Uri ->
						lastPickedIcsUri = uri
						if (pendingIcsAutoImport) {
							pendingIcsAutoImport = false
							mainViewModel.onAction(MainAction.ImportIcsFile(uri))
						} else {
							mainViewModel.onAction(MainAction.ParseIcsFile(uri))
						}
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
		val raw = intent?.getStringExtra(EXTRA_NAVIGATE_TO)
		// Only the routes the widget is supposed to be able to deeplink into.
		// Anything else (including null) leaves widgetNavigateTo unset so
		// AppNavigation falls through to its default start destination.
		// Without this allowlist, any app on the device could craft an
		// `Intent(MAIN).putExtra("navigate_to", ...)` and either crash NavHost
		// (unknown route) or open a screen that the widget surface was never
		// meant to expose.
		widgetNavigateTo = if (raw != null && raw in WIDGET_ALLOWED_ROUTES) raw else null
	}

	companion object {
		private val WIDGET_ALLOWED_ROUTES: Set<String> = setOf(
			Routes.AddTaskScreen.name,
		)
	}
}
