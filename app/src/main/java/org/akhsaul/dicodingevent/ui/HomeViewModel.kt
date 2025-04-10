package org.akhsaul.dicodingevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.akhsaul.dicodingevent.data.Event
import org.akhsaul.dicodingevent.repository.EventRepository
import org.akhsaul.dicodingevent.util.SettingPreferences
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val settingPreferences: SettingPreferences
) : ViewModel() {
    var hasShownToast = false
    private val _currentUpcomingEventList = MutableLiveData<List<Event>>()
    private val _currentFinishedEventList = MutableLiveData<List<Event>>()

    fun isInitialized() = settingPreferences.isInitialized()

    fun setUpcomingEventList(list: List<Event>) {
        _currentUpcomingEventList.postValue(list)
    }

    fun getUpcomingEventList(): LiveData<List<Event>> = _currentUpcomingEventList

    fun getUpcomingEventState() = eventRepository.getUpcomingEvents()

    fun fetchUpcomingEventList() {
        eventRepository.fetchUpcomingEvents(viewModelScope, 5)
    }

    fun setFinishedEventList(list: List<Event>) {
        _currentFinishedEventList.postValue(list)
    }

    fun getFinishedEventList(): LiveData<List<Event>> = _currentFinishedEventList

    fun getFinishedEventState() = eventRepository.getFinishedEvents()

    fun fetchFinishedEventList() {
        eventRepository.fetchFinishedEvents(viewModelScope, 5)
    }
}