package org.akhsaul.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class EventResponse(
    @field:SerializedName("listEvents")
    val listEvents: List<EventItemResponse> = listOf(),

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)