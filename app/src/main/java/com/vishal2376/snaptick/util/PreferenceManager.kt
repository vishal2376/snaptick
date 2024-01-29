package com.vishal2376.snaptick.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferenceManager(val context: Context) {

	private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.SETTINGS_KEY)
	private val dataStore = context.dataStore

	fun loadPreference(key: String, defaultValue: Int = 0): Flow<Int> {
		val themeKey = intPreferencesKey(key)
		return dataStore.data
			.map { preferences ->
				preferences[themeKey] ?: defaultValue
			}
	}

	suspend fun savePreferences(key: String, value: Int) {
		dataStore.edit { preferences ->
			preferences[intPreferencesKey(key)] = value
		}
	}

}