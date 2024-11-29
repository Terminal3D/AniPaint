package com.example.coursework.common.database.entities

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.coursework.core.models.ImageSize
import com.example.coursework.core.models.PaintImage

@Entity(tableName = "saved_images")
data class SavedImageEntity(
    val image: ByteArray,
    val size: Int,
    val name: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) {
    // Метод для получения Bitmap с качественным рескейлингом (бикубическая интерполяция)
    fun getBitmap(targetSize: Int = 128): Bitmap {
        val paintImage = this.toPaintImage()

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        for (x in 0 until size) {
            for (y in 0 until size) {
                val color = paintImage.pixels[x][y]
                bitmap.setPixel(x, y, color)
            }
        }

        return if (targetSize != size) {
            val matrix = android.graphics.Matrix().apply {
                postScale(targetSize.toFloat() / size, targetSize.toFloat() / size)
            }

            val scaledBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(scaledBitmap)

            canvas.drawBitmap(bitmap, matrix, null)
            scaledBitmap
        } else {
            bitmap
        }
    }

    private fun toPaintImage(): PaintImage {
        return PaintImage(
            pixels = this.image.toPixels(),
            imageSize = this.size.toImageSize(),
            name = this.name,
            id = this.id
        )
    }

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

    private fun ByteArray.toPixels(): List<IntArray> {
        val size = Math.sqrt(this.size / 4.0).toInt() // Размер изображения
        val pixels = Array(size) { IntArray(size) }

        var byteIndex = 0
        for (x in 0 until size) {
            for (y in 0 until size) {
                val alpha = this[byteIndex++].toInt() and 0xFF
                val red = this[byteIndex++].toInt() and 0xFF
                val green = this[byteIndex++].toInt() and 0xFF
                val blue = this[byteIndex++].toInt() and 0xFF

                pixels[x][y] = (alpha shl 24) or (red shl 16) or (green shl 8) or blue
            }
        }

        return pixels.asList()
    }
}

