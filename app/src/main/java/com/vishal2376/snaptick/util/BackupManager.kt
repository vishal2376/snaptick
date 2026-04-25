package com.vishal2376.snaptick.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.vishal2376.snaptick.domain.model.BackupData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "BackupManager"

@Singleton
class BackupManager @Inject constructor(
	@ApplicationContext val context: Context
) {
	private val timestamp = LocalDate.now()
	private val backupFileName = "Snaptick_Backup_$timestamp.json"
	private val gson: Gson = GsonBuilder()
		.registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
		.registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
		.create()

	suspend fun createBackup(uri: Uri, data: BackupData): Boolean {
		return withContext(Dispatchers.IO) {
			try {
				context.contentResolver.openOutputStream(uri)?.use { outputStream ->
					val jsonData = gson.toJson(data)
					outputStream.write(jsonData.toByteArray())
				}
				true
			} catch (e: Exception) {
				Log.e(TAG, "Backup write failed", e)
				false
			}
		}
	}

	suspend fun loadBackup(uri: Uri): BackupData? {
		return withContext(Dispatchers.IO) {
			try {
				context.contentResolver.openInputStream(uri)?.use { inputStream ->
					// Strict (non-lenient) reader rejects malformed JSON early
					// instead of silently swallowing comments, single quotes,
					// trailing commas etc. Combined with MainViewModel's task
					// count cap, this bounds the parser's blast radius.
					val reader = JsonReader(inputStream.bufferedReader())
					reader.isLenient = false
					reader.use { gson.fromJson<BackupData>(it, BackupData::class.java) }
				}
			} catch (e: Exception) {
				Log.e(TAG, "Backup read failed", e)
				null
			}
		}
	}

	fun getBackupFilePickerIntent(): Intent {
		val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
			addCategory(Intent.CATEGORY_OPENABLE)
			type = "application/json"
			putExtra(Intent.EXTRA_TITLE, backupFileName)
		}
		return intent
	}

	fun getLoadBackupFilePickerIntent(): Intent {
		val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
			addCategory(Intent.CATEGORY_OPENABLE)
			type = "application/json"
		}
		return intent
	}
}
