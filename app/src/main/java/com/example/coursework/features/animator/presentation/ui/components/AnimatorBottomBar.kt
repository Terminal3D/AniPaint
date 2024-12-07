package com.example.coursework.features.animator.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun AnimatorBottomBar(
    isEnabled: Boolean,
    stateSliderValue: Int,
    onSlideFinished: (Int) -> Unit
) {
    BottomAppBar {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stateSliderValue.toString())
            Slider(
                value = stateSliderValue.toFloat(),
                onValueChange = { onSlideFinished(it.roundToInt()) },
                steps = 47,
                valueRange = 1f..48f,
                enabled = isEnabled
            )
        }
    }
}