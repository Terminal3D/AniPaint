package com.example.coursework.features.paint.data

import com.example.coursework.common.database.di.PaintDatabaseRepository
import com.example.coursework.core.models.ImageSize
import com.example.coursework.core.models.PaintImage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PaintRepositoryImpl @Inject constructor(private val paintDatabaseRepository: PaintDatabaseRepository) :
    PaintRepository {
    override suspend fun saveLastImage(
        imageSize: ImageSize,
        pixels: List<IntArray>,
        name: String?,
        id: Int?
    ) {
        paintDatabaseRepository.updateLastImage(
            PaintImage(
                pixels = pixels,
                imageSize = imageSize,
                name = name,
                id = id ?: 0
            )
        )
    }

    override fun getLastImage(): Flow<PaintImage?> {
        return paintDatabaseRepository.getLastImage()
    }

    override suspend fun saveImage(imageSize: ImageSize, pixels: List<IntArray>, name: String, id: Int?) : Int {
        return paintDatabaseRepository.updateSavedImages(
            PaintImage(
                id = id,
                name = name,
                imageSize = imageSize,
                pixels = pixels
            )
        )
    }

    override fun getImageById(id: Int): PaintImage {
        return paintDatabaseRepository.getImageById(id)
    }
}