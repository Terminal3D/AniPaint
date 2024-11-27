package com.example.coursework.features.paint.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursework.core.models.ImageSize
import com.example.coursework.core.models.PaintImage
import com.example.coursework.features.paint.data.PaintRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


sealed interface PaintUiEvent {

}

sealed interface PaintNavigationEvent {
    data object NavigateBack : PaintNavigationEvent
}

sealed interface PaintAction {
    data object NavigateBack : PaintAction

    data object ToggleBrush : PaintAction
    data object SelectEraser : PaintAction
    data class ChangeColorPickerVisibility(val isVisible: Boolean) : PaintAction
    data class SelectColor(val color: Color) : PaintAction
    data object SaveImage : PaintAction
    data object ClearScreen : PaintAction
    data class ChangeGridVisibility(val isVisible: Boolean) : PaintAction
    data class ChangeClearDialogVisibility(val isVisible: Boolean) : PaintAction
    data class DrawPixel(val x: Int, val y: Int) : PaintAction
}

data class PaintState(
    val isLoading: Boolean = true,
    val error: Boolean = false,
    val isBrushEnabled: Boolean = false,
    val isEraserEnabled: Boolean = false,
    val currentColor: Color = Color.Black,
    val isColorPickerDialogVisible: Boolean = false,
    val isGridVisible: Boolean = true,
    val imageSize: ImageSize = ImageSize.XS,
    val pixels: Array<IntArray> = emptyArray(),

    val isClearDialogVisible: Boolean = false
)

@HiltViewModel
class PaintViewModel @Inject constructor(
    private val paintRepository: PaintRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PaintState())
    val state = _state.asStateFlow()

    private val _uiEvents = MutableSharedFlow<PaintUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val _navigationEvents = MutableSharedFlow<PaintNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()


    fun getPaintScreen(
        imageSize: ImageSize?
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                withContext(Dispatchers.IO) {
                    if (imageSize != null) {
                        _state.update {
                            it.copy(imageSize = imageSize)
                        }

                        makeClearScreen()
                    } else {
                        val lastImage = paintRepository.getLastImage().firstOrNull()
                        if (lastImage != null) {
                            _state.update { state ->
                                state.copy(
                                    pixels = lastImage.pixels.toTypedArray(),
                                    imageSize = lastImage.imageSize
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(imageSize = ImageSize.XXS)
                            }
                            makeClearScreen()
                        }

                    }
                }
            } catch (e: Exception) {
//                _uiEvents.emit(PaintUiEvent.)
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun makeClearScreen() {
        _state.update {
            it.copy(
                pixels = Array(state.value.imageSize.size) {
                    IntArray(state.value.imageSize.size) {
                        Color.White.toArgb()
                    }
                },
            )
        }
    }

    private fun drawPixel(x: Int, y: Int) {
        if (x in 0 until state.value.imageSize.size && y in 0 until state.value.imageSize.size) {
            state.value.pixels[x][y] = state.value.currentColor.toArgb()
        }
    }

    private fun erasePixel(x: Int, y: Int) {
        if (x in 0 until state.value.imageSize.size && y in 0 until state.value.imageSize.size) {
            state.value.pixels[x][y] = Color.White.toArgb()
        }
    }

    private suspend fun saveImageAction() {
        paintRepository.saveLastImage(
            state.value.imageSize,
            state.value.pixels.toList()
        )
    }


    fun onAction(action: PaintAction) {
        when (action) {
            is PaintAction.NavigateBack -> {
                viewModelScope.launch {
                    saveImageAction()
                    _navigationEvents.emit(PaintNavigationEvent.NavigateBack)
                }
            }

            is PaintAction.ToggleBrush -> {
                _state.update {
                    it.copy(
                        isBrushEnabled = !it.isBrushEnabled,
                        isEraserEnabled = false
                    )
                }
            }

            is PaintAction.SelectEraser -> {
                _state.update {
                    it.copy(
                        isEraserEnabled = !it.isEraserEnabled,
                        isBrushEnabled = false
                    )
                }
            }

            is PaintAction.SelectColor -> {
                _state.update { it.copy(currentColor = action.color) }
            }

            is PaintAction.ChangeColorPickerVisibility -> {
                _state.update { it.copy(isColorPickerDialogVisible = action.isVisible) }
            }

            is PaintAction.ChangeGridVisibility -> {
                _state.update {
                    it.copy(
                        isGridVisible = action.isVisible
                    )
                }
            }

            is PaintAction.ChangeClearDialogVisibility -> {
                _state.update {
                    it.copy(
                        isClearDialogVisible = action.isVisible
                    )
                }
            }

            is PaintAction.SaveImage -> {
                // Пока без реализации
                // _uiEvents.emit(PaintUiEvent.ShowMessage("Сохранение пока не реализовано"))
            }

            is PaintAction.DrawPixel -> {
                if (state.value.isBrushEnabled) {
                    drawPixel(action.x, action.y)
                } else {
                    erasePixel(action.x, action.y)
                }
            }

            PaintAction.ClearScreen -> makeClearScreen()
        }
    }

}