package com.vishal2376.snaptick.widget.model

import com.vishal2376.snaptick.domain.model.Task

fun Task.toWidgetTask(): WidgetTaskModel = WidgetTaskModel(
	id = id,
	title = title,
	isCompleted = isCompleted,
	startTime = startTime,
	endTime = endTime,
	priority = priority
)