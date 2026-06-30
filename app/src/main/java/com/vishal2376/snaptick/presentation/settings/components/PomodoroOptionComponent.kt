package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.h3TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.SnaptickTheme

private val POMODORO_PRESET_MINS = listOf(5, 10, 15, 25, 30, 45, 60)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PomodoroOptionComponent(
    selectedMins: Int,
    onSelect: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SheetTitle(text = stringResource(R.string.choose_pomodoro_duration))
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            POMODORO_PRESET_MINS.forEach { mins ->
                PomodoroChip(
                    mins = mins,
                    selected = mins == selectedMins,
                    onClick = { onSelect(mins) }
                )
            }
        }
    }
}

@Composable
private fun PomodoroChip(
    mins: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val accent = MaterialTheme.colorScheme.primary
    val baseBg = MaterialTheme.colorScheme.primaryContainer
    val tintedBg = accent.copy(alpha = 0.12f).compositeOver(baseBg)

    val animatedBg by animateColorAsState(
        targetValue = if (selected) tintedBg else baseBg,
        animationSpec = tween(200),
        label = "chip-bg"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (selected) 1.06f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chip-scale"
    )
    val borderColor = if (selected) accent else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.20f)
    val borderWidth = if (selected) 2.dp else 1.dp

    Text(
        text = stringResource(R.string.minutes_format, mins),
        style = if (selected) h3TextStyle else taskTextStyle,
        color = if (selected) accent else MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier
            .graphicsLayer { scaleX = animatedScale; scaleY = animatedScale }
            .background(animatedBg, RoundedCornerShape(50))
            .border(borderWidth, borderColor, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Preview
@Composable
private fun PomodoroOptionComponentPreview() {
    SnaptickTheme {
        PomodoroOptionComponent(selectedMins = 25, onSelect = {})
    }
}
