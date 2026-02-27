package com.unstampedpages.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unstampedpages.app.ui.navigation.BottomNavBar
import com.unstampedpages.app.ui.navigation.NavRoute
import com.unstampedpages.app.ui.screens.checklist.ChecklistScreen
import com.unstampedpages.app.ui.screens.countryinfo.CountryInfoScreen
import com.unstampedpages.app.ui.screens.home.HomeScreen
import com.unstampedpages.app.ui.screens.mystamps.MyStampsScreen
import com.unstampedpages.app.ui.screens.triplog.TripLogScreen
import com.unstampedpages.app.ui.theme.Primary
import com.unstampedpages.app.ui.theme.Secondary

private data class HeaderContent(
    val title: String,
    val subtitle: String
)

private fun getHeaderContent(route: String?): HeaderContent? {
    return when (route) {
        NavRoute.CountryInfo.route -> HeaderContent("EXPLORE THE WORLD", "Tap a country to discover more")
        NavRoute.Checklist.route -> HeaderContent("TRAVEL CHECKLIST", "Never forget the essentials")
        NavRoute.TripLog.route -> HeaderContent("TRIP LOG", "Document your adventures")
        NavRoute.MyStamps.route -> HeaderContent("MY STAMPS", "Collect passport stamps from around the world")
        else -> null
    }
}

@Composable
fun UnstampedPagesApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val headerContent = getHeaderContent(currentRoute)

    val animationDuration = 300

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Shared header - animates in/out with content
            AnimatedVisibility(
                visible = headerContent != null,
                enter = expandVertically(
                    animationSpec = tween(animationDuration),
                    expandFrom = Alignment.Top
                ) + fadeIn(animationSpec = tween(animationDuration)),
                exit = shrinkVertically(
                    animationSpec = tween(animationDuration),
                    shrinkTowards = Alignment.Top
                ) + fadeOut(animationSpec = tween(animationDuration))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Primary)
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AnimatedContent(
                            targetState = headerContent,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(animationDuration)) togetherWith
                                    fadeOut(animationSpec = tween(animationDuration))
                            },
                            label = "header_text_animation"
                        ) { content ->
                            if (content != null) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = content.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = content.subtitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Secondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            NavHost(
                navController = navController,
                startDestination = NavRoute.Home.route,
                modifier = Modifier.fillMaxSize(),
                enterTransition = { fadeIn(animationSpec = tween(animationDuration)) },
                exitTransition = { fadeOut(animationSpec = tween(animationDuration)) },
                popEnterTransition = { fadeIn(animationSpec = tween(animationDuration)) },
                popExitTransition = { fadeOut(animationSpec = tween(animationDuration)) }
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
}
