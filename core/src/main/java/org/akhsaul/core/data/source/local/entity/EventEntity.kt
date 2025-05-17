package org.akhsaul.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events_table")
data class EventEntity(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "image_logo")
    val imageLogo: String,

    @ColumnInfo(name = "media_cover")
    val mediaCover: String,

    val category: String,

    val name: String,

    val summary: String,

    val quota: Int,

    val registrants: Int,

    @ColumnInfo(name = "city_name")
    val cityName: String,

    @ColumnInfo(name = "begin_time")
    val beginTime: String,

    @ColumnInfo(name = "end_time")
    val endTime: String,

    @ColumnInfo(name = "owner_name")
    val ownerName: String,

    val description: String,

    val link: String,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean
)