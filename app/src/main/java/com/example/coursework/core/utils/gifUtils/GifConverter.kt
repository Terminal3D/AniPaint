package com.example.coursework.core.utils.gifUtils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun createGif(
    context: Context,
    bitmaps: List<Bitmap>,
    frameDurationsInSeconds: List<Float>,
    fileName: String = "output.gif"
): Boolean {
    if (bitmaps.size != frameDurationsInSeconds.size) {
        throw IllegalArgumentException("Количество кадров и длительностей должны совпадать.")
    }

    val gifsDir = getGifDirectory(context)
    val outputFile = File(gifsDir, fileName)


    var fos: FileOutputStream? = null
    return try {
        fos = FileOutputStream(outputFile)
        val encoder = GifEncoder()
        encoder.start(fos)
        encoder.setRepeat(0)
        for (i in bitmaps.indices) {
            val bitmap = bitmaps[i]
            if (bitmap.isRecycled) {
                throw IllegalStateException("Bitmap на индексе $i уже переработан.")
            }
            val frameDurationInMillis = (frameDurationsInSeconds[i] * 1000).toInt()
            encoder.setDelay(frameDurationInMillis)
            encoder.addFrame(bitmap)
        }
        encoder.finish()
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    } finally {
        fos?.close()
    }
}