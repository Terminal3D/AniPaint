package com.example.coursework.common.database.di

import android.net.Uri
import com.example.coursework.common.database.PaintDao
import com.example.coursework.common.database.entities.AnimationEntity
import com.example.coursework.common.database.entities.AnimationFrameEntity
import com.example.coursework.common.database.entities.SavedAnimationEntity
import com.example.coursework.common.database.entities.SavedImageEntity
import com.example.coursework.common.database.mappers.toLastImageEntity
import com.example.coursework.common.database.mappers.toPaintImage
import com.example.coursework.common.database.mappers.toSavedImageEntity
import com.example.coursework.core.models.PaintImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class PaintDatabaseRepositoryImpl @Inject constructor(
    private val paintDao: PaintDao
) : PaintDatabaseRepository {
    override fun getLastImage(): Flow<PaintImage?> {
        return paintDao.getLastImage().map {
            it?.toPaintImage()
        }
    }

    override suspend fun updateLastImage(image: PaintImage) {
        return paintDao.upsertLastImage(image.toLastImageEntity())
    }

    override suspend fun updateSavedImages(image: PaintImage): Int {
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

    override suspend fun updateAnimation(
        animation: AnimationEntity,
        frames: List<AnimationFrameEntity>
    ) : Int {
        paintDao.deleteAnimationById(animation.id)

        val id = paintDao.upsertAnimation(animation).toInt()
        paintDao.insertAnimationFrames(frames)
        return id
    }

    override suspend fun insertAnimation(
        animation: AnimationEntity,
        frames: List<AnimationFrameEntity>,
    ): Int {
        val id = paintDao.upsertAnimation(animation).toInt()
        val framesWithId = frames.map { it.copy(animationId = id) }
        paintDao.insertAnimationFrames(framesWithId)

        return id
    }

    override suspend fun insertSavedAnimation(animation: SavedAnimationEntity) {
        paintDao.insertSavedAnimation(animation)
    }


    override fun getAnimationWithFramesById(id: Int): Pair<Flow<AnimationEntity>, Flow<List<AnimationFrameEntity>>> {
        return Pair(paintDao.getAnimationById(id), paintDao.getAnimationFramesById(id))
    }

    override suspend fun deleteAnimationById(id: Int) {
        withContext(Dispatchers.IO) {
            val uri = paintDao.getSavedAnimationById(id).uri
            paintDao.deleteAnimationById(id)
            paintDao.deleteSavedAnimationById(id)
            deleteGifFile(uri)
        }
    }

    override fun getSavedAnimations(): Flow<List<SavedAnimationEntity>> {
        return paintDao.getSavedAnimations()
    }

    override suspend fun getSavedAnimationById(animationId: Int): SavedAnimationEntity {
        return paintDao.getSavedAnimationById(animationId)
    }

    private fun deleteGifFile(uriString: String) {
        val uri = Uri.parse(uriString)
        val file = File(uri.path ?: return)
        if (file.exists()) {
            file.delete()
        }
    }
}