package com.example.coursework.common.database.di

import com.example.coursework.common.database.entities.AnimationEntity
import com.example.coursework.common.database.entities.AnimationFrameEntity
import com.example.coursework.common.database.entities.SavedAnimationEntity
import com.example.coursework.common.database.entities.SavedImageEntity
import com.example.coursework.core.models.PaintImage
import kotlinx.coroutines.flow.Flow

interface PaintDatabaseRepository {

    fun getLastImage(): Flow<PaintImage?>

    suspend fun updateLastImage(image: PaintImage)

    fun getSavedImages(): Flow<List<SavedImageEntity>>

    suspend fun updateSavedImages(image: PaintImage): Int

    suspend fun deleteSavedImage(image: PaintImage)

    fun getImageById(id: Int): PaintImage

    fun deleteImageById(id: Int)

    suspend fun updateAnimation(animation: AnimationEntity, frames: List<AnimationFrameEntity>) : Int
    suspend fun insertAnimation(animation: AnimationEntity, frames: List<AnimationFrameEntity>) : Int
    suspend fun insertSavedAnimation(animation: SavedAnimationEntity)

    fun getAnimationWithFramesById(id: Int): Pair<Flow<AnimationEntity>, Flow<List<AnimationFrameEntity>>>

    suspend fun deleteAnimationById(id: Int)

    fun getSavedAnimations(): Flow<List<SavedAnimationEntity>>

    suspend fun getSavedAnimationById(animationId: Int): SavedAnimationEntity?


}