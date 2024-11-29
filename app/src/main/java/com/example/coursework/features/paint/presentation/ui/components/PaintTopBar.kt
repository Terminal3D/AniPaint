package com.example.coursework.features.paint.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.coursework.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaintTopBar(
    imageName: String?,
    onBackPressed: () -> Unit,
    onSavePressed: () -> Unit
) {
    TopAppBar(
        title = {
            Text(imageName ?: "Рисовалка")
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        },
        actions = {
            IconButton(onClick = onSavePressed) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_save_24),
                    contentDescription = "Save",
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview
@Composable
fun PaintTopBarPreview() {
    PaintTopBar(
        imageName = "Мона Лиза 2D",
        onBackPressed = {},
        onSavePressed = {}
    )
}
