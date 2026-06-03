package com.vishal2376.snaptick.data.repositories

import android.content.Context
import androidx.room.withTransaction
import com.vishal2376.snaptick.data.calendar.CalendarPusher
import com.vishal2376.snaptick.data.local.TaskCompletion
import com.vishal2376.snaptick.data.local.TaskCompletionDao
import com.vishal2376.snaptick.data.local.TaskDao
import com.vishal2376.snaptick.data.local.TaskDatabase
import com.vishal2376.snaptick.data.local.TaskReminder
import com.vishal2376.snaptick.data.local.TaskReminderDao
import com.vishal2376.snaptick.domain.model.BackupCompletion
import com.vishal2376.snaptick.domain.model.BackupData
import com.vishal2376.snaptick.domain.model.BackupReminder
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.service.PomodoroService
import com.vishal2376.snaptick.util.ReminderScheduler
import com.vishal2376.snaptick.widget.worker.WidgetUpdateWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate

class TaskRepository(
	private val dao: TaskDao,
	private val completionDao: TaskCompletionDao,
	private val reminderDao: TaskReminderDao,
	private val database: TaskDatabase,
	private val context: Context,
	private val calendarPusher: CalendarPusher,
	private val reminderScheduler: ReminderScheduler,
) {
	suspend fun insertTask(task: Task, reminderOffsets: List<Int> = defaultOffsets(task)) {
		dao.insertTask(task)
		val saved = dao.getTaskByUuid(task.uuid) ?: task
		writeReminderOffsets(saved.uuid, reminderOffsets)
		reminderScheduler.schedule(saved, offsets = reminderOffsets)
		calendarPusher.pushInsert(saved)
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun deleteTask(task: Task) {
		reminderScheduler.cancel(task.id)
		PomodoroService.stopIfRunningFor(context, task.id)
		completionDao.deleteAllForTask(task.uuid)
		reminderDao.deleteAllForTask(task.uuid)
		dao.deleteTask(task)
		calendarPusher.pushDelete(task)
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun updateTask(task: Task, reminderOffsets: List<Int>? = null) {
		reminderScheduler.cancel(task.id)
		if (task.isCompleted) PomodoroService.stopIfRunningFor(context, task.id)
		dao.updateTask(task)
		val effective = reminderOffsets ?: reminderDao.offsetsForTask(task.uuid)
			.ifEmpty { defaultOffsets(task) }
		if (reminderOffsets != null) writeReminderOffsets(task.uuid, reminderOffsets)
		reminderScheduler.schedule(task, offsets = effective)
		calendarPusher.pushUpdate(task)
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun getReminderOffsets(uuid: String): List<Int> = reminderDao.offsetsForTask(uuid)

	private suspend fun writeReminderOffsets(uuid: String, offsets: List<Int>) {
		reminderDao.deleteAllForTask(uuid)
		if (offsets.isNotEmpty()) {
			reminderDao.insertAll(offsets.map { TaskReminder(uuid = uuid, offsetMinutes = it) })
		}
	}

	private fun defaultOffsets(task: Task): List<Int> =
		if (task.reminder) listOf(0) else emptyList()

	suspend fun getTaskById(id: Int): Task? {
		return dao.getTaskById(id)
	}

	suspend fun deleteAllTasks() {
		val active = PomodoroService.runningTaskId
		if (active > 0) PomodoroService.stopIfRunningFor(context, active)
		dao.deleteAllTasks()
		WidgetUpdateWorker.enqueueWorker(context)
	}

	fun getTasksByDate(selectedDate: LocalDate): Flow<List<Task>> {
		return dao.getTasksByDate(selectedDate.toString())
	}

	fun getTodayTasks(): Flow<List<Task>> {
		return dao.getTasksByDate(LocalDate.now().toString())
	}

	// Union of one-off + active repeats with task_completions merged for today.
	fun getTodayTasksWithCompletions(): Flow<List<Task>> {
		val today = LocalDate.now()
		val todayIso = today.toString()
		return combine(
			dao.getTasksByDate(todayIso),
			dao.getActiveRepeats(todayIso),
			completionDao.completedUuidsOn(todayIso),
		) { dated, repeats, completedUuids ->
			val completedSet = completedUuids.toHashSet()
			val seen = HashSet<Int>()
			val out = ArrayList<Task>(dated.size + repeats.size)
			fun mergeCompletion(task: Task): Task =
				if (task.isRepeated && task.uuid in completedSet) task.copy(isCompleted = true) else task
			// Repeating task with date == today appears in both queries; merge completion
			// in both loops so the dated branch doesn't shadow the task_completions row.
			for (task in dated) {
				if (seen.add(task.id) && task.shouldOccurOn(today)) out += mergeCompletion(task)
			}
			for (task in repeats) {
				if (task.id in seen) continue
				if (!task.shouldOccurOn(today)) continue
				out += mergeCompletion(task)
				seen += task.id
			}
			out
		}
	}

	fun getAllTasks(): Flow<List<Task>> {
		return dao.getAllTasks().onEach {
			WidgetUpdateWorker.enqueueWorker(context)
		}
	}

	suspend fun getAllTasksSnapshot(): List<Task> = dao.getAllTasksSnapshot()

	suspend fun snapshotBackup(): BackupData {
		val tasks = dao.getAllTasksSnapshot()
		val completions = completionDao.getAllSnapshot()
			.map { BackupCompletion(uuid = it.uuid, date = it.date) }
		val reminders = reminderDao.getAllSnapshot()
			.map { BackupReminder(uuid = it.uuid, offsetMinutes = it.offsetMinutes) }
		return BackupData(tasks = tasks, completions = completions, reminders = reminders)
	}

	// Wipe + insert inside one transaction so a mid-restore failure rolls back.
	suspend fun restoreFromBackup(data: BackupData) {
		database.withTransaction {
			dao.deleteAllTasks()
			completionDao.deleteAll()
			reminderDao.deleteAll()
			for (task in data.tasks) dao.insertTask(task)
			if (data.completions.isNotEmpty()) {
				completionDao.insertAll(
					data.completions.map { TaskCompletion(uuid = it.uuid, date = it.date) }
				)
			}
			val explicitReminders = data.reminders.map {
				TaskReminder(uuid = it.uuid, offsetMinutes = it.offsetMinutes)
			}
			val backfilled = if (explicitReminders.isEmpty()) {
				// v1 backups had no reminders payload; recreate (uuid, 0) for on-time behavior.
				data.tasks.filter { it.reminder }
					.map { TaskReminder(uuid = it.uuid, offsetMinutes = 0) }
			} else {
				explicitReminders
			}
			if (backfilled.isNotEmpty()) reminderDao.insertAll(backfilled)
		}
		val saved = dao.getAllTasksSnapshot()
		reminderScheduler.rescheduleAll(saved.map { task ->
			task to reminderDao.offsetsForTask(task.uuid)
		})
		WidgetUpdateWorker.enqueueWorker(context)
	}

	// Re-arms reminder with skipToday so today's alarm doesn't refire.
	suspend fun markCompletedForDate(uuid: String, date: LocalDate) {
		completionDao.insert(TaskCompletion(uuid = uuid, date = date.toString()))
		dao.getTaskByUuid(uuid)?.let { task ->
			if (date == LocalDate.now()) {
				PomodoroService.stopIfRunningFor(context, task.id)
			}
			val offsets = reminderDao.offsetsForTask(uuid)
			reminderScheduler.cancel(task.id)
			reminderScheduler.schedule(task, offsets = offsets, skipToday = date == LocalDate.now())
		}
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun unmarkCompletedForDate(uuid: String, date: LocalDate) {
		completionDao.delete(uuid, date.toString())
		dao.getTaskByUuid(uuid)?.let { task ->
			val offsets = reminderDao.offsetsForTask(uuid)
			reminderScheduler.cancel(task.id)
			reminderScheduler.schedule(task, offsets = offsets)
		}
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun isCompletedOn(uuid: String, date: LocalDate): Boolean {
		return completionDao.isCompleted(uuid, date.toString())
	}

	suspend fun syncAllTasksNow() {
		val all = dao.getAllTasksSnapshot()
		calendarPusher.pushAllUnmirrored(all)
	}

	suspend fun deletePushedCalendarEvents(): Int {
		val all = dao.getAllTasksSnapshot()
		return calendarPusher.deleteAllPushedEvents(all)
	}

	suspend fun rescheduleAllReminders() {
		val all = dao.getAllTasksSnapshot().filter { it.reminder && !it.isCompleted }
		reminderScheduler.rescheduleAll(
			all.map { task ->
				val stored = reminderDao.offsetsForTask(task.uuid)
				task to stored.ifEmpty { defaultOffsets(task) }
			}
		)
	}
}
