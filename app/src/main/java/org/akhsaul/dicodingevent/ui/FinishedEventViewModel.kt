package org.akhsaul.dicodingevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.akhsaul.dicodingevent.data.Event
import org.akhsaul.dicodingevent.repository.EventRepository
import org.akhsaul.dicodingevent.repository.SearchType
import org.akhsaul.dicodingevent.util.Result
import org.akhsaul.dicodingevent.util.SettingPreferences
import javax.inject.Inject

@HiltViewModel
class FinishedEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val settingPreferences: SettingPreferences
) : ViewModel() {
    var hasShownToast = false
    private var filterIsOpened = false
    private var currentList = listOf<Event>()

    fun isInitialized() = settingPreferences.isInitialized()

    fun getSearchEvent(): LiveData<Result<List<Event>>> {
        return eventRepository.getSearchedEvents()
    }

    fun searchEvent(title: String) {
        eventRepository.searchEvent(viewModelScope, title, SearchType.INACTIVE)
    }

    fun getFinishedEvent(): LiveData<Result<List<Event>>> {
        return eventRepository.getFinishedEvents()
    }

    fun fetchFinishedEvent() {
        eventRepository.fetchFinishedEvents(viewModelScope, 40)
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