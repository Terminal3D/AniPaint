package com.example.coursework.common.database.di

import com.example.coursework.common.database.PaintDao
import com.example.coursework.common.database.entities.AnimationEntity
import com.example.coursework.common.database.entities.AnimationFrameEntity
import com.example.coursework.common.database.entities.SavedImageEntity
import com.example.coursework.common.database.mappers.toLastImageEntity
import com.example.coursework.common.database.mappers.toPaintImage
import com.example.coursework.common.database.mappers.toSavedImageEntity
import com.example.coursework.core.models.PaintImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PaintDatabaseRepositoryImpl @Inject constructor(
    private val paintDao: PaintDao
) : PaintDatabaseRepository {
    override fun getLastImage(): Flow<PaintImage?>{
        return paintDao.getLastImage().map {
            it?.toPaintImage()
        }
    }

    override suspend fun updateLastImage(image: PaintImage) {
        return paintDao.upsertLastImage(image.toLastImageEntity())
    }

    override suspend fun updateSavedImages(image: PaintImage) : Int {
        val savedImageEntity = image.toSavedImageEntity()
        return paintDao.upsertSavedImage(savedImageEntity).toInt()
    }

    override fun getSavedImages(): Flow<List<SavedImageEntity>> {
        return paintDao.getSavedImages()
    }

    override suspend fun deleteSavedImage(image: PaintImage) {
        paintDao.deleteSavedImage(image.toSavedImageEntity())
    }

    override fun getImageById(id: Int): PaintImage {
        return paintDao.getImageById(id).toPaintImage()
    }

    override fun deleteImageById(id: Int) {
        paintDao.deleteImageById(id)
    }

    override suspend fun updateAnimation(animation: AnimationEntity, frames: List<AnimationFrameEntity>) {
//        paintDao.deleteAnimationById(animation.id)

        paintDao.insertAnimation(animation)

        frames.forEach {
            paintDao.insertAnimationFrame(it)
        }
    }

    override fun getAllAnimations(): Flow<List<AnimationEntity>> {
        return paintDao.getAllAnimations()
    }

    override fun getAnimationById(id: Int): Flow<AnimationEntity> {
        return paintDao.getAnimationById(id)
    }

    override suspend fun deleteAnimation(animation: AnimationEntity) {
        paintDao.deleteAnimation(animation)
    }

    override suspend fun deleteAnimationById(id: Int) {
        paintDao.deleteAnimationById(id)
    }

}