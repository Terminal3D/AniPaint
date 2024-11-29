package com.example.coursework.features.paint.data

import com.example.coursework.core.models.ImageSize
import com.example.coursework.core.models.PaintImage
import kotlinx.coroutines.flow.Flow

interface PaintRepository {

    suspend fun saveLastImage(imageSize: ImageSize, pixels: List<IntArray>, name: String?, id: Int?)

    fun getLastImage(): Flow<PaintImage?>

    suspend fun saveImage(imageSize: ImageSize, pixels: List<IntArray>, name: String, id: Int?): Int

    fun getImageById(id: Int): PaintImage
}