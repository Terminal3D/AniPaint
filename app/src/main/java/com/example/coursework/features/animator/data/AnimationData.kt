package com.example.coursework.features.animator.data

import android.content.Context
import android.graphics.Bitmap
import com.example.coursework.common.database.entities.AnimationEntity
import com.example.coursework.common.database.entities.AnimationFrameEntity
import com.example.coursework.common.database.entities.SavedAnimationEntity
import com.example.coursework.core.utils.ImageUtils.getBitmap
import com.example.coursework.core.utils.gifUtils.createGif

data class AnimationData(
    val id: Int? = null,
    val title: String,
    val frames: List<AnimationFrameData>,
)

data class AnimationFrameData(
    val id: Int,
    val frameNumber: Int,
    val frameDuration: Int,
    val image: Bitmap,
    val imageArray: ByteArray,
    val realSize: Int
)

fun AnimationEntity.toAnimationData(frames: List<AnimationFrameEntity>): AnimationData {
    return AnimationData(
        id = this.id,
        title = this.name,
        frames = frames.map {
            AnimationFrameData(
                id = it.id,
                frameNumber = it.frameNumber,
                frameDuration = it.frameDuration,
                image = it.image.getBitmap(actualSize = it.realSize, targetSize = 256),
                imageArray = it.image,
                realSize = it.realSize
            )
        },
    )
}

fun AnimationData.toAnimationEntity() : AnimationEntity {
    return AnimationEntity(
        id = this.id ?: 0,
        name = this.title
    )
}

fun AnimationFrameData.toAnimationFrameEntity(animationId: Int, wipeId: Boolean) : AnimationFrameEntity {
    return AnimationFrameEntity(
        id = if (wipeId) 0 else this.id,
        animationId = animationId,
        frameNumber = this.frameNumber,
        frameDuration = this.frameDuration,
        image = this.imageArray,
        realSize = this.realSize
    )
}

fun AnimationData.toSavedAnimationEntity(context: Context) : SavedAnimationEntity? {
    return this.id?.let {
        val fps = 24f

        val bitmaps = frames.map { it.image.copy(it.image.config ?: Bitmap.Config.ARGB_8888, false) }

        val frameDurationsInSeconds = frames.map { (it.frameDuration.toFloat() / fps) }

        val outputFileName = "animation_${this.id}.gif"


        val success = createGif(context, bitmaps, frameDurationsInSeconds, outputFileName)
        if (success) {
            return@let SavedAnimationEntity(
                id = this.id,
                title = this.title,
                uri = outputFileName
            )
        }
        return@let null
    }
}
