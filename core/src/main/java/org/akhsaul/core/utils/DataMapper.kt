package org.akhsaul.core.utils

import org.akhsaul.core.data.source.local.entity.EventEntity
import org.akhsaul.core.data.source.remote.response.EventItemResponse
import org.akhsaul.core.domain.model.Event

object DataMapper {
    // TODO Memisahkan model untuk domain dengan model untuk data
    fun mapResponsesToEntities(input: List<EventItemResponse>): List<EventEntity> = input.map {
        EventEntity(
            id = it.id,
            imageLogo = it.imageLogo,
            mediaCover = it.mediaCover,
            category = it.category,
            name = it.name,
            summary = it.summary,
            quota = it.quota,
            registrants = it.registrants,
            cityName = it.cityName,
            beginTime = it.beginTime,
            endTime = it.endTime,
            ownerName = it.ownerName,
            description = it.description,
            link = it.link,
            isFavorite = false
        )
    }

    fun mapResponsesToDomain(input: List<EventItemResponse>): List<Event> = input.map {
        Event(
            id = it.id,
            imageLogo = it.imageLogo,
            mediaCover = it.mediaCover,
            category = it.category,
            name = it.name,
            summary = it.summary,
            quota = it.quota,
            registrants = it.registrants,
            cityName = it.cityName,
            beginTime = it.beginTime,
            endTime = it.endTime,
            ownerName = it.ownerName,
            description = it.description,
            link = it.link,
            isFavorite = false
        )
    }

    fun mapEntitiesToDomain(input: List<EventEntity>): List<Event> = input.map {
        Event(
            id = it.id,
            imageLogo = it.imageLogo,
            mediaCover = it.mediaCover,
            category = it.category,
            name = it.name,
            summary = it.summary,
            quota = it.quota,
            registrants = it.registrants,
            cityName = it.cityName,
            beginTime = it.beginTime,
            endTime = it.endTime,
            ownerName = it.ownerName,
            description = it.description,
            link = it.link,
            isFavorite = false
        )
    }

    fun mapResponseToDomain(input: EventItemResponse) = Event(
        id = input.id,
        imageLogo = input.imageLogo,
        mediaCover = input.mediaCover,
        category = input.category,
        name = input.name,
        summary = input.summary,
        quota = input.quota,
        registrants = input.registrants,
        cityName = input.cityName,
        beginTime = input.beginTime,
        endTime = input.endTime,
        ownerName = input.ownerName,
        description = input.description,
        link = input.link,
        isFavorite = false
    )

    fun mapDomainToEntity(input: Event) = EventEntity(
        id = input.id,
        imageLogo = input.imageLogo,
        mediaCover = input.mediaCover,
        category = input.category,
        name = input.name,
        summary = input.summary,
        quota = input.quota,
        registrants = input.registrants,
        cityName = input.cityName,
        beginTime = input.beginTime,
        endTime = input.endTime,
        ownerName = input.ownerName,
        description = input.description,
        link = input.link,
        isFavorite = false
    )
}