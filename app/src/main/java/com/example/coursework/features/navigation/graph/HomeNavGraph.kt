package com.example.coursework.features.navigation.graph

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.coursework.features.gallery.presentation.ui.GalleryScreen
import com.example.coursework.features.gallery.presentation.viewmodel.GalleryNavigationEvent
import com.example.coursework.features.gallery.presentation.viewmodel.GalleryViewModel
import com.example.coursework.features.navigation.route.MainRoutes
import com.example.coursework.features.navigation.route.PaintRoutes
import com.example.coursework.features.paint.presentation.ui.PaintScreen
import com.example.coursework.features.paint.presentation.viewmodel.PaintNavigationEvent
import com.example.coursework.features.paint.presentation.viewmodel.PaintViewModel
import com.example.coursework.features.paintMenu.presentation.ui.PaintMenuScreen
import com.example.coursework.features.paintMenu.presentation.viewmodel.PaintMenuNavigationEvent
import com.example.coursework.features.paintMenu.presentation.viewmodel.PaintMenuViewModel

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainRoutes.Home,
    ) {
        composable<MainRoutes.Home> {
            Text(
                text = "HELLO",
                modifier = modifier
            )
        }

        composable<MainRoutes.PaintMenu> {

            val viewModel = hiltViewModel<PaintMenuViewModel>()
            val lifecycleOwner = LocalLifecycleOwner.current
            val navigationEvents = viewModel.navigationEvents
            val uiEvents = viewModel.uiEvents
            LaunchedEffect(lifecycleOwner.lifecycle) {
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    navigationEvents.collect { navigationEvent ->
                        when (navigationEvent) {
                            is PaintMenuNavigationEvent.NavigateToNewImage -> navController.navigate(
                                PaintRoutes.Paint(navigationEvent.imageSize)
                            )

                            is PaintMenuNavigationEvent.NavigateToLastImage -> navController.navigate(
                                PaintRoutes.Paint(
                                    imageSize = null
                                )
                            )
                        }
                    }
                }
            }

            PaintMenuScreen(
                state = viewModel.state.collectAsState().value,
                onAction = viewModel::onAction,
                modifier = modifier
            )
        }
        composable<MainRoutes.Gallery> {
            val viewModel = hiltViewModel<GalleryViewModel>()
            val lifecycleOwner = LocalLifecycleOwner.current
            val navigationEvents = viewModel.navigationEvents
            val uiEvents = viewModel.uiEvents
            LaunchedEffect(lifecycleOwner.lifecycle) {
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    navigationEvents.collect { navigationEvent ->
                        when (navigationEvent) {
                            is GalleryNavigationEvent.NavigateToImage -> navController.navigate(
                                PaintRoutes.Paint(
                                    imageId = navigationEvent.id
                                )
                            )
                            GalleryNavigationEvent.NavigateToPaintMenu -> navController.navigate(
                                MainRoutes.PaintMenu
                            ) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            }

            GalleryScreen(
                state = viewModel.state.collectAsState().value,
                onAction = viewModel::onAction,
                modifier = modifier
            )
        }

        composable<PaintRoutes.Paint> {

            val args = it.toRoute<PaintRoutes.Paint>()

            val viewModel = hiltViewModel<PaintViewModel>()

            remember {
                viewModel.getPaintScreen(args.imageSize, args.imageId)
            }

            val lifecycleOwner = LocalLifecycleOwner.current
            val navigationEvents = viewModel.navigationEvents
            val uiEvents = viewModel.uiEvents
            LaunchedEffect(lifecycleOwner.lifecycle) {
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    navigationEvents.collect { navigationEvent ->
                        when (navigationEvent) {
                            PaintNavigationEvent.NavigateBack -> navController.popBackStack()
                        }
                    }
                }
            }

            PaintScreen(
                state = viewModel.state.collectAsState().value,
                uiEvents = uiEvents,
                onAction = viewModel::onAction,
                modifier = modifier
            )
        }
    }
}