package com.example.coursework.features.navigation.presentation.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.coursework.features.navigation.route.MainRoutes

@Composable
fun PaintBottomAppBar(
    navController: NavController,
    tabs: List<MainRoutes>
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val isBottomBarDestination = tabs.any {
        currentDestination?.hasRoute(it::class) == true
    }

    if (isBottomBarDestination) {
        BottomAppBar(
            modifier = Modifier.height(80.dp)
        ) {
            NavigationBar {
                tabs.map { item ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute(item::class) } == true,
                        onClick = {
                            if (currentDestination != item) {
                                navController.navigate(item) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(item.icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(item.title),
                            )
                        }
                    )
                }
            }
        }
    }
}