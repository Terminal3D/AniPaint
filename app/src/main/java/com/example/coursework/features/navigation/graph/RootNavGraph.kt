package com.example.coursework.features.navigation.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.coursework.features.navigation.presentation.ui.HomeScreen
import com.example.coursework.features.navigation.route.MainRoutes

@Composable
fun RootNavGraph(
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = MainRoutes.Home
    ) {
        composable<MainRoutes.Home> {
            HomeScreen()
        }
    }
}