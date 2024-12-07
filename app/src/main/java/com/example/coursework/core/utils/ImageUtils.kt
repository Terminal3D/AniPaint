package com.example.coursework.core.utils

import android.graphics.Bitmap

object ImageUtils {

    fun ByteArray.getBitmap(actualSize: Int, targetSize: Int = 128): Bitmap {
        val pixels = this.toPixels()

        val bitmap = Bitmap.createBitmap(actualSize, actualSize, Bitmap.Config.ARGB_8888)

        for (x in 0 until actualSize) {
            for (y in 0 until actualSize) {
                val color = pixels[x][y]
                bitmap.setPixel(x, y, color)
            }
        }

        return if (targetSize != actualSize) {
            val matrix = android.graphics.Matrix().apply {
                postScale(targetSize.toFloat() / actualSize, targetSize.toFloat() / actualSize)
            }

            val scaledBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(scaledBitmap)

            canvas.drawBitmap(bitmap, matrix, null)
            scaledBitmap
        } else {
            bitmap
        }
    }

    fun ByteArray.toPixels(): List<IntArray> {
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