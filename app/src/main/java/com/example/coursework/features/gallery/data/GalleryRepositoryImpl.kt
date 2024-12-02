package com.example.coursework.features.gallery.data

import com.example.coursework.common.database.di.PaintDatabaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(
    private val paintDatabaseRepository: PaintDatabaseRepository
) : GalleryRepository {

    override fun getSavedImages(): Flow<List<GalleryImage>> {
        return paintDatabaseRepository.getSavedImages().map { list ->
            list.map { it.toGalleryImage() }
        }
    }

    override suspend fun deleteSavedImage(image: GalleryImage) = withContext(Dispatchers.IO) {
        paintDatabaseRepository.deleteImageById(image.id)
    }

}