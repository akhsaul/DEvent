package org.akhsaul.dicodingevent.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.akhsaul.dicodingevent.repository.EventRepository
import org.akhsaul.dicodingevent.util.SettingPreferences
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val settingPreferences: SettingPreferences
) : ViewModel() {
    var hasShownToast = false
    fun fetchFavoriteEvents() = eventRepository.fetchFavoriteEvents()
    fun getFavoriteEvents() = eventRepository.getFavoriteEvents()
    fun isInitialized() = settingPreferences.isInitialized()
}