package org.akhsaul.dicodingevent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.akhsaul.dicodingevent.repository.EventRepository
import org.akhsaul.dicodingevent.util.SettingPreferences
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val settingPreferences: SettingPreferences
) : ViewModel() {
    var hasShownToast = false

    fun isInitialized() = settingPreferences.isInitialized()

    fun getUpcomingEventList() = eventRepository.getUpcomingEvents()

    fun fetchUpcomingEventList() {
        eventRepository.fetchUpcomingEvents(viewModelScope, 5)
    }

    fun getFinishedEventList() = eventRepository.getFinishedEvents()

    fun fetchFinishedEventList() {
        eventRepository.fetchFinishedEvents(viewModelScope, 5)
    }
}