package com.vishal2376.snaptick.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.vishal2376.snaptick.MainActivity
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.common.utils.formatDurationTimestamp
import com.vishal2376.snaptick.service.PomodoroService.Companion.startForTask
import com.vishal2376.snaptick.util.SoundEvent
import com.vishal2376.snaptick.util.SettingsStore
import com.vishal2376.snaptick.util.playSound
import com.vishal2376.snaptick.util.vibrateDevice
import com.vishal2376.snaptick.widget.presentation.EXTRA_NAVIGATE_TO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val POMODORO_CHANNEL_ID = "snaptick-pomodoro"
private const val POMODORO_NOTIFICATION_ID = 9001
private const val PERSIST_INTERVAL_TICKS = 10

/**
 * Foreground service that owns the running pomodoro timer. Survives app
 * swipe-away by virtue of being a started + bound foreground service.
 *
 * Wire-up:
 * - PomodoroViewModel binds via [PomodoroBinder] when on-screen, reads
 *   [state] for live updates, and forwards user actions to togglePause /
 *   cancel / markCompleted.
 * - When no timer is active, calling [startForTask] kicks off a new session
 *   for the given taskId. Idempotent: starting the same task that's already
 *   running is a no-op; starting a different task while one is running
 *   replaces the previous timer.
 *
 * Cancel-with-prompt: if the user hits Cancel from the notification AND
 * elapsed time qualifies as a valid session (per Task.isValidPomodoroSession),
 * we open MainActivity with a flag asking the Pomodoro screen to show a
 * "save partial session?" modal. Otherwise the session is dropped silently.
 */
@AndroidEntryPoint
class PomodoroService : Service() {

	@Inject
	lateinit var repository: TaskRepository

	@Inject
	lateinit var settingsStore: SettingsStore

	private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
	private var tickerJob: Job? = null
	private var currentTask: Task? = null
	private var soundEnabled: Boolean = true

	private val _state = MutableStateFlow(ServiceTimerState())
	val state: StateFlow<ServiceTimerState> = _state.asStateFlow()

	private val binder = PomodoroBinder(this)

	override fun onBind(intent: Intent?): IBinder = binder

	override fun onCreate() {
		super.onCreate()
		scope.launch {
			settingsStore.soundEnabledKey.collect { soundEnabled = it }
		}
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		ensureChannel()
		// API 34+ requires the explicit FG type or the notification never appears.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
			startForeground(
				POMODORO_NOTIFICATION_ID,
				buildNotification(_state.value),
				ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
			)
		} else {
			startForeground(POMODORO_NOTIFICATION_ID, buildNotification(_state.value))
		}
		when (intent?.action) {
			ACTION_TOGGLE_PAUSE -> togglePause()
			ACTION_CANCEL -> cancelTimer()
			ACTION_CANCEL_FOR_TASK -> {
				val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)
				if (taskId > 0 && _state.value.taskId == taskId) cancelTimer()
				else if (_state.value.taskId <= 0) stopSelfQuiet()
			}

			ACTION_START_FOR_TASK -> {
				val taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)
				if (taskId > 0) startForTask(taskId)
			}
		}
		return START_STICKY
	}

	private fun stopSelfQuiet() {
		stopForeground(STOP_FOREGROUND_REMOVE)
		stopSelf()
	}

	fun startForTask(taskId: Int) {
		if (_state.value.taskId == taskId && _state.value.timeLeft > 0) return
		scope.launch {
			val task = repository.getTaskById(taskId) ?: return@launch
			// Clear the previous task's timer so it stops showing a partial session.
			val previous = currentTask
			if (previous != null && previous.id != task.id) {
				repository.updateTask(previous.copy(pomodoroTimer = -1))
			}
			currentTask = task
			runningTaskId = task.id
			val total = task.getDuration()
			val resuming = task.pomodoroTimer != -1
			val left = if (resuming) task.pomodoroTimer.toLong().coerceIn(0L, total) else total
			_state.value = ServiceTimerState(
				taskId = task.id,
				taskTitle = task.title,
				totalTime = total,
				timeLeft = left,
				isPaused = false,
				isCompleted = left <= 0L,
			)
			updateNotification()
			startTicker()
		}
	}

	fun togglePause() {
		_state.update { it.copy(isPaused = !it.isPaused) }
		updateNotification()
	}

	fun reset() {
		_state.update { it.copy(timeLeft = it.totalTime, isPaused = true, isCompleted = false) }
		updateNotification()
	}

	fun cancelTimer() {
		val task = currentTask
		val s = _state.value
		val shouldPromptSave =
			task != null && task.isValidPomodoroSession(s.timeLeft) && !s.isCompleted
		tickerJob?.cancel()
		runningTaskId = -1
		scope.launch {
			task?.let { repository.updateTask(it.copy(pomodoroTimer = -1)) }
			_state.value = ServiceTimerState()
			currentTask = null
			if (shouldPromptSave) {
				val open = Intent(applicationContext, MainActivity::class.java).apply {
					addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
					putExtra(EXTRA_PROMPT_SAVE_SESSION, true)
				}
				applicationContext.startActivity(open)
			}
			stopForeground(STOP_FOREGROUND_REMOVE)
			stopSelf()
		}
	}

	fun markCompleted() {
		val task = currentTask ?: return
		tickerJob?.cancel()
		scope.launch {
			repository.updateTask(task.copy(isCompleted = true, pomodoroTimer = -1))
			_state.update { it.copy(isCompleted = true, timeLeft = 0L) }
			updateNotification(showCompleted = true)
		}
	}

	private fun startTicker() {
		tickerJob?.cancel()
		tickerJob = scope.launch {
			var ticksSincePersist = 0
			while (isActive) {
				delay(1000L)
				val cur = _state.value
				if (cur.isPaused || cur.isCompleted || cur.timeLeft <= 0) continue
				val newLeft = cur.timeLeft - 1
				_state.update {
					it.copy(timeLeft = newLeft, isCompleted = newLeft <= 0)
				}
				ticksSincePersist++
				if (ticksSincePersist >= PERSIST_INTERVAL_TICKS) {
					ticksSincePersist = 0
					persistRemaining(newLeft)
				}
				if (newLeft <= 0) {
					vibrateDevice(applicationContext)
					updateNotification(showCompleted = true)
				} else {
					if (newLeft % 60 == 0L) {
						playSound(applicationContext, SoundEvent.POMODORO_TICK, soundEnabled)
					}
					updateNotification()
				}
			}
		}
	}

	private suspend fun persistRemaining(timeLeft: Long) {
		val task = currentTask ?: return
		val snapshot = if (task.isValidPomodoroSession(timeLeft))
			task.copy(pomodoroTimer = timeLeft.toInt())
		else
			task.copy(pomodoroTimer = -1)
		repository.updateTask(snapshot)
		currentTask = snapshot
	}

	private fun ensureChannel() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
		val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
		if (nm.getNotificationChannel(POMODORO_CHANNEL_ID) != null) return
		nm.createNotificationChannel(
			NotificationChannel(
				POMODORO_CHANNEL_ID,
				getString(R.string.pomodoro_channel_name),
				NotificationManager.IMPORTANCE_LOW,
			)
		)
	}

	private fun updateNotification(showCompleted: Boolean = false) {
		val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
		nm.notify(POMODORO_NOTIFICATION_ID, buildNotification(_state.value, showCompleted))
	}

	private fun buildNotification(
		s: ServiceTimerState,
		showCompleted: Boolean = false
	): android.app.Notification {
		val openIntent = PendingIntent.getActivity(
			this, 0,
			Intent(this, MainActivity::class.java).apply {
				addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
				putExtra(EXTRA_NAVIGATE_TO, "PomodoroScreen/${s.taskId}")
			},
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
		)
		val builder = NotificationCompat.Builder(this, POMODORO_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_notification)
			.setOngoing(true)
			.setOnlyAlertOnce(true)
			.setContentIntent(openIntent)
			.setContentTitle(s.taskTitle.ifBlank { "Pomodoro" })

		if (showCompleted || s.isCompleted) {
			builder.setContentText(getString(R.string.pomodoro_complete))
			builder.addAction(0, getString(R.string.mark_done), pendingAction(ACTION_MARK_DONE))
		} else {
			builder.setContentText(formatDurationTimestamp(s.timeLeft))
			val playPauseLabel =
				if (s.isPaused) getString(R.string.play) else getString(R.string.pause)
			builder.addAction(0, playPauseLabel, pendingAction(ACTION_TOGGLE_PAUSE))
			builder.addAction(0, getString(R.string.cancel), pendingAction(ACTION_CANCEL))
		}
		return builder.build()
	}

	private fun pendingAction(action: String): PendingIntent {
		val intent = Intent(this, PomodoroService::class.java).apply { this.action = action }
		return PendingIntent.getService(
			this, action.hashCode(), intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
		)
	}

	override fun onDestroy() {
		tickerJob?.cancel()
		runningTaskId = -1
		super.onDestroy()
	}

	companion object {
		const val ACTION_TOGGLE_PAUSE = "com.vishal2376.snaptick.action.TOGGLE_PAUSE"
		const val ACTION_CANCEL = "com.vishal2376.snaptick.action.CANCEL"
		const val ACTION_CANCEL_FOR_TASK = "com.vishal2376.snaptick.action.CANCEL_FOR_TASK"
		const val ACTION_START_FOR_TASK = "com.vishal2376.snaptick.action.START_FOR_TASK"
		const val ACTION_MARK_DONE = "com.vishal2376.snaptick.action.MARK_DONE"
		const val EXTRA_TASK_ID = "task_id"
		const val EXTRA_PROMPT_SAVE_SESSION = "prompt_save_session"

		/**
		 * Shared snapshot of which task currently owns the running timer. Lets
		 * other parts of the app (repository mutations, etc.) skip an Intent
		 * round-trip when no pomodoro is up at all. Reset by `startForTask`,
		 * `cancelTimer`, and `onDestroy`.
		 */
		@Volatile
		var runningTaskId: Int = -1
			private set

		fun startForTask(context: Context, taskId: Int) {
			val intent = Intent(context, PomodoroService::class.java).apply {
				action = ACTION_START_FOR_TASK
				putExtra(EXTRA_TASK_ID, taskId)
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				context.startForegroundService(intent)
			} else {
				context.startService(intent)
			}
		}

		// Stops the timer iff it belongs to taskId; no-op otherwise.
		fun stopIfRunningFor(context: Context, taskId: Int) {
			if (runningTaskId != taskId) return
			val intent = Intent(context, PomodoroService::class.java).apply {
				action = ACTION_CANCEL_FOR_TASK
				putExtra(EXTRA_TASK_ID, taskId)
			}
			runCatching {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					context.startForegroundService(intent)
				} else {
					context.startService(intent)
				}
			}
		}
	}
}

class PomodoroBinder(val service: PomodoroService) : Binder()

data class ServiceTimerState(
	val taskId: Int = -1,
	val taskTitle: String = "",
	val totalTime: Long = 0L,
	val timeLeft: Long = 0L,
	val isPaused: Boolean = false,
	val isCompleted: Boolean = false,
)
