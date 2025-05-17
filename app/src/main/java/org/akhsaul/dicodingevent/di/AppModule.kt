package org.akhsaul.dicodingevent.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.akhsaul.core.data.EventRepositoryImpl
import org.akhsaul.core.domain.repository.EventRepository
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun provideEventRepository(
        eventRepositoryImpl: EventRepositoryImpl
    ): EventRepository
}