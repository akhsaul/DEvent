package org.akhsaul.dicodingevent.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.akhsaul.dicodingevent.BuildConfig
import org.akhsaul.dicodingevent.data.AppDatabase
import org.akhsaul.dicodingevent.data.EventDao
import org.akhsaul.dicodingevent.net.ApiService
import org.akhsaul.dicodingevent.repository.EventRepository
import org.akhsaul.dicodingevent.repository.EventRepositoryImpl
import org.akhsaul.dicodingevent.util.SettingPreferences
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideEventRepository(
        apiService: ApiService,
        eventDao: EventDao,
    ): EventRepository {
        return EventRepositoryImpl(apiService, eventDao)
    }

    @Provides
    @Singleton
    fun provideEventDao(appDatabase: AppDatabase): EventDao {
        return appDatabase.eventDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "dicoding_event_database"
        ).build()
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("dicoding_event_settings")

    @Provides
    @Singleton
    fun provideSettingPreferences(@ApplicationContext context: Context): SettingPreferences {
        return SettingPreferences(context.dataStore)
    }
}