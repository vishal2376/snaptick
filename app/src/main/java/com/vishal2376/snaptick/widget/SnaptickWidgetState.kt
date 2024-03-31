package com.vishal2376.snaptick.widget

import com.google.gson.annotations.SerializedName
import com.vishal2376.snaptick.widget.model.WidgetTaskModel

/**
 * [SnaptickWidgetState] presents the widget state,
 * @param tasks [List] of [WidgetTaskModel] the actual tasks to be shown in the widget
 */
data class SnaptickWidgetState(
	@SerializedName("tasks")
	val tasks: List<WidgetTaskModel> = emptyList()
)
