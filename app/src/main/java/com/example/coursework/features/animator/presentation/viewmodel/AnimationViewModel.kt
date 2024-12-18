package com.example.coursework.features.animator.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursework.features.animator.data.AnimationData
import com.example.coursework.features.animator.data.AnimationFrameData
import com.example.coursework.features.animator.data.AnimationImageData
import com.example.coursework.features.animator.data.AnimatorRepository
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


const val INITIAL_FRAMES_NUMBER = 30

sealed interface AnimatorUiEvent {

}

sealed interface AnimationNavigationEvent {
    data object NavigateBack : AnimationNavigationEvent
    data class NavigateToImagePaint(val id: Int) : AnimationNavigationEvent
}

sealed interface AnimatorAction {
    data object NavigateBack : AnimatorAction

    data object SaveAnimation : AnimatorAction
    data class UpdateAnimation(val context: Context) : AnimatorAction
    data object SaveAnimationAs : AnimatorAction
    data class UpdateFrameCountForImage(
        val imageId: Int,
        val frames: Int
    ) : AnimatorAction

    data class SaveAnimationWithName(val name: String, val context: Context) : AnimatorAction
    data class AddImageToAnimation(val image: AnimationImageData, val pos: Int) : AnimatorAction
    data class DeleteImageFromAnimation(val pos: Int) : AnimatorAction
    data class NavigateToImagePaint(val image: AnimationImageData) : AnimatorAction

    data class ChangeSaveMenuVisibility(val isVisible: Boolean) : AnimatorAction
    data class ChangeSaveImageWithNameDialogVisibility(val isVisible: Boolean) : AnimatorAction
}

data class AnimatorState(
    val isLoading: Boolean = true,
    val error: Boolean = false,
    val animationName: String? = null,
    val animationId: Int? = null,
    val frames: List<AnimationFrameData> = emptyList(),
    val availableImages: List<AnimationImageData>? = null,

    val isAddImageBottomSheetVisible: Boolean = false,
    val isAddImageBottomSheetLoading: Boolean = false,
    val isSaveMenuVisible: Boolean = false,
    val isSaveImageWithNameDialogVisible: Boolean = false,
)

@HiltViewModel
class AnimatorViewModel @Inject constructor(
    private val animatorRepository: AnimatorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnimatorState())
    val state = _state.asStateFlow()

    private val _uiEvents = MutableSharedFlow<AnimatorUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val _navigationEvents = MutableSharedFlow<AnimationNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    fun getAnimatorScreen(
        animationId: Int?
    ) {
        subscribeToImages()
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                if (animationId != null) {
                    val animation = animatorRepository.getAnimation(animationId)
                    _state.update {
                        it.copy(
                            animationId = animation.id,
                            animationName = animation.title,
                            frames = animation.frames,
                        )
                    }
                }
            } catch (e: Exception) {
                println()
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun subscribeToImages() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                animatorRepository.getImages().collect { list ->
                    _state.update {
                        it.copy(
                            availableImages = list
                        )
                    }
                }
            }
        }
    }


    private suspend fun saveAnimationWithName(animationId: Int?, context: Context) {
        state.value.animationName?.let { name ->
            val id = animatorRepository.saveAnimation(
                AnimationData(
                    id = animationId,
                    title = name,
                    frames = state.value.frames
                ),
                context
            )
            _state.update {
                it.copy(
                    animationId = id
                )
            }
        } ?: _state.update {
            it.copy(isSaveImageWithNameDialogVisible = true)
        }
    }


    fun onAction(action: AnimatorAction) {
        when (action) {
            is AnimatorAction.NavigateBack -> {
                viewModelScope.launch {
                    _navigationEvents.emit(AnimationNavigationEvent.NavigateBack)
                }
            }

            is AnimatorAction.SaveAnimation -> {
                viewModelScope.launch {
                    if (state.value.animationName == null) {
                        _state.update {
                            it.copy(isSaveImageWithNameDialogVisible = true)
                        }
                    } else {
                        _state.update {
                            it.copy(isSaveMenuVisible = true)
                        }
                    }
                }
            }

            is AnimatorAction.SaveAnimationWithName -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(animationName = action.name)
                    }
                    saveAnimationWithName(null, action.context)
                }
            }

            is AnimatorAction.SaveAnimationAs -> {
                _state.update {
                    it.copy(isSaveImageWithNameDialogVisible = true)
                }
            }

            is AnimatorAction.UpdateAnimation -> {
                viewModelScope.launch {
                    saveAnimationWithName(state.value.animationId, action.context)
                }
            }

            is AnimatorAction.AddImageToAnimation -> _state.update {
                it.copy(
                    frames = it.frames.toMutableList().apply {
                        this.add(
                            action.pos,
                            AnimationFrameData(
                                id = 0,
                                image = action.image.bitmap,
                                frameNumber = it.frames.size,
                                frameDuration = INITIAL_FRAMES_NUMBER,
                                imageArray = action.image.imageByteArray,
                                realSize = action.image.size
                            )
                        )
                    }
                )
            }

            is AnimatorAction.DeleteImageFromAnimation -> _state.update {
                it.copy(
                    frames = it.frames.minusElement(it.frames[action.pos])
                )
            }

            is AnimatorAction.NavigateToImagePaint -> viewModelScope.launch {
                _navigationEvents.emit(
                    AnimationNavigationEvent.NavigateToImagePaint(action.image.id)
                )
            }

            is AnimatorAction.ChangeSaveImageWithNameDialogVisibility -> {
                _state.update {
                    it.copy(isSaveImageWithNameDialogVisible = action.isVisible)
                }
            }

            is AnimatorAction.ChangeSaveMenuVisibility -> {
                _state.update {
                    it.copy(isSaveMenuVisible = action.isVisible)
                }
            }

            is AnimatorAction.UpdateFrameCountForImage -> {
                _state.update {
                    it.copy(
                        frames = it.frames.mapIndexed { index, image ->
                            if (index == action.imageId) {
                                image.copy(frameDuration = action.frames)
                            } else image
                        }
                    )
                }
            }
        }
    }

}