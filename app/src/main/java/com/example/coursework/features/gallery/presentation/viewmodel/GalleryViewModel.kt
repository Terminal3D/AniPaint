package com.example.coursework.features.gallery.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursework.features.gallery.data.GalleryImage
import com.example.coursework.features.gallery.data.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GalleryUiEvent

sealed interface GalleryNavigationEvent {
    data class NavigateToImage(val id: Int) : GalleryNavigationEvent
    data object NavigateToPaintMenu : GalleryNavigationEvent
}

sealed interface GalleryAction {
    data class NavigateToImageAction(val image: GalleryImage) : GalleryAction
    data class DeleteImageAction(val image: GalleryImage) : GalleryAction
    data object NavigateToPaintMenuAction : GalleryAction
}

data class GalleryState(
    val isLoading: Boolean = true,
    val error: Boolean = false,

    val savedImages: List<GalleryImage> = emptyList()
)


@HiltViewModel
class GalleryViewModel @Inject constructor(private val galleryRepository: GalleryRepository) :
    ViewModel() {

    private val _state = MutableStateFlow(GalleryState())
    val state = _state.asStateFlow()

    private val _uiEvents = MutableSharedFlow<GalleryUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val _navigationEvents = MutableSharedFlow<GalleryNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()


    init {
        getSavedImages()
    }

    private fun getSavedImages() {
        viewModelScope.launch {
            galleryRepository.getSavedImages().collect { images ->
                _state.update {
                    it.copy(savedImages = images)
                }
            }
        }
    }


    fun onAction(action: GalleryAction) {
        viewModelScope.launch {
            when (action) {
                is GalleryAction.DeleteImageAction -> galleryRepository.deleteSavedImage(action.image)
                is GalleryAction.NavigateToImageAction -> _navigationEvents.emit(
                    GalleryNavigationEvent.NavigateToImage(action.image.id)
                )
                GalleryAction.NavigateToPaintMenuAction -> _navigationEvents.emit(
                    GalleryNavigationEvent.NavigateToPaintMenu
                )
            }
        }
    }
}