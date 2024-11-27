package com.example.coursework.common.database.mappers

import com.example.coursework.common.database.entities.LastImageEntity
import com.example.coursework.common.database.entities.SavedImageEntity
import com.example.coursework.core.models.ImageSize
import com.example.coursework.core.models.PaintImage
import java.io.ByteArrayOutputStream

// Преобразуем PaintImage в LastImageEntity
internal fun PaintImage.toLastImageEntity(): LastImageEntity {
    return LastImageEntity(
        image = this.pixels.toByteArray(), // Преобразуем в ByteArray
        size = this.imageSize.size
    )
}

// Преобразуем PaintImage в SavedImageEntity
internal fun PaintImage.toSavedImageEntity(): SavedImageEntity {
    return SavedImageEntity(
        image = this.pixels.toByteArray(), // Преобразуем в ByteArray
        size = this.imageSize.size
    )
}

// Преобразуем LastImageEntity в PaintImage
internal fun LastImageEntity.toPaintImage(): PaintImage {
    return PaintImage(
        pixels = this.image.toPixels(), // Преобразуем обратно в пиксели
        imageSize = this.size.toImageSize()
    )
}

// Преобразуем SavedImageEntity в PaintImage
internal fun SavedImageEntity.toPaintImage(): PaintImage {
    return PaintImage(
        pixels = this.image.toPixels(), // Преобразуем обратно в пиксели
        imageSize = this.size.toImageSize()
    )
}

// Преобразуем массив пикселей в ByteArray (по пикселям)
private fun List<IntArray>.toByteArray(): ByteArray {
    val size = this.size
    val byteArrayOutputStream = ByteArrayOutputStream(size * size * 4) // Каждый пиксель занимает 4 байта (ARGB)

    // Перебираем все пиксели и записываем их в ByteArray
    for (x in 0 until size) {
        for (y in 0 until size) {
            val pixel = this[x][y]

            // Записываем 4 байта: альфа, красный, зеленый, синий
            byteArrayOutputStream.write((pixel shr 24) and 0xFF) // Альфа
            byteArrayOutputStream.write((pixel shr 16) and 0xFF) // Красный
            byteArrayOutputStream.write((pixel shr 8) and 0xFF)  // Зеленый
            byteArrayOutputStream.write(pixel and 0xFF)         // Синий
        }
    }

    return byteArrayOutputStream.toByteArray()
}

// Преобразуем ByteArray обратно в массив пикселей
private fun ByteArray.toPixels(): List<IntArray> {
    val size = Math.sqrt(this.size / 4.0).toInt() // Размер изображения
    val pixels = Array(size) { IntArray(size) }

    var byteIndex = 0
    // Перебираем ByteArray и восстанавливаем пиксели
    for (x in 0 until size) {
        for (y in 0 until size) {
            val alpha = this[byteIndex++].toInt() and 0xFF
            val red = this[byteIndex++].toInt() and 0xFF
            val green = this[byteIndex++].toInt() and 0xFF
            val blue = this[byteIndex++].toInt() and 0xFF

            // Собираем пиксель в формате ARGB
            pixels[x][y] = (alpha shl 24) or (red shl 16) or (green shl 8) or blue
        }
    }

    return pixels.asList() // Возвращаем список массивов пикселей
}

// Преобразуем числовое значение размера изображения в ImageSize
private fun Int.toImageSize(): ImageSize {
    return when (this) {
        16 -> ImageSize.XXS
        32 -> ImageSize.XS
        64 -> ImageSize.S
        128 -> ImageSize.M
        256 -> ImageSize.L
        else -> ImageSize.XS
    }
}
