package com.example.coursework.features.navigation.presentation.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.coursework.core.ui.theme.PaintTheme
import com.example.coursework.features.navigation.graph.RootNavGraph

@Composable
fun PaintApp() {
    PaintTheme {
        RootNavGraph(rememberNavController())
    }
}
