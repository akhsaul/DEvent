package org.akhsaul.dicodingevent.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.akhsaul.dicodingevent.R
import org.akhsaul.dicodingevent.util.SettingPreferences
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(){
    @Inject
    lateinit var settingPreferences: SettingPreferences

    override fun onCreate() {
        super.onCreate()
        settingPreferences.initThemeMode(getString(R.string.key_dark_mode), resources)
    }
}