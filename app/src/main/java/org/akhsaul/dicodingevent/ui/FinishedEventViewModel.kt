package org.akhsaul.dicodingevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.akhsaul.core.SearchType
import org.akhsaul.core.domain.model.Event
import org.akhsaul.core.domain.repository.EventRepository
import org.akhsaul.dicodingevent.util.SettingPreferences
import javax.inject.Inject

@HiltViewModel
class FinishedEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val settingPreferences: SettingPreferences
) : ViewModel() {
    var hasShownToast = false
    private val _currentFinishedEventList = MutableLiveData<List<Event>>()
    private val _currentSearchEventList = MutableLiveData<List<Event>>()
    private var filterIsOpened = false
    private var keywordSearchEvent: String? = null

    fun isInitialized() = settingPreferences.isInitialized()

    fun setSearchEventList(list: List<Event>) = _currentSearchEventList.postValue(list)
    fun getSearchEventList(): LiveData<List<Event>> = _currentSearchEventList
    fun getSearchEventState() = eventRepository.getSearchedEvents()

    fun fetchSearchEvent() {
        val keyword = keywordSearchEvent ?: return
        searchEvent(keyword)
    }

    fun searchEvent(keyword: String) {
        eventRepository.searchEvent(viewModelScope, keyword, SearchType.INACTIVE)
        keywordSearchEvent = keyword
    }

    fun setFinishedEventList(list: List<Event>) = _currentFinishedEventList.postValue(list)
    fun getFinishedEventList(): LiveData<List<Event>> = _currentFinishedEventList
    fun getFinishedEventState() = eventRepository.getFinishedEvents()

    fun fetchFinishedEvent() {
        eventRepository.fetchFinishedEvents(viewModelScope, 40)
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