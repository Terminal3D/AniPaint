package com.example.coursework.features.gallery.data

import android.graphics.Bitmap
import com.example.coursework.common.database.entities.SavedImageEntity

data class GalleryImage(
    val id: Int,
    val image: Bitmap,
    val size: Int,
    val title: String
)

internal fun SavedImageEntity.toGalleryImage(): GalleryImage {
    return GalleryImage(
        id = this.id,
        image = this.getBitmap(),
        size = this.size,
        title = this.name
    )
}
