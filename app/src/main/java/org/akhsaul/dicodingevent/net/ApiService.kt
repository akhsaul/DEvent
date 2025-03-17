package org.akhsaul.dicodingevent.net

import org.akhsaul.dicodingevent.data.EventResponse
import org.akhsaul.dicodingevent.data.EventType
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int = EventType.ALL.value,
        @Query("limit") limit: Int
    ): Response<EventResponse>

    @GET("events")
    suspend fun getSearchEvent(
        @Query("active") active: Int = EventType.ALL.value,
        @Query("q") keyword: String
    ): Response<EventResponse>

    @GET("events?active=-1&limit=1")
    suspend fun getNotificationEvent(): Response<EventResponse>
}