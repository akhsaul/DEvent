package org.akhsaul.dicodingevent.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.akhsaul.dicodingevent.data.Event
import org.akhsaul.dicodingevent.repository.EventRepository
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    private var _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite
    private var _event: Event? = null
    val event get() = requireNotNull(_event)

    fun setCurrentEvent(event: Event) {
        _event = event

        viewModelScope.launch {
            val result = eventRepository.isFavoriteEvent(event)
            _isFavorite.value = result
        }
    }

    fun toggleFavorite() {
        _isFavorite.value = _isFavorite.value?.not()
        eventRepository.setFavoriteEvent(
            viewModelScope,
            event,
            requireNotNull(_isFavorite.value)
        )
    }
}