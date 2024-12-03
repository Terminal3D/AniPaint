package com.example.coursework.core.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ShareUtils {

    /**
     * Сохраняет Bitmap во временный файл в кэше приложения.
     *
     * @param context Контекст приложения.
     * @param bitmap Изображение, которое необходимо сохранить.
     * @param fileName Имя файла для сохранения.
     * @return Файл, содержащий сохраненное изображение.
     */
    fun saveBitmapToCache(context: Context, bitmap: Bitmap, fileName: String): File {
        val cachePath = File(context.cacheDir, "images")
        if (!cachePath.exists()) {
            cachePath.mkdirs() // Создает директорию, если не существует
        }

        val file = File(cachePath, fileName)
        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }
        return file
    }

    /**
     * Получает URI для файла через FileProvider.
     *
     * @param context Контекст приложения.
     * @param file Файл, для которого необходимо получить URI.
     * @return URI для совместного использования.
     */
    fun getImageUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    /**
     * Создает Intent для совместного использования изображения.
     *
     * @param uri URI изображения для совместного использования.
     * @return Intent для обмена изображением.
     */
    fun createShareIntent(uri: Uri): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}