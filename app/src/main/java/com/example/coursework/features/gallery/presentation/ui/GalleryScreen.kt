package com.example.coursework.features.gallery.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.coursework.features.gallery.presentation.ui.components.animations.AnimationGallery
import com.example.coursework.features.gallery.presentation.ui.components.images.ImageGallery
import com.example.coursework.features.gallery.presentation.viewmodel.GalleryAction
import com.example.coursework.features.gallery.presentation.viewmodel.GalleryState
import com.example.coursework.features.gallery.presentation.viewmodel.GalleryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    state: GalleryState,
    onAction: (GalleryAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Галерея") },
                actions = {
                    if (state.galleryType == GalleryType.ANIMATIONS) {
                        IconButton(onClick = {
                            onAction(GalleryAction.NavigateToNewAnimation)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            TabRow(
                selectedTabIndex = state.galleryType.ordinal,
                modifier = Modifier.fillMaxWidth(),
            ) {
                GalleryType.entries.forEach { type ->
                    Tab(
                        selected = state.galleryType == type,
                        onClick = { onAction(GalleryAction.SwitchGalleryType(type)) },
                        text = { Text(type.value.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
            when (state.galleryType) {
                GalleryType.IMAGES -> ImageGallery(
                    state,
                    onAction,
                    modifier = modifier
                )

                GalleryType.ANIMATIONS -> {
                    AnimationGallery(
                        state,
                        onAction,
                        modifier = modifier
                    )
                }
            }
        }
    }
}
