package org.akhsaul.dicodingevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import org.akhsaul.dicodingevent.data.Event
import org.akhsaul.dicodingevent.repository.EventRepository
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    private var _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite
    private lateinit var _event: Event

    fun setCurrentEvent(event: Event) {
        _event = event
    }

    fun isFavoriteEvent(): Boolean {
        return runBlocking {
            val result = eventRepository.isFavoriteEvent(_event)
            _isFavorite.value = result
            requireNotNull(_isFavorite.value)
        }
    }

    fun toggleFavorite() {
        _isFavorite.value = _isFavorite.value?.not()
    }

    fun saveToDatabase(isFavorite: Boolean) {
        eventRepository.setFavoriteEvent(viewModelScope, _event, isFavorite)
    }
}