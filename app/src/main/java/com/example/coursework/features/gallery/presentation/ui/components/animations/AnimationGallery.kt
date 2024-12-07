package com.example.coursework.features.gallery.presentation.ui.components.animations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.coursework.core.ui.AnimationCard
import com.example.coursework.core.ui.AnimationCardItem
import com.example.coursework.core.utils.gifUtils.shareGif
import com.example.coursework.features.animator.presentation.ui.components.getGifDirectory
import com.example.coursework.features.gallery.data.GalleryAnimation
import com.example.coursework.features.gallery.presentation.viewmodel.GalleryAction
import com.example.coursework.features.gallery.presentation.viewmodel.GalleryState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationGallery(
    state: GalleryState,
    onAction: (GalleryAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var sheetIsVisible by remember { mutableStateOf(false) }
    var animationSheetSource: GalleryAnimation? by remember { mutableStateOf(null) }

    val context = LocalContext.current

    if (state.savedAnimations.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Вы еще не создали ни одной анимации.\n Сделаем первую?",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onAction(GalleryAction.NavigateToNewAnimation) }) {
                Text("Создать анимацию")
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(state.savedAnimations) { animation ->
                val gifFile = File(getGifDirectory(context), animation.gifUri)

                AnimationCard(
                    animation = AnimationCardItem(
                        title = animation.title,
                        gifUri = gifFile.toUri()
                    ),
                    onClick = {
                        onAction(GalleryAction.NavigateToAnimationAction(animation))
                    },
                    onLongClick = {
                        animationSheetSource = animation
                        sheetIsVisible = true
                    }
                )
            }
        }
        if (sheetIsVisible) {
            ModalBottomSheet(
                onDismissRequest = {
                    sheetIsVisible = false
                },
                containerColor = ListItemDefaults.containerColor
            ) {
                Column {
                    ListItem(
                        headlineContent = {
                            Text("Поделиться")
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                            )
                        },
                        modifier = Modifier.clickable {
                            animationSheetSource?.gifUri?.let { uri ->
                                val gifFile = File(getGifDirectory(context), uri)
                                shareGif(context, gifFile)
                            }
                        }
                    )
                    ListItem(
                        headlineContent = {
                            Text("Удалить")
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                            )
                        },
                        modifier = Modifier.clickable {
                            onAction(GalleryAction.DeleteAnimationAction(animationSheetSource))
                            sheetIsVisible = false
                        }
                    )
                }
            }
        }
    }
}
