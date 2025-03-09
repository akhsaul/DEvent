package org.akhsaul.dicodingevent.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.preference.PreferenceDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SettingPreferences(
    private val dataStore: DataStore<Preferences>
) : PreferenceDataStore() {
    private var isInitialized = false
    private val mutex = Mutex()

    fun setThemeMode(dark: Boolean) {
        if (dark == isDarkModeTheme()) {
            runBlocking {
                mutex.withLock {
                    isInitialized = true
                }
            }
            return
        }

        val compatDelegate = if (dark) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(compatDelegate)

        runBlocking {
            mutex.withLock {
                isInitialized = false
            }
        }
    }

    fun isInitialized(): Boolean {
        return runBlocking {
            mutex.withLock {
                isInitialized
            }
        }
    }

    private fun isDarkModeTheme(): Boolean {
        return when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> false
        }
    }

    fun getDarkMode(key: String, default: Boolean): Boolean {
        val result = runBlocking {
            getBoolean(key)
        }
        if (result == null) {
            putBoolean(key, default)
        }
        return result ?: default
    }

    private suspend fun getBoolean(key: String): Boolean? {
        val preferences = dataStore.data.firstOrNull()
        return preferences?.get(booleanPreferencesKey(key))
    }

    override fun putBoolean(key: String?, value: Boolean) {
        if (key == null) return
        runBlocking {
            dataStore.edit { preferences ->
                preferences[booleanPreferencesKey(key)] = value
            }
        }
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        if (key == null) return defValue
        return runBlocking {
            val result = getBoolean(key)
            result ?: defValue
        }
    }
}