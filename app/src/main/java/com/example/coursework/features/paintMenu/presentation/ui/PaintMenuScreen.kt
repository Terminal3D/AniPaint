package com.example.coursework.features.paintMenu.presentation.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coursework.features.paintMenu.presentation.presentation.PaintMenuAction
import com.example.coursework.features.paintMenu.presentation.presentation.PaintMenuState

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
                    // Опция "Продолжить последний рисунок"
                    state.lastImage?.let { lastImage ->
                        Button(
                            onClick = { onAction(PaintMenuAction.OpenLastImage) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text("Продолжить последний рисунок (${lastImage.imageSize.size}x${lastImage.imageSize.size})")
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
