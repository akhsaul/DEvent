package org.akhsaul.dicodingevent.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//import org.akhsaul.dicodingevent.data.Event
//import org.akhsaul.dicodingevent.data.EventDao
//import org.akhsaul.dicodingevent.net.ApiService
//import org.akhsaul.dicodingevent.util.Result
import javax.inject.Inject

//class EventRepositoryImpl @Inject constructor(
//    private val apiService: ApiService,
//    private val eventDao: EventDao
//) : EventRepository {
//    private val resultUpcomingEvents = MediatorLiveData<Result<List<Event>>>()
//    private val resultFinishedEvents = MediatorLiveData<Result<List<Event>>>()
//    private val resultSearchEvent = MediatorLiveData<Result<List<Event>>>()
//    private val resultFavoriteEvents = MediatorLiveData<Result<List<Event>>>()
//
//    override fun fetchUpcomingEvents(
//        scope: CoroutineScope,
//        limit: Int
//    ) {
//
//        resultUpcomingEvents.value = Result.Loading
//        scope.launch(Dispatchers.IO) {
//            try {
//                val apiResult = apiService.getUpcomingEvent(limit)
//
//                if (apiResult.isSuccessful) {
//                    val events = apiResult.body()?.listEvents ?: emptyList()
//                    scope.launch {
//                        resultUpcomingEvents.value = Result.Success(events)
//                    }
//                } else {
//                    scope.launch {
//                        resultUpcomingEvents.value = Result.Error
//                    }
//                }
//
//            } catch (_: Throwable) {
//                scope.launch {
//                    resultUpcomingEvents.value = Result.Error
//                }
//            }
//        }
//    }
//
//    override fun getUpcomingEvents(): LiveData<Result<List<Event>>> = resultUpcomingEvents
//
//    override fun fetchFinishedEvents(scope: CoroutineScope, limit: Int) {
//        resultFinishedEvents.value = Result.Loading
//        scope.launch(Dispatchers.IO) {
//            try {
//                val apiResult = apiService.getFinishedEvent(limit)
//                if (apiResult.isSuccessful) {
//                    val events = apiResult.body()?.listEvents ?: emptyList()
//                    scope.launch {
//                        resultFinishedEvents.value = Result.Success(events)
//                    }
//                } else {
//                    scope.launch {
//                        resultFinishedEvents.value = Result.Error
//                    }
//                }
//            } catch (_: Throwable) {
//                scope.launch {
//                    resultFinishedEvents.value = Result.Error
//                }
//            }
//        }
//    }
//
//    override fun getFinishedEvents(): LiveData<Result<List<Event>>> = resultFinishedEvents
//
//    override suspend fun isFavoriteEvent(event: Event) =
//        eventDao.findFavoriteEvent(event.id) != null
//
//    override fun setFavoriteEvent(scope: CoroutineScope, event: Event, isFavorite: Boolean) {
//        scope.launch(Dispatchers.IO) {
//            if (isFavorite) {
//                eventDao.insertFavoriteEvent(event)
//            } else {
//                eventDao.deleteFavoriteEvent(event)
//            }
//        }
//    }
//
//    override fun searchEvent(scope: CoroutineScope, title: String, type: SearchType) {
//        resultSearchEvent.value = Result.Loading
//        scope.launch(Dispatchers.IO) {
//            val finalTitle = title.trim()
//            try {
//                val apiResult = apiService.getSearchEvent(type.value, finalTitle)
//                if (apiResult.isSuccessful) {
//                    val events = apiResult.body()?.listEvents
//                    scope.launch {
//                        resultSearchEvent.value = Result.Success(events ?: emptyList())
//                    }
//                } else {
//                    scope.launch {
//                        resultSearchEvent.value = Result.Error
//                    }
//                }
//            } catch (_: Throwable) {
//                scope.launch {
//                    resultSearchEvent.value = Result.Error
//                }
//            }
//        }
//    }
//
//    override fun getSearchedEvents(): LiveData<Result<List<Event>>> = resultSearchEvent
//
//    override fun fetchFavoriteEvents() {
//        resultFavoriteEvents.value = Result.Loading
//        try {
//            resultFavoriteEvents.addSource(eventDao.getAllFavoriteEvents()) {
//                resultFavoriteEvents.value = Result.Success(it)
//            }
//        } catch (_: Throwable) {
//            resultFavoriteEvents.value = Result.Error
//        }
//    }
//
//    override fun getFavoriteEvents(): LiveData<Result<List<Event>>> = resultFavoriteEvents
//}

//enum class SearchType(val value: Int) {
//    ACTIVE(1),
//    INACTIVE(0),
//}