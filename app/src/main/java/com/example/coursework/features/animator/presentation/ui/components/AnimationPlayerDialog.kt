package com.example.coursework.features.animator.presentation.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.coursework.R
import com.example.coursework.core.utils.gifUtils.createGif
import com.example.coursework.core.utils.gifUtils.shareGif
import com.example.coursework.features.animator.data.AnimationFrameData
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun AnimationPlayerDialog(
    frames: List<AnimationFrameData>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var gifFile by remember { mutableStateOf<File?>(null) }
    var isGifCreated by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }
    var elapsedTime by remember { mutableLongStateOf(0L) }

    val fps = 24f


    val frameDurationsMillis = remember(frames) {
        frames.map { frame ->
            (frame.frameDuration.toFloat() / fps * 1000).toLong()
        }
    }

    val cumulativeDurations by remember(frames, frameDurationsMillis) {
        mutableStateOf(
            frameDurationsMillis.fold(mutableListOf<Long>()) { acc, duration ->
                val last = acc.lastOrNull() ?: 0L
                acc.add(last + duration)
                acc
            }
        )
    }

    val totalDuration by remember(frameDurationsMillis) {
        mutableStateOf(frameDurationsMillis.sum())
    }


    LaunchedEffect(Unit) {

        val bitmaps = frames.map { it.image.copy(it.image.config ?: Bitmap.Config.ARGB_8888, false) }


        val frameDurationsInSeconds = frames.map { (it.frameDuration.toFloat() / fps) }

        val outputFile = File(getGifDirectory(context), "output.gif")
        val success = createGif(context, bitmaps, frameDurationsInSeconds, "output.gif")
        if (success) {
            gifFile = outputFile
            isGifCreated = true
        } else {
            Toast.makeText(context, "Ошибка при создании GIF", Toast.LENGTH_SHORT).show()
        }
    }


    LaunchedEffect(isPlaying, elapsedTime, isGifCreated) {
        if (isPlaying && isGifCreated && totalDuration > 0) {
            while (isPlaying) {
                delay(100L)
                elapsedTime += 100L
                if (elapsedTime >= totalDuration) {
                    elapsedTime %= totalDuration
                }
            }
        }
    }


    val currentFrameIndex by remember(elapsedTime, cumulativeDurations) {
        derivedStateOf {
            cumulativeDurations.indexOfFirst { it > elapsedTime }.takeIf { it != -1 }
                ?: frames.size - 1
        }
    }


    val sliderPosition by remember(elapsedTime, totalDuration) {
        derivedStateOf {
            if (totalDuration > 0) elapsedTime / totalDuration.toFloat() else 0f
        }
    }


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isGifCreated && frames.isNotEmpty()) {

                    FrameImage(
                        bitmap = frames[currentFrameIndex].image,
                        modifier = Modifier
                            .size(300.dp)
                            .padding(16.dp)
                    )
                } else {
                    CircularProgressIndicator(modifier = Modifier.size(100.dp))
                }


                Slider(
                    value = sliderPosition,
                    onValueChange = { value ->
                        elapsedTime = (value * totalDuration).toLong().coerceIn(0L, totalDuration)
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { isPlaying = !isPlaying }) {
                        Icon(
                            imageVector = if (isPlaying) ImageVector.vectorResource(R.drawable.baseline_pause_24) else Icons.Filled.PlayArrow,
                            contentDescription = "Play/Pause"
                        )
                    }

                    if (isGifCreated && gifFile != null) {
                        IconButton(onClick = { shareGif(context, gifFile!!) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share GIF"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FrameImage(bitmap: Bitmap, modifier: Modifier = Modifier) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Animation Frame",
        modifier = modifier
    )
}


fun getGifDirectory(context: Context): File {
    val gifDir = File(context.cacheDir, "gifs")
    if (!gifDir.exists()) {
        gifDir.mkdirs()
    }
    return gifDir
}
