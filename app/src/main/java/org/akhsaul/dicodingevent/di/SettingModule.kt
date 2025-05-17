package org.akhsaul.dicodingevent.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.akhsaul.dicodingevent.util.SettingPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingModule {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("dicoding_event_settings")

    @Provides
    @Singleton
    fun provideSettingPreferences(@ApplicationContext context: Context) =
        SettingPreferences(context.dataStore)
}