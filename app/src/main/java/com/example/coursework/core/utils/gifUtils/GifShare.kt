package com.example.coursework.core.utils.gifUtils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun shareGif(context: Context, gifFile: File) {
    if (!gifFile.exists()) {
        return
    }

    val uri: Uri = FileProvider.getUriForFile(
        context,
        "com.example.coursework.fileprovider",
        gifFile
    )

    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/gif"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(shareIntent, "Поделиться GIF"))
}

fun getGifDirectory(context: Context): File {
    val cacheDir = File(context.cacheDir, "gifs")
    if (!cacheDir.exists()) {
        cacheDir.mkdirs()
    }
    return cacheDir
}