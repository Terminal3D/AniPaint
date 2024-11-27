package com.example.coursework.features.navigation.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.coursework.features.navigation.graph.HomeNavGraph
import com.example.coursework.features.navigation.route.MainRoutes

@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = {
            PaintBottomAppBar(
                navController = navController,
                tabs = listOf(
                    MainRoutes.Home,
                    MainRoutes.Paint,
                    MainRoutes.Gallery
                )
            )
        }
    ) { innerPaddingModifier ->
        HomeNavGraph(
            navController = navController,
            modifier = Modifier.padding(bottom = innerPaddingModifier.calculateBottomPadding())
        )
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}