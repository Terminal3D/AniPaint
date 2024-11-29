package com.example.coursework.features.paintMenu.presentation.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coursework.features.paintMenu.presentation.viewmodel.PaintMenuAction
import com.example.coursework.features.paintMenu.presentation.viewmodel.PaintMenuState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaintMenuScreen(
    state: PaintMenuState,
    onAction: (PaintMenuAction) -> Unit,
    modifier: Modifier
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Выбор размера изображения") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    state.lastImage?.let { lastImage ->
                        Button(
                            onClick = { onAction(PaintMenuAction.OpenLastImage) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text("Продолжить последний рисунок ${lastImage.name ?: ""} (${lastImage.imageSize.size}x${lastImage.imageSize.size})")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Начать новый рисунок:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    state.imageSizes.forEach { size ->
                        SizeOptionButton(
                            width = size.size,
                            height = size.size,
                            onClick = { onAction(PaintMenuAction.CreateNewImage(size))}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SizeOptionButton(
    width: Int,
    height: Int,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = "$width x $height")
    }
}

@Preview(showBackground = true)
@Composable
fun PaintMenuScreenPreview() {

    PaintMenuScreen(
        state = PaintMenuState(),
        onAction = {},
        modifier = Modifier
    )
}
