package com.example.coursework.core.ui.topbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.window.PopupProperties
import com.example.coursework.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorTopBar(
    name: String,
    showSaveMenu: Boolean,
    onBackPressed: () -> Unit,
    onSavePressed: () -> Unit,
    onSaveAsPressed: () -> Unit,
    onUpdatePressed: () -> Unit,
    onHideSaveMenu: () -> Unit,
    additionalIcons: @Composable (()-> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(name)
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        },
        actions = {
            if (additionalIcons != null) additionalIcons()
            IconButton(onClick = onSavePressed) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_save_24),
                    contentDescription = "Save",
                )
            }
            DropdownMenu(
                onDismissRequest = {
                    onHideSaveMenu()
                },
                properties = PopupProperties(
                    dismissOnClickOutside = true,
                    dismissOnBackPress = true,
                    focusable = false
                ),
                expanded = showSaveMenu,
                modifier = Modifier.wrapContentSize()
            ) {
                DropdownMenuItem(
                    text = {
                        Text("Сохранить")
                    },
                    onClick = {
                        onUpdatePressed()
                        onHideSaveMenu()
                    },
                    modifier = Modifier.wrapContentSize(),
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_update_24),
                            contentDescription = null
                        )
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text("Сохранить как")
                    },
                    onClick = {
                        onSaveAsPressed()
                        onHideSaveMenu()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_save_as_24),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.wrapContentSize()
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}