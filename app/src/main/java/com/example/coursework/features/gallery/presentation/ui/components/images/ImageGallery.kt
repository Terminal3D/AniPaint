package com.example.coursework.features.gallery.presentation.ui.components.images

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.coursework.core.utils.ShareUtils
import com.example.coursework.features.gallery.data.GalleryImage
import com.example.coursework.features.gallery.presentation.viewmodel.GalleryAction
import com.example.coursework.features.gallery.presentation.viewmodel.GalleryState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageGallery(
    state: GalleryState,
    onAction: (GalleryAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var sheetIsVisible by remember { mutableStateOf(false) }
    var imageSheetSource: GalleryImage? by remember { mutableStateOf(null) }

    val context = LocalContext.current

    val shareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {

    }

    if (state.savedImages.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Вы еще не сохранили ни одного изображения.\n Нарисуем первое?",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onAction(GalleryAction.NavigateToPaintMenuAction) }) {
                Text("Создать изображение")
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(state.savedImages) { image ->
                GalleryImageCard(
                    image = image,
                    onClick = {
                        onAction(GalleryAction.NavigateToImageAction(image))
                    },
                    onLongClick = {
                        imageSheetSource = image
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
                            imageSheetSource?.let { image ->
                                val bitmap: Bitmap =
                                    image.image // Предполагается, что GalleryImage содержит Bitmap

                                val file = ShareUtils.saveBitmapToCache(
                                    context = context,
                                    bitmap = bitmap,
                                    fileName = "{${image.title}}_${image.id}.png" // Предполагается, что у GalleryImage есть id
                                )
                                val uri = ShareUtils.getImageUri(context, file)

                                // Создаем Intent для обмена
                                val shareIntent = ShareUtils.createShareIntent(uri)

                                // Запускаем Intent через лаунчер
                                shareLauncher.launch(
                                    Intent.createChooser(
                                        shareIntent,
                                        "Поделиться изображением через"
                                    )
                                )
                            }

                            sheetIsVisible = false
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
                            onAction(GalleryAction.DeleteImageAction(imageSheetSource))
                            sheetIsVisible = false
                        }
                    )
                }
            }
        }
    }
}
