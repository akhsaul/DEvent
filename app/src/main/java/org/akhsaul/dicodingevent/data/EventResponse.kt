package org.akhsaul.dicodingevent.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class EventResponse(
    @field:SerializedName("listEvents")
    val listEvents: List<Event> = listOf(),

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "events_table")
@Parcelize
data class Event(
    @PrimaryKey
    @field:SerializedName("id")
    val id: Int,

    @ColumnInfo(name = "image_logo")
    @field:SerializedName("imageLogo")
    val imageLogo: String,

    @ColumnInfo(name = "media_cover")
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

    @ColumnInfo(name = "city_name")
    @field:SerializedName("cityName")
    val cityName: String,

    @ColumnInfo(name = "begin_time")
    @field:SerializedName("beginTime")
    val beginTime: String,

    @ColumnInfo(name = "end_time")
    @field:SerializedName("endTime")
    val endTime: String,

    @ColumnInfo(name = "owner_name")
    @field:SerializedName("ownerName")
    val ownerName: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("link")
    val link: String,
) : Parcelable