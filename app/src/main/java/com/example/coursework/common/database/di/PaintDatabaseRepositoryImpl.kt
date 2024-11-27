package com.example.coursework.common.database.di

import com.example.coursework.common.database.PaintDao
import com.example.coursework.common.database.mappers.toLastImageEntity
import com.example.coursework.common.database.mappers.toPaintImage
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
}