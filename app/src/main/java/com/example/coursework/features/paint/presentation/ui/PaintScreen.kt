package com.example.coursework.features.paint.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.coursework.features.paint.presentation.ui.components.ColorPickerDialog
import com.example.coursework.features.paint.presentation.ui.components.PaintBottomBar
import com.example.coursework.features.paint.presentation.ui.components.PaintTopBar
import com.example.coursework.features.paint.presentation.viewmodel.PaintAction
import com.example.coursework.features.paint.presentation.viewmodel.PaintState
import com.example.coursework.features.paint.presentation.viewmodel.PaintUiEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun PaintScreen(
    state: PaintState,
    uiEvents: Flow<PaintUiEvent>,
    onAction: (PaintAction) -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        topBar = {
            PaintTopBar(
                onBackPressed = {
                    onAction(PaintAction.NavigateBack)
                },
                onSavePressed = {}
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
                        onColorSelected = { onAction(PaintAction.SelectColor(it)) },
                        onDismiss = { onAction(PaintAction.ChangeColorPickerVisibility(false)) }
                    )
                }

                var recomposeTrigger by remember { mutableIntStateOf(0) }


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
            }
        }
    }
}

