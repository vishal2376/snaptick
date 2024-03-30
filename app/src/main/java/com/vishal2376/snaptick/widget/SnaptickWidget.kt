package com.vishal2376.snaptick.widget

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.widget.components.SnaptickWidgetTheme
import com.vishal2376.snaptick.widget.components.WidgetTasks
import com.vishal2376.snaptick.widget.model.WidgetTaskModel

val parameterTaskId = ActionParameters.Key<Int>("task_id")

object SnaptickWidget : GlanceAppWidget() {

	override val stateDefinition: GlanceStateDefinition<List<WidgetTaskModel>>
		get() = SnaptickWidgetState


	override suspend fun provideGlance(context: Context, id: GlanceId) {

		provideContent {

			val tasks = currentState<List<WidgetTaskModel>>()

			SnaptickWidgetTheme {
				WidgetTasks(
					tasks = tasks,
					onTaskClick = { taskId ->
						actionRunCallback<OnTaskClickedCallback>(
							parameters = actionParametersOf(parameterTaskId to taskId)
						)
					},
					modifier = GlanceModifier
						.appWidgetBackground()
						.fillMaxSize()
						.background(ImageProvider(R.drawable.bg_round_primary))
						.padding(16.dp)
				)
			}
		}
	}
}
