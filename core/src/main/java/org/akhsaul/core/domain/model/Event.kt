package org.akhsaul.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    val id: Int,

    val imageLogo: String,

    val mediaCover: String,

    val category: String,

    val name: String,

    val summary: String,

    val quota: Int,


    val registrants: Int,

    val cityName: String,

    val beginTime: String,

    val endTime: String,

    val ownerName: String,

    val description: String,

    val link: String,

    val isFavorite: Boolean,
) : Parcelable