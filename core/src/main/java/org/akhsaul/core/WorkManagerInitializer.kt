package org.akhsaul.core

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import java.util.Collections

@Suppress("unused")
class WorkManagerInitializer : Initializer<WorkManager> {
    override fun create(context: Context): WorkManager {
        val entryPoint = WorkManagerEntryPoint.resolve(context)
        val configuration = Configuration.Builder()
            .setWorkerCoroutineContext(Dispatchers.IO + CoroutineName("Work-Manager"))
            .setWorkerFactory(entryPoint.hiltWorkerFactory())
            .build()
        WorkManager.initialize(context, configuration)
        return WorkManager.getInstance(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface WorkManagerEntryPoint {
        fun hiltWorkerFactory(): HiltWorkerFactory

        companion object {
            fun resolve(context: Context): WorkManagerEntryPoint =
                EntryPointAccessors.fromApplication(context)
        }
    }
}

