package com.example.coursework.features.paint.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coursework.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaintTopBar(
    onBackPressed: () -> Unit,
    onSavePressed: () -> Unit
) {
    TopAppBar(
        title = {
            Text("Рисовалка")
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
        onBackPressed = {},
        onSavePressed = {}
    )
}
