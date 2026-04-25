package com.vishal2376.snaptick.presentation.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
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
	notificationsEnabled: Boolean,
	onEnableNotifications: () -> Unit,
	onFinish: () -> Unit,
) {
	val pagerState = rememberPagerState(pageCount = { TOTAL_PAGES })
	val scope = rememberCoroutineScope()
	val haptic = LocalHapticFeedback.current

	LaunchedEffect(pagerState) {
		snapshotFlow { pagerState.currentPage }
			.drop(1)
			.collect {
				haptic.performHapticFeedback(HapticFeedbackType.LongPress)
			}
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
					notificationsEnabled = notificationsEnabled,
					onRestoreClick = onRestoreBackup,
					onPickIcsClick = onPickIcsFile,
					onCalendarSyncToggle = onToggleCalendarSync,
					onEnableNotifications = onEnableNotifications,
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

		// CTA - always visible
		val isLastPage = pagerState.currentPage == TOTAL_PAGES - 1
		val view = LocalView.current

		val shimmerTransition = rememberInfiniteTransition(label = "cta-shimmer")
		val shimmerProgress by shimmerTransition.animateFloat(
			initialValue = -0.4f,
			targetValue = 1.4f,
			animationSpec = infiniteRepeatable(
				animation = tween(durationMillis = 1800, easing = LinearEasing),
				repeatMode = RepeatMode.Restart
			),
			label = "cta-shimmer-progress"
		)

		Button(
			onClick = {
				if (isLastPage) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
						view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
					} else {
						haptic.performHapticFeedback(HapticFeedbackType.LongPress)
					}
					onFinish()
				} else {
					haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
					scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
				}
			},
			modifier = Modifier
				.fillMaxWidth()
				.padding(24.dp)
				.drawWithCache {
					onDrawWithContent {
						drawContent()
						if (isLastPage) {
							val sweep = size.width * shimmerProgress
							val brush = Brush.linearGradient(
								colors = listOf(
									Color.Transparent,
									Color.White.copy(alpha = 0.32f),
									Color.Transparent
								),
								start = Offset(sweep - size.width * 0.25f, 0f),
								end = Offset(sweep + size.width * 0.25f, size.height)
							)
							drawRect(brush)
						}
					}
				},
			colors = ButtonDefaults.buttonColors(
				containerColor = MaterialTheme.colorScheme.primary,
				contentColor = MaterialTheme.colorScheme.onPrimary
			),
			shape = RoundedCornerShape(16.dp)
		) {
			Text(
				text = if (isLastPage) "Get started" else "Next",
				style = taskTextStyle,
				modifier = Modifier.padding(6.dp)
			)
		}
		Spacer(Modifier.height(8.dp))
	}
}
