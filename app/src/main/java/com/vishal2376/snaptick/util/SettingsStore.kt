package com.vishal2376.snaptick.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.SortTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate


class SettingsStore(val context: Context) {

	companion object {
		val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.SETTINGS_KEY)

		private val THEME_KEY = intPreferencesKey("theme_key")
		private val SORT_TASK_KEY = intPreferencesKey("sort_task_key")
		private val LAST_OPENED_KEY = stringPreferencesKey("last_opened_key")
		private val STREAK_KEY = intPreferencesKey("streak_key")

		private val DEFAULT_THEME = AppTheme.Dark.ordinal
		private val DEFAULT_SORT_TASK = SortTask.BY_CREATE_TIME_DESCENDING.ordinal
		private val DEFAULT_LAST_OPENED = LocalDate.now().toString()
		private const val DEFAULT_STREAK = 0
	}


	val themeKey: Flow<Int> = context.dataStore.data.map { preferences ->
		preferences[THEME_KEY] ?: DEFAULT_THEME
	}

	val sortTaskKey: Flow<Int> = context.dataStore.data.map { preferences ->
		preferences[SORT_TASK_KEY] ?: DEFAULT_SORT_TASK
	}

	val lastOpenedKey: Flow<String> = context.dataStore.data.map { preferences ->
		preferences[LAST_OPENED_KEY] ?: DEFAULT_LAST_OPENED
	}

	val streakKey: Flow<Int> = context.dataStore.data.map { preferences ->
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

