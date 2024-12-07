package com.example.coursework.features.gallery.data

import android.graphics.Bitmap
import com.example.coursework.common.database.entities.SavedImageEntity
import com.example.coursework.core.utils.ImageUtils.getBitmap

data class GalleryImage(
    val id: Int,
    val bitmap: Bitmap,
    val size: Int,
    val title: String
)

internal fun SavedImageEntity.toGalleryImage(): GalleryImage {
    return GalleryImage(
        id = this.id,
        bitmap = this.image.getBitmap(actualSize = this.size),
        size = this.size,
        title = this.name
    )
}
