package com.example.coursework.features.paint.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.coursework.R
import com.example.coursework.core.ui.SaveImageDialog
import com.example.coursework.core.ui.topbar.EditorTopBar
import com.example.coursework.features.paint.presentation.ui.components.ColorPickerDialog
import com.example.coursework.features.paint.presentation.ui.components.PaintBottomBar
import com.example.coursework.features.paint.presentation.viewmodel.PaintAction
import com.example.coursework.features.paint.presentation.viewmodel.PaintState
import com.example.coursework.features.paint.presentation.viewmodel.PaintUiEvent
import kotlinx.coroutines.flow.Flow
import kotlin.math.roundToInt

@Composable
fun PaintScreen(
    state: PaintState,
    uiEvents: Flow<PaintUiEvent>,
    onAction: (PaintAction) -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        topBar = {
            EditorTopBar(
                name = state.imageName ?: "Рисовалка",
                onBackPressed = {
                    onAction(PaintAction.NavigateBack)
                },
                onSavePressed = {
                    onAction(PaintAction.SaveImage)
                },
                showSaveMenu = state.isSaveMenuVisible,
                onHideSaveMenu = {
                    onAction(PaintAction.ChangeSaveMenuVisibility(false))
                },
                onUpdatePressed = {
                    onAction(PaintAction.UpdateImage)
                },
                onSaveAsPressed = {
                    onAction(PaintAction.SaveImageAs)
                }
            )
        },
        bottomBar = {
            PaintBottomBar(
                state = state,
                onAction = onAction
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.tertiary),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                if (state.isClearDialogVisible) {
                    AlertDialog(
                        onDismissRequest = { onAction(PaintAction.ChangeClearDialogVisibility(false)) },
                        title = { Text("Вы уверены?") },
                        text = { Text("Вы хотите очистить экран? Все данные будут потеряны.") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    onAction(PaintAction.ClearScreen)
                                    onAction(PaintAction.ChangeClearDialogVisibility(false))
                                }
                            ) {
                                Text("Да")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { onAction(PaintAction.ChangeClearDialogVisibility(false)) }
                            ) {
                                Text("Нет")
                            }
                        }
                    )
                }

                if (state.isColorPickerDialogVisible) {
                    ColorPickerDialog(
                        initialColor = state.currentColor,
                        onColorSelected = { onAction(PaintAction.SelectColor(it)) },
                        onDismiss = { onAction(PaintAction.ChangeColorPickerVisibility(false)) }
                    )
                }

                if (state.isSaveImageWithNameDialogVisible) {
                    SaveImageDialog(
                        onSave = {
                            onAction(PaintAction.SaveImageWithName(it))
                        },
                        onDismiss = {
                            onAction(PaintAction.ChangeSaveImageWithNameDialogVisibility(false))
                        },
                        imageName = state.imageName,
                        title = "Введите название для изображения",
                        placeholder = "Имя изображения"
                    )
                }

                var recomposeTrigger by remember { mutableIntStateOf(0) }


                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(
                            color = Color.Transparent,
                            shape = MaterialTheme.shapes.small
                        ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onAction(PaintAction.ChangeGridVisibility(!state.isGridVisible)) },
                        modifier = Modifier
                            .background(
                                color = if (state.isGridVisible) MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.1f
                                ) else Color.Transparent,
                                shape = MaterialTheme.shapes.small
                            ),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = if (state.isGridVisible) {
                                ImageVector.vectorResource(R.drawable.baseline_grid_off_24)
                            } else {
                                ImageVector.vectorResource(R.drawable.baseline_grid_on_24)
                            },
                            contentDescription = "Toggle Grid",
                            tint = if (state.isGridVisible) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }

                    IconButton(
                        onClick = {
                            onAction(PaintAction.ChangeClearDialogVisibility(true))
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Очистить изображение",

                        )
                    }

                    // Кнопка Undo
                    IconButton(
                        onClick = { onAction(PaintAction.UndoLastAction) },
                        enabled = state.canUndo,
                        modifier = Modifier
                            .background(
                                color = if (state.canUndo) MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.1f
                                ) else Color.Transparent,
                                shape = MaterialTheme.shapes.small
                            ),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_undo_24),
                            contentDescription = "Undo",
                            tint = if (state.canUndo) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }

                    // Кнопка Redo
                    IconButton(
                        onClick = { onAction(PaintAction.RedoLastAction) },
                        enabled = state.canRedo,
                        modifier = Modifier
                            .background(
                                color = if (state.canRedo) MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.1f
                                ) else Color.Transparent,
                                shape = MaterialTheme.shapes.small
                            ),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_redo_24),
                            contentDescription = "Redo",
                            tint = if (state.canRedo) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }

                Box {
                    Canvas(
                        modifier = Modifier
                            .size(256.dp)
                            .background(Color.White)
                            .border(1.dp, Color.Gray)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { change, _ ->
                                        change.consume()
                                        val touchX = change.position.x
                                        val touchY = change.position.y

                                        val pixelX = (touchX / size.width * state.imageSize.size)
                                            .toInt()
                                            .coerceIn(0, state.imageSize.size - 1)
                                        val pixelY = (touchY / size.height * state.imageSize.size)
                                            .toInt()
                                            .coerceIn(0, state.imageSize.size - 1)

                                        onAction(PaintAction.DrawPixel(pixelX, pixelY))

                                        // Инкрементируем recomposeTrigger для рекомпозиции
                                        recomposeTrigger++
                                    }
                                )
                            }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = { offset ->
                                        val pixelX =
                                            (offset.x / size.width * state.imageSize.size)
                                                .toInt()
                                                .coerceIn(0, state.imageSize.size - 1)
                                        val pixelY =
                                            (offset.y / size.height * state.imageSize.size)
                                                .toInt()
                                                .coerceIn(0, state.imageSize.size - 1)

                                        onAction(PaintAction.DrawPixel(pixelX, pixelY))
                                        recomposeTrigger++
                                    }
                                )
                            }
                    ) {
                        var trigger = recomposeTrigger
                        // Отрисовка пикселей
                        val currentPixels = state.pixels
                        val pixelSize = size.width / state.imageSize.size.toFloat()

                        for (x in 0 until state.imageSize.size) {
                            for (y in 0 until state.imageSize.size) {
                                val color = Color(currentPixels[x][y])
                                drawRect(
                                    color = color,
                                    topLeft = Offset(x * pixelSize, y * pixelSize),
                                    size = androidx.compose.ui.geometry.Size(pixelSize, pixelSize)
                                )
                            }
                        }

                        // Отрисовка сетки, если включена
                        if (state.isGridVisible) {
                            val gridStep = size.width / state.imageSize.size.toFloat()
                            for (i in 0..state.imageSize.size) {
                                // Вертикальные линии
                                drawLine(
                                    color = Color.DarkGray,
                                    start = Offset(x = i * gridStep, y = 0f),
                                    end = Offset(x = i * gridStep, y = size.height),
                                    strokeWidth = 0.5f
                                )
                                // Горизонтальные линии
                                drawLine(
                                    color = Color.LightGray,
                                    start = Offset(x = 0f, y = i * gridStep),
                                    end = Offset(x = size.width, y = i * gridStep),
                                    strokeWidth = 0.5f
                                )
                            }
                        }
                    }
                }

                if (state.isCircleToolSelected) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Радиус окружности: ${state.currentCircleRadius.roundToInt()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Slider(
                            value = state.currentCircleRadius,
                            onValueChange = { newRadius ->
                                onAction(PaintAction.ChangeCircleRadius(newRadius))
                            },
                            valueRange = 1f..24f,
                            steps = 24,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    }
}

