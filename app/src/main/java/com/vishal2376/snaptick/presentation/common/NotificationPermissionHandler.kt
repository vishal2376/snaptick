package com.vishal2376.snaptick.presentation.common

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun NotificationPermissionHandler(
	onPermissionGranted: () -> Unit,
	onPermissionDenied: () -> Unit
) {
	val context = LocalContext.current

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission()
	) { isGranted ->
		if (isGranted) {
			onPermissionGranted()
		} else {
			onPermissionDenied()
		}
	}

	// Check the current permission state
	LaunchedEffect(Unit) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			val isGranted =
				context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

			if (!isGranted) {
				launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
			} else {
				onPermissionGranted()
			}
		} else {
			onPermissionGranted()
		}
	}
}
