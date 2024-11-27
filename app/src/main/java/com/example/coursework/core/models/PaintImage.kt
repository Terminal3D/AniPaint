package com.example.coursework.core.models

import kotlinx.serialization.Serializable

@Serializable
data class PaintImage(
    val pixels: List<IntArray>,
    val imageSize: ImageSize
)
