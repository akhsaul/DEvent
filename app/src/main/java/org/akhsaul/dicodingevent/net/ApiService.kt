package org.akhsaul.dicodingevent.net

import org.akhsaul.dicodingevent.data.EventResponse
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
    suspend fun getSearchEvent(
        @Query("active") active: Int = -1,
        @Query("q") keyword: String
    ): Response<EventResponse>

    @GET("events?active=-1&limit=1")
    suspend fun getNotificationEvent(): Response<EventResponse>
}