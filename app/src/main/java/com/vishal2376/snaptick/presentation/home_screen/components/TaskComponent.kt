package com.vishal2376.snaptick.presentation.home_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.taskDescTextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.Green
import com.vishal2376.snaptick.ui.theme.LightGray
import com.vishal2376.snaptick.ui.theme.Red

@Composable
fun TaskComponent(task: Task) {

	val randomColor = listOf(Green, Blue, Red).random()

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.background(
				randomColor, RoundedCornerShape(
					topStart = 8.dp, bottomStart = 8.dp, topEnd = 20.dp, bottomEnd = 20.dp
				)
			)
			.padding(start = 10.dp)
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.background(
					MaterialTheme.colorScheme.secondary, RoundedCornerShape(
						topEnd = 8.dp, bottomEnd = 8.dp
					)
				)
				.padding(8.dp, 16.dp)
		) {

			Row(
				modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {

				IconButton(
					onClick = { /*TODO*/ }, modifier = Modifier.size(32.dp)
				) {

					if (task.isCompleted) {
						Icon(
							painter = painterResource(id = R.drawable.ic_check_circle),
							contentDescription = null, tint = Green, modifier = Modifier.size(20.dp)
						)
					} else {
						Box(modifier = Modifier
							.size(20.dp)
							.border(width = 2.dp, color = LightGray, shape = CircleShape),
						    contentAlignment = Alignment.Center, content = {})
					}

				}

				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween
				) {

					Column(verticalArrangement = Arrangement.Center) {
						Text(text = task.title, style = taskTextStyle, color = Color.White)
						Spacer(modifier = Modifier.height(4.dp))
						Row(
							horizontalArrangement = Arrangement.spacedBy(4.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							Icon(
								painter = painterResource(id = R.drawable.ic_clock),
								contentDescription = null, modifier = Modifier.size(15.dp),
								tint = LightGray
							)
							Text(
								text = task.getFormattedTime(), style = taskDescTextStyle,
								color = LightGray
							)
						}
					}

					Icon(
						imageVector = Icons.Default.MoreVert, contentDescription = null,
						tint = Color.White
					)
				}
			}

		}

	}
}

@Preview
@Composable
fun TaskComponentPreview() {
	val task = Task(
		id = 0, title = "Drink Water", isCompleted = false, startTime = System.currentTimeMillis(),
		endTime = System.currentTimeMillis() + 3600000
	)
	TaskComponent(task = task)
}