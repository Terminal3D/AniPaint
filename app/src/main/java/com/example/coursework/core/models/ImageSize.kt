package com.example.coursework.core.models

import kotlinx.serialization.Serializable

@Serializable
enum class ImageSize(val size: Int) {
    XXS(16),
    XS(32),
    S(64),
    M(128),
    L(256)
}