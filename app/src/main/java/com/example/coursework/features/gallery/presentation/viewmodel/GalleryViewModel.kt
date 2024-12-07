package com.example.coursework.features.gallery.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursework.features.gallery.data.GalleryAnimation
import com.example.coursework.features.gallery.data.GalleryImage
import com.example.coursework.features.gallery.data.GalleryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class GalleryType(val value: String) {
    IMAGES("Изображения"),
    ANIMATIONS("Анимации")
}

sealed interface GalleryUiEvent

sealed interface GalleryNavigationEvent {
    data class NavigateToImage(val id: Int) : GalleryNavigationEvent
    data object NavigateToPaintMenu : GalleryNavigationEvent
    data class NavigateToAnimation(val id: Int?) : GalleryNavigationEvent
}

sealed interface GalleryAction {
    data class NavigateToImageAction(val image: GalleryImage) : GalleryAction
    data class DeleteImageAction(val image: GalleryImage?) : GalleryAction
    data class SwitchGalleryType(val type: GalleryType) : GalleryAction
    data object NavigateToPaintMenuAction : GalleryAction
    data object NavigateToNewAnimation : GalleryAction
    data class NavigateToAnimationAction(val animation: GalleryAnimation) : GalleryAction
    data class DeleteAnimationAction(val animation: GalleryAnimation?) : GalleryAction
}

data class GalleryState(
    val isLoading: Boolean = true,
    val error: Boolean = false,

    val savedImages: List<GalleryImage> = emptyList(),
    val savedAnimations: List<GalleryAnimation> = emptyList(),

    val galleryType: GalleryType = GalleryType.IMAGES,
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
            _state.update {
                it.copy(isLoading = true)
            }

            subscribeToContent()

            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    private suspend fun subscribeToContent() = withContext(Dispatchers.IO) {
        launch {
            galleryRepository.getSavedImages().collect { images ->
                _state.update {
                    it.copy(savedImages = images)
                }
            }
        }
        launch {
            galleryRepository.getSavedAnimations().collect { animations ->
                _state.update {
                    it.copy(savedAnimations = animations)
                }
            }
        }
    }

    fun onAction(action: GalleryAction) {
        viewModelScope.launch {
            when (action) {
                is GalleryAction.DeleteImageAction -> action.image?.let {
                    galleryRepository.deleteSavedImage(action.image)
                }

                is GalleryAction.NavigateToImageAction -> _navigationEvents.emit(
                    GalleryNavigationEvent.NavigateToImage(action.image.id)
                )

                GalleryAction.NavigateToPaintMenuAction -> _navigationEvents.emit(
                    GalleryNavigationEvent.NavigateToPaintMenu
                )

                is GalleryAction.SwitchGalleryType -> {
                    _state.update { it.copy(galleryType = action.type) }
                }

                is GalleryAction.NavigateToNewAnimation -> {
                    _navigationEvents.emit(GalleryNavigationEvent.NavigateToAnimation(null))
                }

                is GalleryAction.DeleteAnimationAction -> {
                    action.animation?.let {
                        galleryRepository.deleteSavedAnimation(action.animation)
                    }
                }

                is GalleryAction.NavigateToAnimationAction -> {
                    _navigationEvents.emit(GalleryNavigationEvent.NavigateToAnimation(action.animation.id))
                }
            }
        }
    }
}