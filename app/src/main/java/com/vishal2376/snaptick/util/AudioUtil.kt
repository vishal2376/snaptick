package com.vishal2376.snaptick.util

import android.content.Context
import android.media.MediaPlayer
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

fun playSound(context: Context, soundEvent: SoundEvent) {
    val soundResId = when (soundEvent) {
	    SoundEvent.TASK_ADDED -> R.raw.task_added
	    SoundEvent.TASK_COMPLETED -> R.raw.task_completed
	    SoundEvent.TASK_DELETED -> R.raw.task_deleted
    }
    playMediaSound(context, soundResId)
}

enum class SoundEvent {
	TASK_ADDED,
	TASK_COMPLETED,
	TASK_DELETED,
}