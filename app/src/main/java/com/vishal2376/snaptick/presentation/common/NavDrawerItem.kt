package com.vishal2376.snaptick.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.vishal2376.snaptick.R

enum class NavDrawerItem(val stringId: Int, val icon: ImageVector) {
	REPORT_BUGS(R.string.report_bugs, Icons.Default.BugReport),
	SUGGESTIONS(R.string.suggestions, Icons.Default.Chat),
	RATE_US(R.string.rate_us, Icons.Default.Star),
	SHARE_APP(R.string.share_app, Icons.Default.Share)
}