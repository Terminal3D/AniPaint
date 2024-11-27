package com.example.coursework.features.paintMenu.data

import com.example.coursework.core.models.ImageSize
import com.example.coursework.core.models.PaintImage

interface PaintMenuRepository {
    fun getImageSizes() : List<ImageSize>

    fun getLastImage() : PaintImage?
}