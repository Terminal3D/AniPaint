package com.example.coursework.features.animator.data

import android.graphics.Bitmap
import com.example.coursework.common.database.entities.SavedImageEntity
import com.example.coursework.core.utils.ImageUtils.getBitmap

data class AnimationImageData(
    val id: Int,
    val title: String,
    val size: Int,
    val bitmap: Bitmap,
    val imageByteArray: ByteArray
)

fun SavedImageEntity.toAnimationImageData() : AnimationImageData {
    return AnimationImageData(
        id = this.id,
        title = this.name,
        size = this.size,
        bitmap = this.image.getBitmap(actualSize = this.size),
        imageByteArray = this.image
    )
}