package com.example.coursework.features.paintMenu.data

import com.example.coursework.core.models.ImageSize
import com.example.coursework.core.models.PaintImage
import kotlinx.coroutines.flow.Flow

interface PaintMenuRepository {
    fun getImageSizes() : List<ImageSize>

    fun getLastImage() : Flow<PaintImage?>
}