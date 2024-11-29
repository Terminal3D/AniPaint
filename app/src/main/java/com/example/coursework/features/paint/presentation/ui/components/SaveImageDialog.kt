package com.example.coursework.features.paint.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SaveImageDialog(
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
    imageName: String?
) {

    val (name, setName) = remember { mutableStateOf(TextFieldValue(imageName ?: "")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Введите название для изображения")
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(
                    value = name,
                    onValueChange = setName,
                    label = { Text(text = "Имя изображения") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newName = name.text.trim()
                    if (newName.isNotEmpty()) {
                        onSave(newName)
                    }
                    onDismiss()
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SaveImageDialogPreview() {
    SaveImageDialog(
        imageName = null,
        onSave = {},
        onDismiss = {}
    )
}
