package com.example.coursework.features.gallery.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coursework.features.gallery.presentation.ui.components.GalleryImageCard
import com.example.coursework.features.gallery.presentation.viewmodel.GalleryAction
import com.example.coursework.features.gallery.presentation.viewmodel.GalleryState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    state: GalleryState,
    onAction: (GalleryAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Изображения") }
            )
        }
    ) { paddingValues ->
        if (state.savedImages.isEmpty()) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Вы еще не сохранили ни одного изображения.\n Нарисуем первое?")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onAction(GalleryAction.NavigateToPaintMenuAction) }) {
                    Text("Создать изображение")
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(state.savedImages) { image ->
                    GalleryImageCard(
                        image = image,
                        onClick = {
                            onAction(GalleryAction.NavigateToImageAction(image))
                        },
                        onDelete = {
                            onAction(GalleryAction.DeleteImageAction(image))
                        },
                        onShare = {
                            onAction(GalleryAction.ShareImageAction(image))
                        }
                    )
                }
            }
        }
    }
}
