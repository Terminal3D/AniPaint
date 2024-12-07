package com.example.coursework.features.animator.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.coursework.R
import com.example.coursework.core.models.ImageCardItem
import com.example.coursework.core.models.SliderItem
import com.example.coursework.core.ui.ImageCard
import com.example.coursework.core.ui.ImageSlider
import com.example.coursework.core.ui.SaveImageDialog
import com.example.coursework.core.ui.topbar.EditorTopBar
import com.example.coursework.features.animator.presentation.ui.components.AnimationPlayerDialog
import com.example.coursework.features.animator.presentation.ui.components.AnimatorBottomBar
import com.example.coursework.features.animator.presentation.viewmodel.AnimatorAction
import com.example.coursework.features.animator.presentation.viewmodel.AnimatorState
import com.example.coursework.features.animator.presentation.viewmodel.AnimatorUiEvent
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatorScreen(
    state: AnimatorState,
    uiEvents: Flow<AnimatorUiEvent>,
    onAction: (AnimatorAction) -> Unit,
    modifier: Modifier = Modifier
) {

    var bottomSheetVisible by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            state.frames.size.plus(1)
        }
    )

    val context = LocalContext.current

    var showAnimationDialog by remember { mutableStateOf(false) }

    if (showAnimationDialog) {
        AnimationPlayerDialog(
            frames = state.frames,
            onDismiss = { showAnimationDialog = false }
        )
    }

    if (state.isSaveImageWithNameDialogVisible) {
        SaveImageDialog(
            onSave = {
                onAction(AnimatorAction.SaveAnimationWithName(it, context))
            },
            onDismiss = {
                onAction(AnimatorAction.ChangeSaveImageWithNameDialogVisibility(false))
            },
            imageName = state.animationName,
            title = "Введите название для анимации",
            placeholder = "Имя анимации"
        )
    }

    Scaffold(
        topBar = {
            EditorTopBar(
                name = state.animationName ?: "Анимашка",
                onBackPressed = {
                    onAction(AnimatorAction.NavigateBack)
                },
                onSavePressed = {
                    onAction(AnimatorAction.SaveAnimation)
                },
                showSaveMenu = state.isSaveMenuVisible,
                onHideSaveMenu = {
                    onAction(AnimatorAction.ChangeSaveMenuVisibility(false))
                },
                onUpdatePressed = {
                    onAction(AnimatorAction.UpdateAnimation(context))
                },
                onSaveAsPressed = {
                    onAction(AnimatorAction.SaveAnimationAs)
                },
                additionalIcons = {
                    IconButton(
                        onClick = {
                            showAnimationDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_play_circle_outline_24),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            AnimatorBottomBar(
                isEnabled = true,
                onSlideFinished = {
                    onAction(AnimatorAction.UpdateFrameCountForImage(
                        imageId = pagerState.currentPage,
                        frames = it
                    ))
                },
                stateSliderValue = if (pagerState.currentPage < state.frames.size) {
                    state.frames[pagerState.currentPage].frameDuration
                } else 24
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                ImageSlider(
                    imageList = buildList {
                        state.frames.map { frame ->
                            SliderItem.Image(
                                bitmap = frame.image,
                                onClick = {}
                            )
                        }.let { addAll(it) }
                        add(SliderItem.NewImage(
                            onClick = {
                                bottomSheetVisible = true
                            }
                        ))
                    },
                    pagerState = pagerState
                )
            }
        }

        if (bottomSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = {
                   bottomSheetVisible = false
                },
                containerColor = ListItemDefaults.containerColor
            ) {
                if (state.availableImages == null) {
                    Text("Пока не добавили ни одной картинки")
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(state.availableImages) { image ->
                            ImageCard(
                                image = ImageCardItem(
                                    title = image.title,
                                    size = image.size.toString(),
                                    bitmap = image.bitmap
                                ),
                                onClick = {
                                    bottomSheetVisible = false
                                    onAction(AnimatorAction.AddImageToAnimation(image))
                                },
                                onLongClick = {
                                    bottomSheetVisible = false
                                    onAction(AnimatorAction.NavigateToImagePaint(image))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
