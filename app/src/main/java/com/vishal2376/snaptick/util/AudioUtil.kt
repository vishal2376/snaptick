package com.vishal2376.snaptick.util

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import androidx.annotation.RawRes
import com.vishal2376.snaptick.R

private fun playMediaSound(context: Context, @RawRes soundResId: Int) {
	val mediaPlayer = MediaPlayer.create(context, soundResId)
	mediaPlayer?.apply {
		setOnCompletionListener {
			release()
		}
		start()
	}
}

fun playSound(context: Context, soundEvent: SoundEvent, enabled: Boolean = true) {
	if (!enabled) return
	when (soundEvent) {
		SoundEvent.POMODORO_TICK -> playMinuteTick()
		else -> {
			val soundResId = when (soundEvent) {
				SoundEvent.TASK_ADDED -> R.raw.task_added
				SoundEvent.TASK_COMPLETED -> R.raw.task_completed
				SoundEvent.TASK_DELETED -> R.raw.task_deleted
				SoundEvent.POMODORO_TICK -> return
			}
			playMediaSound(context, soundResId)
		}
	}
}

private fun playMinuteTick() {
	val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 40)
	toneGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 120)
	toneGen.release()
}

enum class SoundEvent {
	TASK_ADDED,
	TASK_COMPLETED,
	TASK_DELETED,
	POMODORO_TICK,
}