package com.example.coursework.features.navigation.route

import com.example.coursework.core.models.ImageSize
import kotlinx.serialization.Serializable

sealed interface PaintRoutes {

    @Serializable
    data class Paint(
        val imageSize: ImageSize? = null,
        val imageId: Int? = null
    ) : PaintRoutes

    @Serializable
    data class Animator(
        val animationId: Int? = null
    ) : PaintRoutes
}