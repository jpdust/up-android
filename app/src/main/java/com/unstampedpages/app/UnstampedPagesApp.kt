package com.unstampedpages.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unstampedpages.app.ui.navigation.BottomNavBar
import com.unstampedpages.app.ui.navigation.NavRoute
import com.unstampedpages.app.ui.screens.checklist.ChecklistScreen
import com.unstampedpages.app.ui.screens.countryinfo.CountryInfoScreen
import com.unstampedpages.app.ui.screens.home.HomeScreen
import com.unstampedpages.app.ui.screens.mystamps.MyStampsScreen
import com.unstampedpages.app.ui.screens.triplog.TripLogScreen

@Composable
fun UnstampedPagesApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoute.Home.route) {
                HomeScreen(
                    onNavigateToCountries = {
                        navController.navigate(NavRoute.CountryInfo.route) {
                            popUpTo(NavRoute.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToChecklist = {
                        navController.navigate(NavRoute.Checklist.route) {
                            popUpTo(NavRoute.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToTripLog = {
                        navController.navigate(NavRoute.TripLog.route) {
                            popUpTo(NavRoute.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToMyStamps = {
                        navController.navigate(NavRoute.MyStamps.route) {
                            popUpTo(NavRoute.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(NavRoute.CountryInfo.route) {
                CountryInfoScreen()
            }
            composable(NavRoute.Checklist.route) {
                ChecklistScreen()
            }
            composable(NavRoute.TripLog.route) {
                TripLogScreen()
            }
            composable(NavRoute.MyStamps.route) {
                MyStampsScreen()
            }
        }
    }
}
