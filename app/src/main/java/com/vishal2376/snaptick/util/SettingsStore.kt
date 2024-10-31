package com.vishal2376.snaptick.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.CalenderView
import com.vishal2376.snaptick.presentation.common.SortTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime


class SettingsStore(val context: Context) {

	companion object {
		val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.SETTINGS_KEY)

		private val THEME_KEY = intPreferencesKey("theme_key")
		private val DYNAMIC_THEME_KEY = booleanPreferencesKey("dynamic_theme_key")
		private val TIME_PICKER_KEY = booleanPreferencesKey("time_picker_key")
		private val TIME_FORMAT_KEY = booleanPreferencesKey("time_format_key")
		private val LANGUAGE_KEY = stringPreferencesKey("language_key")
		private val SORT_TASK_KEY = intPreferencesKey("sort_task_key")
		private val CALENDER_VIEW_KEY = intPreferencesKey("calender_view_key")
		private val LAST_OPENED_KEY = stringPreferencesKey("last_opened_key")
		private val STREAK_KEY = intPreferencesKey("streak_key")
		private val SLEEP_TIME_KEY = stringPreferencesKey("sleep_time_key")

		private const val DEFAULT_LANGUAGE = "en"
		private val DEFAULT_THEME = AppTheme.Dark.ordinal
		private const val DEFAULT_DYNAMIC_THEME = false
		private const val DEFAULT_TIME_PICKER_KEY = true
		private const val DEFAULT_TIME_FORMAT = false
		private val DEFAULT_CALENDER_VIEW = CalenderView.MONTHLY.ordinal
		private val DEFAULT_SORT_TASK = SortTask.BY_CREATE_TIME_DESCENDING.ordinal
		private val DEFAULT_LAST_OPENED = LocalDate.now().toString()
		private const val DEFAULT_STREAK = 0
		private val DEFAULT_SLEEP_TIME_KEY = LocalTime.of(23, 59).toString()
	}


	val themeKey: Flow<Int> = context.dataStore.data.map { preferences ->
		preferences[THEME_KEY] ?: DEFAULT_THEME
	}

	val dynamicThemeKey: Flow<Boolean> = context.dataStore.data.map { preferences ->
		preferences[DYNAMIC_THEME_KEY] ?: DEFAULT_DYNAMIC_THEME
	}

	val timePickerKey: Flow<Boolean> = context.dataStore.data.map { preferences ->
		preferences[TIME_PICKER_KEY] ?: DEFAULT_TIME_PICKER_KEY
	}

	val timeFormatKey: Flow<Boolean> = context.dataStore.data.map { preferences ->
		preferences[TIME_FORMAT_KEY] ?: DEFAULT_TIME_FORMAT
	}

	val languageKey: Flow<String> = context.dataStore.data.map { preferences ->
		preferences[LANGUAGE_KEY] ?: DEFAULT_LANGUAGE
	}

	val sleepTimeKey: Flow<String> = context.dataStore.data.map { preferences ->
		preferences[SLEEP_TIME_KEY] ?: DEFAULT_SLEEP_TIME_KEY
	}

	val sortTaskKey: Flow<Int> = context.dataStore.data.map { preferences ->
		preferences[SORT_TASK_KEY] ?: DEFAULT_SORT_TASK
	}

	val calenderViewKey: Flow<Int> = context.dataStore.data.map { preferences ->
		preferences[CALENDER_VIEW_KEY] ?: DEFAULT_CALENDER_VIEW
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

	suspend fun setDynamicTheme(isEnabled: Boolean) {
		context.dataStore.edit { preferences ->
			preferences[DYNAMIC_THEME_KEY] = isEnabled
		}
	}

	suspend fun setTimePicker(isWheelTimePicker: Boolean) {
		context.dataStore.edit { preferences ->
			preferences[TIME_PICKER_KEY] = isWheelTimePicker
		}
	}

	suspend fun setTimeFormat(is24hourFormat: Boolean) {
		context.dataStore.edit { preferences ->
			preferences[TIME_FORMAT_KEY] = is24hourFormat
		}
	}

	suspend fun setLanguage(language: String) {
		context.dataStore.edit { preferences ->
			preferences[LANGUAGE_KEY] = language
		}
	}

	suspend fun setSleepTime(time: String) {
		context.dataStore.edit { preferences ->
			preferences[SLEEP_TIME_KEY] = time
		}
	}

	suspend fun setSortTask(sortTask: Int) {
		context.dataStore.edit { preferences ->
			preferences[SORT_TASK_KEY] = sortTask
		}
	}

	suspend fun setCalenderView(calenderView: Int) {
		context.dataStore.edit { preferences ->
			preferences[CALENDER_VIEW_KEY] = calenderView
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

