package com.vishal2376.snaptick.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object PreferenceManager {

	private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.SETTINGS_KEY)

	fun loadPreference(context: Context, key: String, defaultValue: Int = 0): Flow<Int> {
		return context.dataStore.data
			.map { preferences ->
				preferences[intPreferencesKey(key)] ?: defaultValue
			}
	}

	suspend fun savePreferences(context: Context, key: String, value: Int) {
		context.dataStore.edit { preferences ->
			preferences[intPreferencesKey(key)] = value
		}
	}

}