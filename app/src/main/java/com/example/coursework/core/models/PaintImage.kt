package com.example.coursework.core.models

import kotlinx.serialization.Serializable

@Serializable
data class PaintImage(
    val id: Int?,
    val name: String?,
    val pixels: List<IntArray>,
    val imageSize: ImageSize,
)
