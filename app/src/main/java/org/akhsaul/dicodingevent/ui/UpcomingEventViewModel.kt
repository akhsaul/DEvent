package org.akhsaul.dicodingevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.akhsaul.dicodingevent.data.Event
import org.akhsaul.dicodingevent.data.EventType
import org.akhsaul.dicodingevent.repository.EventRepository
import org.akhsaul.dicodingevent.util.Result
import org.akhsaul.dicodingevent.util.SettingPreferences
import javax.inject.Inject

@HiltViewModel
class UpcomingEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val settingPreferences: SettingPreferences,
) : ViewModel() {

    var hasShownToast = false
    private var currentList = listOf<Event>()
    private var filterIsOpened = false

    fun isInitialized() = settingPreferences.isInitialized()

    fun getUpcomingEventList(): LiveData<Result<List<Event>>> {
        return eventRepository.getUpcomingEvents()
    }

    fun fetchUpcomingEventList() {
        eventRepository.fetchUpcomingEvents(viewModelScope, 40)
    }

    fun searchEvent(title: String) {
        eventRepository.searchEvent(viewModelScope, title, EventType.ACTIVE)
    }

    fun getSearchEvent(): LiveData<Result<List<Event>>> {
        return eventRepository.getSearchedEvents()
    }

    fun openFilter(currentList: List<Event>) {
        this.currentList = currentList
        filterIsOpened = true
    }

    fun closeFilter(): List<Event> {
        filterIsOpened = false
        val list = currentList
        currentList = emptyList()
        return list
    }

    fun isFilterOpened() = filterIsOpened
}