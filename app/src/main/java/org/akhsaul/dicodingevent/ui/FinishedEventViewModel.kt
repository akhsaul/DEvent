package org.akhsaul.dicodingevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
class FinishedEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val settingPreferences: SettingPreferences
) : ViewModel() {
    var hasShownToast = false
    var isGrid = true
    private var filterIsOpened = false
    private var currentList = listOf<Event>()
    private val mixedResult = MediatorLiveData<Result<List<Event>>>()

    init {
        val upcomingEventsSource = eventRepository.getUpcomingEvents()
        val finishedEventsSource = eventRepository.getFinishedEvents()
        mixedResult.addSource(upcomingEventsSource) {
            val finishedEventsResult = finishedEventsSource.value
            if (it != null && finishedEventsResult != null) {
                combine(it, finishedEventsResult)
            }
        }
        mixedResult.addSource(finishedEventsSource) {
            val upcomingEventsResult = upcomingEventsSource.value
            if (it != null && upcomingEventsResult != null) {
                combine(upcomingEventsResult, it)
            }
        }
    }

    private fun combine(
        upcomingEventsResult: Result<List<Event>>,
        finishedEventsResult: Result<List<Event>>,
    ) {
        if (upcomingEventsResult is Result.Success && finishedEventsResult is Result.Success) {
            val combinedList = upcomingEventsResult.data.plus(finishedEventsResult.data)
            mixedResult.value = Result.Success(combinedList)
        } else {
            mixedResult.value = finishedEventsResult
        }
    }

    fun isInitialized() = settingPreferences.isInitialized()

    fun getSearchEvent(): LiveData<Result<List<Event>>> {
        return eventRepository.getSearchedEvents()
    }

    fun searchEvent(title: String) {
        eventRepository.searchEvent(viewModelScope, title, EventType.ALL)
    }

    fun getFinishedEvent(): LiveData<Result<List<Event>>> {
        return mixedResult
    }

    fun fetchFinishedEvent() {
        eventRepository.fetchUpcomingEvents(viewModelScope, 40)
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