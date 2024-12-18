package com.example.coursework.features.entry.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EntryAction {
    data object CreatePicture : EntryAction
    data object CreateAnimation : EntryAction
    data object OpenGallery : EntryAction
}

sealed interface EntryNavgiationEvents {
    data object CreatePicture : EntryNavgiationEvents
    data object CreateAnimation : EntryNavgiationEvents
    data object OpenGallery : EntryNavgiationEvents
}

@HiltViewModel
class EntryViewModel @Inject constructor() : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<EntryNavgiationEvents>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    fun onAction(action: EntryAction) {
        viewModelScope.launch {
            when (action) {
                EntryAction.CreateAnimation -> _navigationEvents.emit(EntryNavgiationEvents.CreateAnimation)
                EntryAction.CreatePicture -> _navigationEvents.emit(EntryNavgiationEvents.CreatePicture)
                EntryAction.OpenGallery -> _navigationEvents.emit(EntryNavgiationEvents.OpenGallery)
            }
        }
    }
}