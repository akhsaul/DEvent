package org.akhsaul.dicodingevent.di

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface InitializerEntryPoint {

    companion object {
        fun resolve(context: Context): InitializerEntryPoint {
            requireNotNull(context.applicationContext)
            return EntryPointAccessors.fromApplication(
                context.applicationContext,
                InitializerEntryPoint::class.java
            )
        }
    }
}