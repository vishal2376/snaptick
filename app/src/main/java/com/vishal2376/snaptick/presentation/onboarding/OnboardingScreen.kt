package com.vishal2376.snaptick.presentation.onboarding

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.drop
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.main.action.MainAction
import com.vishal2376.snaptick.presentation.main.state.MainState
import com.vishal2376.snaptick.presentation.onboarding.pages.RestoreAndSyncPage
import com.vishal2376.snaptick.presentation.onboarding.pages.ThemePreviewPage
import com.vishal2376.snaptick.presentation.onboarding.pages.WelcomePage
import kotlinx.coroutines.launch

private const val TOTAL_PAGES = 3

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
	state: MainState,
	onAction: (MainAction) -> Unit,
	onRestoreBackup: () -> Unit,
	onPickIcsFile: () -> Unit,
	onToggleCalendarSync: (Boolean) -> Unit,
	onFinish: () -> Unit,
) {
	val pagerState = rememberPagerState(pageCount = { TOTAL_PAGES })
	val scope = rememberCoroutineScope()
	val haptic = LocalHapticFeedback.current

	LaunchedEffect(pagerState) {
		snapshotFlow { pagerState.currentPage }
			.drop(1)
			.collect { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
	) {
		// Skip button
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp, vertical = 12.dp),
			horizontalArrangement = Arrangement.End
		) {
			TextButton(onClick = onFinish) {
				Text(
					text = "Skip",
					style = taskTextStyle,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
		}

		HorizontalPager(
			state = pagerState,
			modifier = Modifier.weight(1f)
		) { page ->
			when (page) {
				0 -> WelcomePage(currentTheme = state.theme)
				1 -> ThemePreviewPage(
					selectedTheme = state.theme,
					onThemeSelected = { onAction(MainAction.UpdateAppTheme(it)) }
				)
				2 -> RestoreAndSyncPage(
					calendarSyncEnabled = state.calendarSyncEnabled,
					onRestoreClick = onRestoreBackup,
					onPickIcsClick = onPickIcsFile,
					onCalendarSyncToggle = onToggleCalendarSync
				)
			}
		}

		// Page indicators
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 16.dp),
			horizontalArrangement = Arrangement.Center
		) {
			repeat(TOTAL_PAGES) { index ->
				val selected = pagerState.currentPage == index
				val width by animateDpAsState(
					targetValue = if (selected) 24.dp else 8.dp,
					animationSpec = spring(
						dampingRatio = Spring.DampingRatioMediumBouncy,
						stiffness = Spring.StiffnessMedium
					),
					label = "indicator"
				)
				Box(
					modifier = Modifier
						.padding(horizontal = 4.dp)
						.size(width = width, height = 8.dp)
						.background(
							color = if (selected) MaterialTheme.colorScheme.primary
							else MaterialTheme.colorScheme.primaryContainer,
							shape = CircleShape
						)
				)
			}
		}

		// CTA
		Button(
			onClick = {
				if (pagerState.currentPage < TOTAL_PAGES - 1) {
					scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
				} else {
					onFinish()
				}
			},
			modifier = Modifier
				.fillMaxWidth()
				.padding(24.dp),
			colors = ButtonDefaults.buttonColors(
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = MaterialTheme.colorScheme.onPrimary
			),
			shape = RoundedCornerShape(16.dp)
		) {
			Text(
				text = if (pagerState.currentPage < TOTAL_PAGES - 1) "Next" else "Get started",
				style = taskTextStyle,
				modifier = Modifier.padding(6.dp)
			)
		}
		Spacer(Modifier.height(8.dp))
	}
}
