package com.unstampedpages.app.ui.navigation

import androidx.annotation.StringRes
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
import com.unstampedpages.app.R
import com.unstampedpages.app.analytics.AppAnalytics

sealed class NavRoute(
    val route: String,
    @param:StringRes val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val analyticsName: String
) {
    object Home : NavRoute(
        route = "home",
        titleResId = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        analyticsName = AppAnalytics.SCREEN_HOME
    )

    object CountryInfo : NavRoute(
        route = "country_info",
        titleResId = R.string.nav_countries,
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore,
        analyticsName = AppAnalytics.SCREEN_COUNTRIES
    )

    object Checklist : NavRoute(
        route = "checklist",
        titleResId = R.string.nav_checklist,
        selectedIcon = Icons.Filled.Checklist,
        unselectedIcon = Icons.Outlined.Checklist,
        analyticsName = AppAnalytics.SCREEN_CHECKLIST
    )

    object TripLog : NavRoute(
        route = "trip_log",
        titleResId = R.string.nav_trip_log,
        selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
        unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook,
        analyticsName = AppAnalytics.SCREEN_TRIP_LOG
    )

    object MyStamps : NavRoute(
        route = "my_stamps",
        titleResId = R.string.nav_my_stamps,
        selectedIcon = Icons.Filled.PhotoLibrary,
        unselectedIcon = Icons.Outlined.PhotoLibrary,
        analyticsName = AppAnalytics.SCREEN_STAMPS
    )

    companion object {
        val items: List<NavRoute> by lazy {
            listOf(Home, CountryInfo, Checklist, TripLog, MyStamps)
        }
    }
}
