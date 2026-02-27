package com.unstampedpages.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavRoute(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : NavRoute(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object CountryInfo : NavRoute(
        route = "country_info",
        title = "Countries",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore
    )

    object Checklist : NavRoute(
        route = "checklist",
        title = "Checklist",
        selectedIcon = Icons.Filled.Checklist,
        unselectedIcon = Icons.Outlined.Checklist
    )

    object TripLog : NavRoute(
        route = "trip_log",
        title = "Trip Log",
        selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
        unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook
    )

    object MyStamps : NavRoute(
        route = "my_stamps",
        title = "My Stamps",
        selectedIcon = Icons.Filled.PhotoLibrary,
        unselectedIcon = Icons.Outlined.PhotoLibrary
    )

    companion object {
        val items: List<NavRoute> by lazy {
            listOf(Home, CountryInfo, Checklist, TripLog, MyStamps)
        }
    }
}
