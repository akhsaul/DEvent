package org.akhsaul.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class EventItemResponse(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("imageLogo")
    val imageLogo: String,

    @field:SerializedName("mediaCover")
    val mediaCover: String,

    @field:SerializedName("category")
    val category: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("summary")
    val summary: String,

    @field:SerializedName("quota")
    val quota: Int,

    @field:SerializedName("registrants")
    val registrants: Int,

    @field:SerializedName("cityName")
    val cityName: String,

    @field:SerializedName("beginTime")
    val beginTime: String,

    @field:SerializedName("endTime")
    val endTime: String,

    @field:SerializedName("ownerName")
    val ownerName: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("link")
    val link: String,
)