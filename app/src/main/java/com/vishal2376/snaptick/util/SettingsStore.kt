package com.vishal2376.snaptick.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.SETTINGS_KEY)

class SettingsStore(val context: Context) {

	companion object {
		private const val DEFAULT_THEME = 0
		private const val DEFAULT_SORT_TASK = 0
		private const val DEFAULT_LAST_OPENED = ""
		private const val DEFAULT_STREAK = 0
	}

	private val THEME_KEY = intPreferencesKey("theme_key")
	private val SORT_TASK_KEY = intPreferencesKey("sort_task_key")
	private val LAST_OPENED_KEY = stringPreferencesKey("last_opened_key")
	private val STREAK_KEY = intPreferencesKey("streak_key")

	fun getTheme(): Flow<Int> = context.dataStore.data.map { preferences ->
		preferences[THEME_KEY] ?: DEFAULT_THEME
	}

	fun getSortTask(): Flow<Int> = context.dataStore.data.map { preferences ->
		preferences[SORT_TASK_KEY] ?: DEFAULT_SORT_TASK
	}

	fun getLastOpened(): Flow<String> = context.dataStore.data.map { preferences ->
		preferences[LAST_OPENED_KEY] ?: DEFAULT_LAST_OPENED
	}

	fun getStreak(): Flow<Int> = context.dataStore.data.map { preferences ->
		preferences[STREAK_KEY] ?: DEFAULT_STREAK
	}

	suspend fun setTheme(theme: Int) {
		context.dataStore.edit { preferences ->
			preferences[THEME_KEY] = theme
		}
	}

	suspend fun setSortTask(sortTask: Int) {
		context.dataStore.edit { preferences ->
			preferences[SORT_TASK_KEY] = sortTask
		}
	}

	suspend fun setLastOpened(lastOpened: String) {
		context.dataStore.edit { preferences ->
			preferences[LAST_OPENED_KEY] = lastOpened
		}
	}

	suspend fun setStreak(streak: Int) {
		context.dataStore.edit { preferences ->
			preferences[STREAK_KEY] = streak
		}
	}

}

