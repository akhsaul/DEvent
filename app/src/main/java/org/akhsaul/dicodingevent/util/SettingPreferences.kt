package org.akhsaul.dicodingevent.util

import android.content.res.Resources
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.preference.PreferenceDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.akhsaul.dicodingevent.isSystemInDarkMode
import org.akhsaul.dicodingevent.setAppDarkMode

class SettingPreferences(
    private val dataStore: DataStore<Preferences>
) : PreferenceDataStore() {
    private var isInitialized = false
    private val mutex = Mutex()

    fun initThemeMode(key: String, resource: Resources) {
        if (isInitialized()) return

        val isDarkFromDatastore = runBlocking {
            getBoolean(key)
        }
        val isDarkFromSystem = isSystemInDarkMode(resource)
        val isDark = isDarkFromDatastore ?: isDarkFromSystem
        if (isDarkFromDatastore == null) {
            putBoolean(key, isDark)
        }
        setAppDarkMode(isDark)
        runBlocking {
            mutex.withLock {
                isInitialized = true
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