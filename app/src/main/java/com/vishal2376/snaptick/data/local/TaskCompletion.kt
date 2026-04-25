package com.vishal2376.snaptick.data.local

import androidx.room.Entity

/**
 * Per-occurrence completion of a repeating task. The (`uuid`, `date`) pair
 * is the natural key: a single repeat-template task can be completed on many
 * dates, but at most once per date.
 *
 * One-off tasks do NOT use this table. Their completion stays on the
 * `Task.isCompleted` column. Only repeat templates flow through here so the
 * template row itself never mutates per-day.
 */
@Entity(
	tableName = "task_completions",
	primaryKeys = ["uuid", "date"]
)
data class TaskCompletion(
	val uuid: String,
	/** ISO-8601 LocalDate (yyyy-MM-dd). */
	val date: String,
)
