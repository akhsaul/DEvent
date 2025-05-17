package org.akhsaul.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import org.akhsaul.core.BuildConfig
import org.akhsaul.core.data.source.local.room.EventDao
import org.akhsaul.core.data.source.local.room.EventDatabase
import org.akhsaul.core.data.source.remote.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        // TODO Menerapkan certificate pinning untuk koneksi ke server.
        val certificatePinner = CertificatePinner.Builder()
            .add(BuildConfig.HOST_NAME, "sha256/DzuzCR2LACQP3q7NiiYGGd1TFf+Eg6Ql1BwDv/OCHH0=")
            .add(BuildConfig.HOST_NAME, "sha256/K7rZOrXHknnsEhUH8nLL4MZkejquUuIvOIr6tCa0rbo=")
            .add(BuildConfig.HOST_NAME, "sha256/C5+lpZ7tcVwmwQIMcRtPbsQtWLABXhQzejna0wHFr8M=")
            .build()
        return OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .certificatePinner(certificatePinner)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(client: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideEventDao(appDatabase: EventDatabase): EventDao {
        return appDatabase.eventDao()
    }

    @Singleton
    @Provides
    fun provideEventDatabase(@ApplicationContext context: Context): EventDatabase {
        // TODO Menerapkan encryption pada database.
        return Room.databaseBuilder(
            context,
            EventDatabase::class.java,
            "dicoding_event.db"
        ).build()
    }
}