package com.vishal2376.snaptick.data.repositories

import android.content.Context
import com.vishal2376.snaptick.data.calendar.CalendarPusher
import com.vishal2376.snaptick.data.local.TaskCompletion
import com.vishal2376.snaptick.data.local.TaskCompletionDao
import com.vishal2376.snaptick.data.local.TaskDao
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.util.ReminderScheduler
import com.vishal2376.snaptick.widget.worker.WidgetUpdateWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate

class TaskRepository(
	private val dao: TaskDao,
	private val completionDao: TaskCompletionDao,
	private val context: Context,
	private val calendarPusher: CalendarPusher,
	private val reminderScheduler: ReminderScheduler,
) {
	suspend fun insertTask(task: Task) {
		dao.insertTask(task)
		val saved = dao.getTaskByUuid(task.uuid) ?: task
		reminderScheduler.schedule(saved)
		calendarPusher.pushInsert(saved)
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun deleteTask(task: Task) {
		reminderScheduler.cancel(task.id)
		completionDao.deleteAllForTask(task.uuid)
		dao.deleteTask(task)
		calendarPusher.pushDelete(task)
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun updateTask(task: Task) {
		reminderScheduler.cancel(task.id)
		dao.updateTask(task)
		reminderScheduler.schedule(task)
		calendarPusher.pushUpdate(task)
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun getTaskById(id: Int): Task? {
		return dao.getTaskById(id)
	}

	suspend fun deleteAllTasks() {
		dao.deleteAllTasks()
		WidgetUpdateWorker.enqueueWorker(context)
	}

	fun getTasksByDate(selectedDate: LocalDate): Flow<List<Task>> {
		return dao.getTasksByDate(selectedDate.toString())
	}

	fun getTodayTasks(): Flow<List<Task>> {
		return dao.getTasksByDate(LocalDate.now().toString())
	}

	/**
	 * Today's tasks with per-date completion state merged in. Repeat-template
	 * tasks have their `isCompleted` flag flipped to `true` when a row exists
	 * in `task_completions` for `(uuid, today)`. One-off tasks pass through
	 * unchanged.
	 */
	fun getTodayTasksWithCompletions(): Flow<List<Task>> {
		val today = LocalDate.now().toString()
		return combine(
			dao.getTasksByDate(today),
			completionDao.completedUuidsOn(today),
		) { tasks, completedUuids ->
			val completedSet = completedUuids.toHashSet()
			tasks.map { task ->
				if (task.isRepeated && task.uuid in completedSet) task.copy(isCompleted = true)
				else task
			}
		}
	}

	fun getLastRepeatedTasks(): List<Task> {
		val today = LocalDate.now().toString()
		return dao.getLastRepeatedTasks(today)
	}

	fun getAllTasks(): Flow<List<Task>> {
		return dao.getAllTasks().onEach {
			WidgetUpdateWorker.enqueueWorker(context)
		}
	}

	suspend fun getAllTasksSnapshot(): List<Task> = dao.getAllTasksSnapshot()

	/**
	 * Records a completion for the given repeating task on the given date.
	 * One-off task completion still flows through `updateTask`.
	 */
	suspend fun markCompletedForDate(uuid: String, date: LocalDate) {
		completionDao.insert(TaskCompletion(uuid = uuid, date = date.toString()))
	}

	suspend fun unmarkCompletedForDate(uuid: String, date: LocalDate) {
		completionDao.delete(uuid, date.toString())
	}

	suspend fun isCompletedOn(uuid: String, date: LocalDate): Boolean {
		return completionDao.isCompleted(uuid, date.toString())
	}

	/** Pushes every task that doesn't yet have a calendar event id to the
	 *  selected device calendar. Cheap no-op when sync is disabled. */
	suspend fun syncAllTasksNow() {
		val all = dao.getAllTasksSnapshot()
		calendarPusher.pushAllUnmirrored(all)
	}

	/**
	 * Removes every device-calendar event Snaptick has ever pushed and clears
	 * each task's `calendarEventId`. Called when the user turns calendar sync
	 * off so previously-mirrored events don't keep living in their Google
	 * Calendar. Returns the number of events actually deleted.
	 */
	suspend fun deletePushedCalendarEvents(): Int {
		val all = dao.getAllTasksSnapshot()
		return calendarPusher.deleteAllPushedEvents(all)
	}

	/** Used by the boot-recovery worker to re-arm every active reminder. */
	suspend fun rescheduleAllReminders() {
		val all = dao.getAllTasksSnapshot().filter { it.reminder && !it.isCompleted }
		reminderScheduler.rescheduleAll(all)
	}
}
