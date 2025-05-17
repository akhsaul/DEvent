package org.akhsaul.core.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.akhsaul.core.SearchType
import org.akhsaul.core.data.source.local.room.EventDao
import org.akhsaul.core.data.source.remote.network.ApiService
import org.akhsaul.core.domain.model.Event
import org.akhsaul.core.domain.repository.EventRepository
import org.akhsaul.core.utils.DataMapper
import javax.inject.Inject

// TODO use Kotlin Flow
class EventRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao
) : EventRepository {
    private val resultUpcomingEvents = MediatorLiveData<Result<List<Event>>>()
    private val resultFinishedEvents = MediatorLiveData<Result<List<Event>>>()
    private val resultSearchEvent = MediatorLiveData<Result<List<Event>>>()
    private val resultFavoriteEvents = MediatorLiveData<Result<List<Event>>>()

    override fun fetchUpcomingEvents(
        scope: CoroutineScope,
        limit: Int
    ) {
        resultUpcomingEvents.value = Result.Loading
        scope.launch(Dispatchers.IO) {
            try {
                val apiResult = apiService.getUpcomingEvent(limit)

                if (apiResult.isSuccessful) {
                    val events = apiResult.body()?.listEvents ?: emptyList()
                    scope.launch {
                        resultUpcomingEvents.value =
                            Result.Success(DataMapper.mapResponsesToDomain(events))
                    }
                } else {
                    scope.launch {
                        resultUpcomingEvents.value = Result.Error()
                    }
                }

            } catch (_: Throwable) {
                scope.launch {
                    resultUpcomingEvents.value = Result.Error()
                }
            }
        }
    }

    override fun getUpcomingEvents(): LiveData<Result<List<Event>>> =
        resultUpcomingEvents

    fun getUpcomingEventsFlow(limit: Int): Flow<Result<List<Event>>> = flow {
        val apiResult = apiService.getAllEvent(EventType.ACTIVE.value, limit)
        if (apiResult.isSuccessful) {
            val events = apiResult.body()?.listEvents ?: emptyList()
            emit(Result.Success(DataMapper.mapResponsesToDomain(events)))
        } else {
            emit(Result.Error(apiResult.message()))
        }
    }.catch { emit(Result.Error(it.message)) }
        .onStart { emit(Result.Loading) }
        .flowOn(Dispatchers.IO)

    override fun fetchFinishedEvents(scope: CoroutineScope, limit: Int) {
        resultFinishedEvents.value = Result.Loading
        scope.launch(Dispatchers.IO) {
            try {
                val apiResult = apiService.getFinishedEvent(limit)
                if (apiResult.isSuccessful) {
                    val events = apiResult.body()?.listEvents ?: emptyList()
                    scope.launch {
                        resultFinishedEvents.value =
                            Result.Success(DataMapper.mapResponsesToDomain(events))
                    }
                } else {
                    scope.launch {
                        resultFinishedEvents.value = Result.Error()
                    }
                }
            } catch (_: Throwable) {
                scope.launch {
                    resultFinishedEvents.value = Result.Error()
                }
            }
        }
    }

    override fun getFinishedEvents(): LiveData<Result<List<Event>>> =
        resultFinishedEvents

    fun getFinishedEventsFlow(limit: Int): Flow<Result<List<Event>>> = flow {
        val apiResult = apiService.getAllEvent(EventType.INACTIVE.value, limit)
        if (apiResult.isSuccessful) {
            val events = apiResult.body()?.listEvents ?: emptyList()
            emit(Result.Success(DataMapper.mapResponsesToDomain(events)))
        } else {
            emit(Result.Error(apiResult.message()))
        }
    }.catch { emit(Result.Error(it.message)) }
        .onStart { emit(Result.Loading) }
        .flowOn(Dispatchers.IO)

    override suspend fun isFavoriteEvent(event: Event) =
        eventDao.findFavoriteEvent(event.id) != null

    override fun setFavoriteEvent(
        scope: CoroutineScope,
        event: Event,
        isFavorite: Boolean
    ) {
        scope.launch(Dispatchers.IO) {
            if (isFavorite) {
                eventDao.insertFavoriteEvent(DataMapper.mapDomainToEntity(event))
            } else {
                eventDao.deleteFavoriteEvent(DataMapper.mapDomainToEntity(event))
            }
        }
    }

    override fun searchEvent(scope: CoroutineScope, title: String, type: SearchType) {
        resultSearchEvent.value = Result.Loading
        scope.launch(Dispatchers.IO) {
            val finalTitle = title.trim()
            try {
                val apiResult = apiService.getSearchEvent(type.value, finalTitle)
                if (apiResult.isSuccessful) {
                    val events = apiResult.body()?.listEvents
                    scope.launch {
                        resultSearchEvent.value =
                            Result.Success(DataMapper.mapResponsesToDomain(events ?: emptyList()))
                    }
                } else {
                    scope.launch {
                        resultSearchEvent.value = Result.Error(apiResult.message())
                    }
                }
            } catch (t: Throwable) {
                scope.launch {
                    resultSearchEvent.value = Result.Error(t.message)
                }
            }
        }
    }

    override fun getSearchedEvents(): LiveData<Result<List<Event>>> = resultSearchEvent

    override fun fetchFavoriteEvents() {
        resultFavoriteEvents.value = Result.Loading
        try {
            resultFavoriteEvents.addSource(eventDao.getAllFavoriteEvents()) {
                resultFavoriteEvents.value = Result.Success(DataMapper.mapEntitiesToDomain(it))
            }
        } catch (t: Throwable) {
            resultFavoriteEvents.value = Result.Error(t.message)
        }
    }

    override fun getFavoriteEvents(): LiveData<Result<List<Event>>> = resultFavoriteEvents

    override fun getFavoriteEventsFlow() = eventDao.getAllFavoriteEvent()
        .map { DataMapper.mapEntitiesToDomain(it) }
        .map { Result.Success(it) }
        .catch<Result<List<Event>>> { emit(Result.Error(it.message)) }
        .onStart { emit(Result.Loading) }
        .flowOn(Dispatchers.IO)

    override suspend fun getNotificationEvent(): Result<Event?> {
        return try {
            val apiResult = apiService.getNotificationEvent()
            if (apiResult.isSuccessful) {
                val event = apiResult.body()?.listEvents?.firstOrNull()
                if (event != null) {
                    Result.Success(DataMapper.mapResponseToDomain(event))
                } else {
                    Result.Success(null)
                }
            } else {
                Result.Error(apiResult.message())
            }
        } catch (t: Throwable) {
            Result.Error(t.message)
        }
    }
}

enum class EventType(val value: Int) {
    ACTIVE(1),
    INACTIVE(0),
    ALL(-1)
}