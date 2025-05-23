package com.vishal2376.snaptick.widget.presentation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.ui.theme.LightGray
import com.vishal2376.snaptick.ui.theme.Red
import com.vishal2376.snaptick.ui.theme.Yellow
import com.vishal2376.snaptick.widget.presentation.components.EmptyTaskWidgetComponent
import com.vishal2376.snaptick.widget.presentation.components.TaskWidgetComponent

@Composable
@GlanceComposable
fun SnaptickTaskWidget(tasks: List<Task>) {

	val context = LocalContext.current

	Box(
		modifier = GlanceModifier
			.fillMaxSize()
			.background(colorProvider = GlanceTheme.colors.background)
			.cornerRadius(20.dp)
			.padding(16.dp)
	) {
		if (tasks.isEmpty()) {
			EmptyTaskWidgetComponent { }
		} else {
			Column {
				Row(
					modifier = GlanceModifier.fillMaxWidth(),
					verticalAlignment = Alignment.Vertical.CenterVertically
				) {
					Text(
						text = context.getString(R.string.today_tasks),
						style = TextStyle(
							color = GlanceTheme.colors.onBackground,
							fontSize = 20.sp,
							fontWeight = FontWeight.Bold,
						),
						modifier = GlanceModifier.defaultWeight()
					)
					Row {
						CustomIconButton(R.drawable.ic_refresh) {
							// TODO: Refresh Widget
							Toast.makeText(context, "Widget Refreshed", Toast.LENGTH_SHORT).show()
						}
						Spacer(modifier = GlanceModifier.width(8.dp))
						CustomIconButton(
							icon = R.drawable.ic_add,
							tint = GlanceTheme.colors.onPrimary,
							bgColor = GlanceTheme.colors.primary,
						) {
							// TODO: Add new task
						}
					}
				}

				Spacer(modifier = GlanceModifier.height(16.dp))

				LazyColumn(modifier = GlanceModifier.fillMaxWidth()) {
					items(tasks) { task ->
						Column {
							TaskWidgetComponent(task, {})
							Spacer(modifier = GlanceModifier.height(8.dp))
						}
					}
				}
			}
		}
	}
}

@Composable
private fun CustomIconButton(
	icon: Int,
	tint: ColorProvider = GlanceTheme.colors.onBackground,
	bgColor: ColorProvider = GlanceTheme.colors.primaryContainer,
	onClick: () -> Unit,
) {
	Box(
		modifier = GlanceModifier.padding(8.dp)
			.background(bgColor)
			.cornerRadius(8.dp)
			.clickable(onClick),
	) {
		Image(
			provider = ImageProvider(icon),
			contentDescription = null,
			modifier = GlanceModifier.size(16.dp),
			colorFilter = ColorFilter.tint(tint)
		)
	}
}
