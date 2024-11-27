package com.example.coursework.features.navigation.route

import com.example.coursework.core.models.ImageSize
import kotlinx.serialization.Serializable

sealed interface PaintRoutes {

    @Serializable
    data class Paint(val imageSize: ImageSize) : PaintRoutes

}