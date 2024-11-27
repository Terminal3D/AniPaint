package com.example.coursework.features.paintMenu.data

import com.example.coursework.core.models.ImageSize
import com.example.coursework.core.models.PaintImage
import javax.inject.Inject

class PaintMenuRepositoryImpl @Inject constructor() : PaintMenuRepository {
    override fun getImageSizes(): List<ImageSize> {
        return ImageSize.entries
    }

    override fun getLastImage(): PaintImage? {
        return null
    }
}