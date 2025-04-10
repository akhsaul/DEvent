package org.akhsaul.dicodingevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.akhsaul.dicodingevent.data.Event
import org.akhsaul.dicodingevent.repository.EventRepository
import org.akhsaul.dicodingevent.util.SettingPreferences
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val settingPreferences: SettingPreferences
) : ViewModel() {
    var hasShownToast = false
    private val _currentFavoriteEventList = MutableLiveData<List<Event>>()
    fun setFavoriteEventList(list: List<Event>)  = _currentFavoriteEventList.postValue(list)
    fun getFavoriteEventList(): LiveData<List<Event>> = _currentFavoriteEventList
    fun getFavoriteEventState() = eventRepository.getFavoriteEvents()
    fun fetchFavoriteEvents() = eventRepository.fetchFavoriteEvents()
    fun isInitialized() = settingPreferences.isInitialized()
}