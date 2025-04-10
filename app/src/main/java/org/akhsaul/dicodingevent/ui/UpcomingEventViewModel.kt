package org.akhsaul.dicodingevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.akhsaul.dicodingevent.data.Event
import org.akhsaul.dicodingevent.repository.EventRepository
import org.akhsaul.dicodingevent.repository.SearchType
import org.akhsaul.dicodingevent.util.SettingPreferences
import javax.inject.Inject

@HiltViewModel
class UpcomingEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val settingPreferences: SettingPreferences,
) : ViewModel() {
    var hasShownToast = false
    private val _currentUpcomingEventList = MutableLiveData<List<Event>>()
    private val _currentSearchEventList = MutableLiveData<List<Event>>()
    private var filterIsOpened = false
    private var keywordSearchEvent: String? = null

    fun isInitialized() = settingPreferences.isInitialized()

    fun setUpcomingEventList(list: List<Event>) = _currentUpcomingEventList.postValue(list)
    fun getUpcomingEventList(): LiveData<List<Event>> = _currentUpcomingEventList
    fun getUpcomingEventState() = eventRepository.getUpcomingEvents()

    fun fetchUpcomingEventList() {
        eventRepository.fetchUpcomingEvents(viewModelScope, 40)
    }

    fun setSearchEventList(list: List<Event>) = _currentSearchEventList.postValue(list)
    fun getSearchEventList(): LiveData<List<Event>> = _currentSearchEventList
    fun getSearchEventState() = eventRepository.getSearchedEvents()

    fun fetchSearchEvent() {
        val keyword = keywordSearchEvent ?: return
        searchEvent(keyword)
    }

    fun searchEvent(keyword: String) {
        eventRepository.searchEvent(viewModelScope, keyword, SearchType.ACTIVE)
        keywordSearchEvent = keyword
    }

    fun openFilter() {
        filterIsOpened = true
    }

    fun closeFilter() {
        filterIsOpened = false
        keywordSearchEvent = null
    }

    fun isFilterOpened() = filterIsOpened
}