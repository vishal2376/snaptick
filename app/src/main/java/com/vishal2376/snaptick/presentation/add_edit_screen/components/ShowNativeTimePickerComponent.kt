package com.vishal2376.snaptick.presentation.add_edit_screen.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ShowNativeTimePicker(time: LocalTime, onClick: () -> Unit) {
	Box(modifier = Modifier.padding(vertical = 16.dp)) {
		Row(
			modifier = Modifier
				.clip(RoundedCornerShape(16.dp))
				.clickable { onClick() }
				.border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp))
				.padding(10.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Icon(
				imageVector = Icons.Default.AccessTime,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onPrimary,
				modifier = Modifier.size(24.dp)
			)
			val dtf = DateTimeFormatter.ofPattern("hh : mm a")
			Text(
				text = time.format(dtf),
				style = taskTextStyle,
				color = MaterialTheme.colorScheme.onPrimary
			)
		}
	}
}