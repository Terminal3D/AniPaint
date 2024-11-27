package com.example.coursework.features.paint.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coursework.R
import com.example.coursework.features.paint.presentation.viewmodel.PaintAction
import com.example.coursework.features.paint.presentation.viewmodel.PaintState

@Composable
fun PaintBottomBar(
    state: PaintState,
    onAction: (PaintAction) -> Unit
) {
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
                onClick = {
                    onAction(PaintAction.ChangeColorPickerVisibility(isVisible = true))
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_palette_24),
                    contentDescription = "Выбрать цвет",
                    tint = state.currentColor
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

            IconButton(
                onClick = { onAction(PaintAction.ChangeGridVisibility(!state.isGridVisible)) }
            ) {
                Icon(
                    imageVector = if (state.isGridVisible) {
                        ImageVector.vectorResource(R.drawable.baseline_grid_off_24)
                    } else {
                        ImageVector.vectorResource(R.drawable.baseline_grid_on_24)
                    },
                    contentDescription = null
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                onClick = { onAction(PaintAction.ChangeClearDialogVisibility(true)) },
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Очистить изображение",
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
