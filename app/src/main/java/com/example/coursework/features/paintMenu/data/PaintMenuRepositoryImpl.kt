package com.example.coursework.features.paintMenu.data

import com.example.coursework.common.database.di.PaintDatabaseRepository
import com.example.coursework.core.models.ImageSize
import com.example.coursework.core.models.PaintImage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PaintMenuRepositoryImpl @Inject constructor(
    private val paintDatabaseRepository: PaintDatabaseRepository
) : PaintMenuRepository {
    override fun getImageSizes(): List<ImageSize> {
        return ImageSize.entries
    }

    override fun getLastImage(): Flow<PaintImage?> {
        return paintDatabaseRepository.getLastImage()
    }
}