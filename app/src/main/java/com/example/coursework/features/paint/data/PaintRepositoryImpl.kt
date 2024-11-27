package com.example.coursework.features.paint.data

import com.example.coursework.common.database.di.PaintDatabaseRepository
import com.example.coursework.core.models.ImageSize
import com.example.coursework.core.models.PaintImage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PaintRepositoryImpl @Inject constructor(private val paintDatabaseRepository: PaintDatabaseRepository) :
    PaintRepository {
    override suspend fun saveLastImage(imageSize: ImageSize, pixels: List<IntArray>) {
        paintDatabaseRepository.updateLastImage(
            PaintImage(
                pixels = pixels,
                imageSize = imageSize
            )
        )
    }

    override fun getLastImage(): Flow<PaintImage?> {
        return paintDatabaseRepository.getLastImage()
    }
}