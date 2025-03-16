package org.akhsaul.dicodingevent.di

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import org.akhsaul.dicodingevent.R
import org.akhsaul.dicodingevent.util.SettingPreferences
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var settingPreferences: SettingPreferences

    override val workManagerConfiguration: Configuration
        get() {
            return Configuration.Builder()
                .setWorkerCoroutineContext(Dispatchers.IO + CoroutineName("Work-Manager"))
                .setWorkerFactory(workerFactory)
                .build()
        }

    override fun onCreate() {
        super.onCreate()
        InitializerEntryPoint.resolve(this)
        WorkManager.initialize(this, workManagerConfiguration)
        settingPreferences.initThemeMode(getString(R.string.key_dark_mode), resources)
    }
}
