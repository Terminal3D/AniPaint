package com.example.coursework.features.paintMenu.presentation.presentation

import android.graphics.Paint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursework.core.models.ImageSize
import com.example.coursework.core.models.PaintImage
import com.example.coursework.features.paintMenu.data.PaintMenuRepository
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

sealed interface PaintMenuUiEvent

sealed interface PaintMenuNavigationEvent {

    data class NavigateToNewImage(
        val imageSize: ImageSize
    ) : PaintMenuNavigationEvent

}

sealed interface PaintMenuAction {
    data class CreateNewImage(val imageSize: ImageSize) : PaintMenuAction
}

data class PaintMenuState(
    val isLoading: Boolean = true,
    val error: Boolean = false,

    val imageSizes: List<ImageSize> = emptyList(),
    val lastImage: PaintImage? = null
)

@HiltViewModel
class PaintMenuViewModel @Inject constructor(
    private val paintMenuRepository: PaintMenuRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PaintMenuState())
    val state = _state.asStateFlow()

    private val _uiEvents = MutableSharedFlow<PaintMenuUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val _navigationEvents = MutableSharedFlow<PaintMenuNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        init()
    }

    private fun init() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                withContext(Dispatchers.IO) {
                    val lastImage = paintMenuRepository.getLastImage()

                    _state.update {
                        it.copy(
                            imageSizes = paintMenuRepository.getImageSizes(),
                            lastImage = lastImage
                        )
                    }
                }
            } catch (e: Exception) {
//                _uiEvents.emit(PaintUiEvent.)
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onAction(action: PaintMenuAction) {
        viewModelScope.launch {
            when (action) {
                is PaintMenuAction.CreateNewImage -> _navigationEvents.emit(
                    PaintMenuNavigationEvent.NavigateToNewImage(action.imageSize)
                )
            }
        }
    }

}