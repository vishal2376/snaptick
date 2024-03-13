package com.vishal2376.snaptick.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.room.Room
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.data.local.TaskDatabase
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.ui.theme.DarkColorScheme
import com.vishal2376.snaptick.ui.theme.LightGray
import com.vishal2376.snaptick.ui.theme.White500
import java.time.LocalDate

class SnaptickWidget : GlanceAppWidget() {

	override suspend fun provideGlance(context: Context, id: GlanceId) {
		provideContent {
			GlanceTheme(ColorProviders(DarkColorScheme)) {
				WidgetContent()
			}
		}
	}

	@Composable
	private fun WidgetContent() {

		val context = LocalContext.current

		val repository = getTaskRepository(context)
		val todayTasks by repository.getTodayTasks().collectAsState(initial = emptyList())

		val dayOfWeek = LocalDate.now().dayOfWeek.value - 1
		val updatedTodayTasks = todayTasks.filter { task ->
			if (task.isRepeated) {
				task.getRepeatWeekList().contains(dayOfWeek)
			} else {
				true
			}
		}

		Column(
			modifier = GlanceModifier.fillMaxWidth()
				.background(ImageProvider(R.drawable.bg_round_primary))
				.padding(16.dp)
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
			LazyColumn {
				items(updatedTodayTasks) { task ->
					Column {
						TaskWidget(task)
						Spacer(modifier = GlanceModifier.height(8.dp))
					}
				}
			}
		}
	}

	@Composable
	private fun getTaskRepository(context: Context): TaskRepository {
		val db = Room.databaseBuilder(
			context.applicationContext,
			TaskDatabase::class.java,
			"local_db"
		).build()

		return TaskRepository(db.taskDao())
	}

	@Composable
	fun TaskWidget(task: Task) {

		val taskBackground =
			listOf(R.drawable.bg_task_low, R.drawable.bg_task_med, R.drawable.bg_task_high)

		val taskCheckImage =
			if (task.isCompleted) R.drawable.ic_check_circle else R.drawable.ic_uncheck_circle

		Column(
			modifier = GlanceModifier
				.fillMaxWidth()
				.background(ImageProvider(taskBackground[task.priority]))
				.padding(8.dp)
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				Image(
					provider = ImageProvider(taskCheckImage),
					contentDescription = null,
					modifier = GlanceModifier.size(35.dp).padding(8.dp)
				)

				Column {
					Text(
						text = task.title,
						style = TextStyle(
							color = ColorProvider(color = White500),
							fontSize = 16.sp,
						)
					)

					Spacer(modifier = GlanceModifier.height(8.dp))

					Row(verticalAlignment = Alignment.CenterVertically) {
						Image(
							provider = ImageProvider(R.drawable.ic_clock),
							contentDescription = null,
							modifier = GlanceModifier.size(15.dp)
						)
						Spacer(modifier = GlanceModifier.width(4.dp))
						Text(
							text = task.getFormattedTime(),
							style = TextStyle(
								color = ColorProvider(color = LightGray),
								fontSize = 12.sp
							)
						)
					}
				}
			}
		}
	}
}