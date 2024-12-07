package com.example.coursework.core.models

import android.graphics.Bitmap

sealed class SliderItem(
    open val onClick: () -> Unit
) {
    data class Image(
        val bitmap: Bitmap,
        override val onClick: () -> Unit
    ) : SliderItem(onClick)

    data class NewImage(
        override val onClick: () -> Unit
    ) : SliderItem(onClick)
}
