package com.vishal2376.snaptick.widget.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.ui.theme.White500
import com.vishal2376.snaptick.widget.model.WidgetTaskModel

@Composable
@GlanceComposable
fun WidgetTasks(
	tasks: List<WidgetTaskModel>,
	onTaskClick: (Int) -> Action,
	modifier: GlanceModifier = GlanceModifier
) {
	val context = LocalContext.current

	Column(
		modifier = modifier
	) {
		Text(
			text = context.getString(R.string.today_tasks),
			style = TextStyle(
				color = ColorProvider(color = White500),
				fontSize = 20.sp,
				fontWeight = FontWeight.Bold,
			)
		)
		Spacer(modifier = GlanceModifier.height(16.dp))

		when {
			tasks.isEmpty() -> WidgetNoTasks(modifier = GlanceModifier.fillMaxSize())

			else -> LazyColumn(modifier = GlanceModifier.defaultWeight()) {
				items(
					items = tasks,
					itemId = { task -> task.id.toLong() },
				) { task ->
					Column {
						WidgetTaskComponent(
							task = task,
							onClick = onTaskClick(task.id),
							modifier = GlanceModifier
								.fillMaxWidth()
						)
						Spacer(modifier = GlanceModifier.height(4.dp))
					}
				}
			}
		}
	}
}