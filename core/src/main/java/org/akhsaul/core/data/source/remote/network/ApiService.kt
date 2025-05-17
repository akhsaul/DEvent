package org.akhsaul.core.data.source.remote.network

import org.akhsaul.core.data.EventType
import org.akhsaul.core.data.source.remote.response.EventResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events?active=1")
    suspend fun getUpcomingEvent(
        @Query("limit") limit: Int
    ): Response<EventResponse>

    @GET("events?active=0")
    suspend fun getFinishedEvent(
        @Query("limit") limit: Int
    ): Response<EventResponse>

    @GET("events")
    suspend fun getAllEvent(
        @Query("active") eventType: Int = EventType.ALL.value,
        @Query("limit") limit: Int
    ): Response<EventResponse>

    @GET("events")
    suspend fun getSearchEvent(
        @Query("active") eventType: Int = EventType.ALL.value,
        @Query("q") keyword: String
    ): Response<EventResponse>

    @GET("events?active=-1&limit=1")
    suspend fun getNotificationEvent(): Response<EventResponse>
}