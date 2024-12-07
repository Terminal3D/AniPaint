package com.example.coursework.features.paint.presentation.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coursework.R
import com.example.coursework.features.paint.presentation.viewmodel.PaintAction
import com.example.coursework.features.paint.presentation.viewmodel.PaintState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PaintBottomBar(
    state: PaintState,
    onAction: (PaintAction) -> Unit
) {
    var brushSizeMenuExpanded by remember { mutableStateOf(false) }
    var figureOptionsVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    BottomAppBar(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        actions = {
            IconButton(
                onClick = { onAction(PaintAction.ToggleBrush) }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_brush_24),
                    contentDescription = "Включить кисть",
                    tint = if (state.isBrushEnabled) MaterialTheme.colorScheme.primary else Color.LightGray
                )
            }

            IconButton(
                onClick = { onAction(PaintAction.SelectEraser) }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_eraser_24),
                    contentDescription = "Выбрать ластик",
                    tint = if (state.isEraserEnabled) MaterialTheme.colorScheme.primary else Color.LightGray
                )
            }
            Box {
                IconButton(onClick = { brushSizeMenuExpanded = true }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_line_weight_24),
                        contentDescription = "Выбрать размер кисти",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                DropdownMenu(
                    expanded = brushSizeMenuExpanded,
                    onDismissRequest = { brushSizeMenuExpanded = false }
                ) {
                    for (size in 1..4) {
                        DropdownMenuItem(
                            text = { Text("Размер кисти $size") },
                            onClick = {
                                onAction(PaintAction.ChangeBrushSize(size))
                                brushSizeMenuExpanded = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = { onAction(PaintAction.SelectFill) },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_format_color_fill_24),
                    contentDescription = "Заливка",
                    tint = if (state.isFillEnabled) MaterialTheme.colorScheme.primary else Color.LightGray
                )
            }

            Box {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(100))
                        .background(
                            color = if (state.isCircleToolSelected || state.isLineToolSelected) MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.1f
                            ) else Color.Transparent,
                            shape = MaterialTheme.shapes.small
                        )
                        .combinedClickable(
                            onClick = { onAction(state.currentTool.action) },
                            onLongClick = {
                                figureOptionsVisible = true
                            }
                        )
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(state.currentTool.icon),
                        contentDescription = "Draw Line",
                        tint = if (state.isLineToolSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                    )
                }

                // Колонка с дополнительными опциями фигур
                DropdownMenu(
                    expanded = figureOptionsVisible,
                    onDismissRequest = { figureOptionsVisible = false },
                ) {
                    state.toolList.forEach { tool ->
                        DropdownMenuItem(
                            onClick = {
                                onAction(tool.action)
                                figureOptionsVisible = false
                            },
                            text = { Text(tool.displayName) },
                            leadingIcon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(tool.icon),
                                    contentDescription = tool.displayName,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    onAction(PaintAction.ChangeClearDialogVisibility(true))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Очистить изображение",
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = state.currentColor,
                onClick = { onAction(PaintAction.ChangeColorPickerVisibility(isVisible = true)) },
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_palette_24),
                    contentDescription = "Выбрать цвет",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Preview
@Composable
fun PaintBottomBarPreview() {
    PaintBottomBar(
        state = PaintState()
    ) { }
}
