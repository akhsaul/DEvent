package org.akhsaul.core.domain.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.akhsaul.core.SearchType
import org.akhsaul.core.data.Result
import org.akhsaul.core.domain.model.Event

interface EventRepository {
    fun fetchUpcomingEvents(scope: CoroutineScope, limit: Int)
    fun fetchFinishedEvents(scope: CoroutineScope, limit: Int)
    fun getUpcomingEvents(): LiveData<Result<List<Event>>>
    fun getFinishedEvents(): LiveData<Result<List<Event>>>
    suspend fun isFavoriteEvent(event: Event): Boolean
    fun setFavoriteEvent(scope: CoroutineScope, event: Event, isFavorite: Boolean)
    fun searchEvent(scope: CoroutineScope, title: String, type: SearchType)
    fun getSearchedEvents(): LiveData<Result<List<Event>>>
    fun fetchFavoriteEvents()
    fun getFavoriteEvents(): LiveData<Result<List<Event>>>
    fun getFavoriteEventsFlow(): Flow<Result<List<Event>>>
    suspend fun getNotificationEvent(): Result<Event?>
}