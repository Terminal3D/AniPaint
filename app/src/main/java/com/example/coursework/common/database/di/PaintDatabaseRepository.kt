package com.example.coursework.common.database.di

import com.example.coursework.common.database.entities.AnimationEntity
import com.example.coursework.common.database.entities.AnimationFrameEntity
import com.example.coursework.common.database.entities.SavedImageEntity
import com.example.coursework.core.models.PaintImage
import kotlinx.coroutines.flow.Flow

interface PaintDatabaseRepository {

    fun getLastImage() : Flow<PaintImage?>

    suspend fun updateLastImage(image: PaintImage)

    fun getSavedImages() : Flow<List<SavedImageEntity>>

    suspend fun updateSavedImages(image: PaintImage) : Int

    suspend fun deleteSavedImage(image: PaintImage)

    fun getImageById(id: Int): PaintImage

    fun deleteImageById(id: Int)

    suspend fun updateAnimation(animation: AnimationEntity, frames: List<AnimationFrameEntity>)
    fun getAllAnimations(): Flow<List<AnimationEntity>>
    fun getAnimationById(id: Int): Flow<AnimationEntity>
    suspend fun deleteAnimation(animation: AnimationEntity)
    suspend fun deleteAnimationById(id: Int)
}