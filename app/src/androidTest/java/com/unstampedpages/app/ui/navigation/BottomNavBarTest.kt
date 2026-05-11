package com.unstampedpages.app.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.ui.navigation.BottomNavBarRobot.Companion.bottomNavBarRobot
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [BottomNavBar].
 *
 * Tests are organized into four groups:
 *   1. Rendering — every tab label appears on screen
 *   2. Selection state — the correct tab is marked selected for each route
 *   3. Navigation — clicking a tab changes the current destination
 *   4. Guard — clicking the already-selected tab does NOT trigger navigation
 *
 * Each test launches [BottomNavBar] in isolation via [createAndroidComposeRule] with a
 * [TestNavHostController] backed by a minimal [NavHost].  This gives full control over
 * the initial route and direct access to the back stack for assertions.
 */
@RunWith(AndroidJUnit4::class)
class BottomNavBarTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

    /**
     * English labels for all five tabs (matches values/strings.xml).
     * Used in "only one tab selected" assertions that need the full set.
     */
    private val allTabLabels = listOf("Home", "Countries", "Checklist", "Trip Log", "My Stamps")

    /**
     * Renders [BottomNavBar] with a [TestNavHostController] and a [NavHost] whose destinations
     * mirror [NavRoute.items].  If [startRoute] differs from the home destination, the controller
     * navigates there after the initial composition settles.
     */
    private fun launchBottomNavBar(startRoute: String = NavRoute.Home.route) {
        composeTestRule.setContent {
            val context = LocalContext.current
            navController = remember {
                TestNavHostController(context).apply {
                    navigatorProvider.addNavigator(ComposeNavigator())
                }
            }
            NavHost(
                navController = navController,
                startDestination = NavRoute.Home.route
            ) {
                NavRoute.items.forEach { route ->
                    composable(route.route) { /* test stub — content irrelevant */ }
                }
            }
            BottomNavBar(navController = navController)
        }
        composeTestRule.waitForIdle()

        if (startRoute != NavRoute.Home.route) {
            composeTestRule.runOnUiThread { navController.navigate(startRoute) }
            composeTestRule.waitForIdle()
        }
    }

    // ==================== 1. Rendering ====================

    @Test
    fun allFiveTabs_areDisplayed() {
        launchBottomNavBar()
        composeTestRule.bottomNavBarRobot {
            verifyAllTabsDisplayed(allTabLabels)
        }
    }

    @Test
    fun homeTab_isDisplayed() {
        launchBottomNavBar()
        composeTestRule.bottomNavBarRobot { verifyTabDisplayed("Home") }
    }

    @Test
    fun countriesTab_isDisplayed() {
        launchBottomNavBar()
        composeTestRule.bottomNavBarRobot { verifyTabDisplayed("Countries") }
    }

    @Test
    fun checklistTab_isDisplayed() {
        launchBottomNavBar()
        composeTestRule.bottomNavBarRobot { verifyTabDisplayed("Checklist") }
    }

    @Test
    fun tripLogTab_isDisplayed() {
        launchBottomNavBar()
        composeTestRule.bottomNavBarRobot { verifyTabDisplayed("Trip Log") }
    }

    @Test
    fun myStampsTab_isDisplayed() {
        launchBottomNavBar()
        composeTestRule.bottomNavBarRobot { verifyTabDisplayed("My Stamps") }
    }

    /**
     * Tab labels are resolved from string resources at runtime.
     * This test verifies that every [NavRoute] item's [titleResId] resolves to
     * a string that actually appears in the nav bar, so a resource rename cannot
     * silently break the UI without failing here.
     */
    @Test
    fun tabLabels_matchNavRouteStringResources() {
        launchBottomNavBar()
        val activity = composeTestRule.activity
        composeTestRule.bottomNavBarRobot {
            NavRoute.items.forEach { route ->
                verifyTabDisplayed(activity.getString(route.titleResId))
            }
        }
    }

    // ==================== 2. Selection state ====================

    @Test
    fun homeTab_isSelected_whenOnHomeRoute() {
        launchBottomNavBar(startRoute = NavRoute.Home.route)
        composeTestRule.bottomNavBarRobot {
            verifyOnlyTabIsSelected("Home", allTabLabels)
        }
    }

    @Test
    fun countriesTab_isSelected_whenOnCountriesRoute() {
        launchBottomNavBar(startRoute = NavRoute.CountryInfo.route)
        composeTestRule.bottomNavBarRobot {
            verifyOnlyTabIsSelected("Countries", allTabLabels)
        }
    }

    @Test
    fun checklistTab_isSelected_whenOnChecklistRoute() {
        launchBottomNavBar(startRoute = NavRoute.Checklist.route)
        composeTestRule.bottomNavBarRobot {
            verifyOnlyTabIsSelected("Checklist", allTabLabels)
        }
    }

    @Test
    fun tripLogTab_isSelected_whenOnTripLogRoute() {
        launchBottomNavBar(startRoute = NavRoute.TripLog.route)
        composeTestRule.bottomNavBarRobot {
            verifyOnlyTabIsSelected("Trip Log", allTabLabels)
        }
    }

    @Test
    fun myStampsTab_isSelected_whenOnMyStampsRoute() {
        launchBottomNavBar(startRoute = NavRoute.MyStamps.route)
        composeTestRule.bottomNavBarRobot {
            verifyOnlyTabIsSelected("My Stamps", allTabLabels)
        }
    }

    /** Negative check: non-home tabs are not selected on initial launch. */
    @Test
    fun nonHomeTabs_areNotSelected_onInitialLaunch() {
        launchBottomNavBar()
        composeTestRule.bottomNavBarRobot {
            verifyTabIsNotSelected("Countries")
            verifyTabIsNotSelected("Checklist")
            verifyTabIsNotSelected("Trip Log")
            verifyTabIsNotSelected("My Stamps")
        }
    }

    // ==================== 3. Navigation ====================

    @Test
    fun clickingCountriesTab_navigatesTo_countriesRoute() {
        launchBottomNavBar()
        composeTestRule.bottomNavBarRobot { clickTab("Countries") }
        assertEquals(NavRoute.CountryInfo.route, navController.currentDestination?.route)
    }

    @Test
    fun clickingChecklistTab_navigatesTo_checklistRoute() {
        launchBottomNavBar()
        composeTestRule.bottomNavBarRobot { clickTab("Checklist") }
        assertEquals(NavRoute.Checklist.route, navController.currentDestination?.route)
    }

    @Test
    fun clickingTripLogTab_navigatesTo_tripLogRoute() {
        launchBottomNavBar()
        composeTestRule.bottomNavBarRobot { clickTab("Trip Log") }
        assertEquals(NavRoute.TripLog.route, navController.currentDestination?.route)
    }

    @Test
    fun clickingMyStampsTab_navigatesTo_myStampsRoute() {
        launchBottomNavBar()
        composeTestRule.bottomNavBarRobot { clickTab("My Stamps") }
        assertEquals(NavRoute.MyStamps.route, navController.currentDestination?.route)
    }

    @Test
    fun clickingHomeTab_fromNonHomeRoute_navigatesTo_homeRoute() {
        // Navigate to Checklist through the BottomNavBar so the back stack carries the same
        // saveState/restoreState metadata that a real user tap would produce.  Using
        // navController.navigate() directly bypasses those navOptions and leaves the back
        // stack in a state that is incompatible with the home-tab navigation logic.
        launchBottomNavBar()
        composeTestRule.bottomNavBarRobot { clickTab("Checklist") }
        composeTestRule.bottomNavBarRobot { clickTab("Home") }
        assertEquals(NavRoute.Home.route, navController.currentDestination?.route)
    }

    /** Clicking a tab must update the selection highlight in the bar itself. */
    @Test
    fun clickingTab_updatesSelectedState() {
        launchBottomNavBar()
        composeTestRule.bottomNavBarRobot {
            verifyTabIsSelected("Home")
            clickTab("Checklist")
            verifyTabIsSelected("Checklist")
            verifyTabIsNotSelected("Home")
        }
    }

    /** Cycling through all tabs in order must select each one correctly. */
    @Test
    fun onlyOneTab_isSelected_atATime_whenCyclingThroughAll() {
        launchBottomNavBar()
        val sequence = listOf("Countries", "Checklist", "Trip Log", "My Stamps", "Home")
        composeTestRule.bottomNavBarRobot {
            sequence.forEach { label ->
                clickTab(label)
                verifyOnlyTabIsSelected(label, allTabLabels)
            }
        }
    }

    // ==================== 4. Guard: no re-navigation when already selected ====================

    /**
     * [BottomNavBar] contains an explicit guard:
     *   `if (currentRoute != item.route) { navController.navigate(...) }`
     *
     * When the user taps the tab that is already selected, navigate() must NOT be called,
     * so the back stack size must remain unchanged.
     */
    @Test
    fun clickingAlreadySelectedHomeTab_doesNotGrowBackStack() {
        launchBottomNavBar()
        val stackSizeBefore = navController.currentBackStack.value.size

        composeTestRule.bottomNavBarRobot { clickTab("Home") }

        assertEquals(stackSizeBefore, navController.currentBackStack.value.size)
        assertEquals(NavRoute.Home.route, navController.currentDestination?.route)
    }

    @Test
    fun clickingAlreadySelectedCountriesTab_doesNotGrowBackStack() {
        launchBottomNavBar(startRoute = NavRoute.CountryInfo.route)
        val stackSizeBefore = navController.currentBackStack.value.size

        composeTestRule.bottomNavBarRobot { clickTab("Countries") }

        assertEquals(stackSizeBefore, navController.currentBackStack.value.size)
        assertEquals(NavRoute.CountryInfo.route, navController.currentDestination?.route)
    }

    @Test
    fun clickingAlreadySelectedChecklistTab_doesNotGrowBackStack() {
        launchBottomNavBar(startRoute = NavRoute.Checklist.route)
        val stackSizeBefore = navController.currentBackStack.value.size

        composeTestRule.bottomNavBarRobot { clickTab("Checklist") }

        assertEquals(stackSizeBefore, navController.currentBackStack.value.size)
        assertEquals(NavRoute.Checklist.route, navController.currentDestination?.route)
    }

    @Test
    fun clickingAlreadySelectedTripLogTab_doesNotGrowBackStack() {
        launchBottomNavBar(startRoute = NavRoute.TripLog.route)
        val stackSizeBefore = navController.currentBackStack.value.size

        composeTestRule.bottomNavBarRobot { clickTab("Trip Log") }

        assertEquals(stackSizeBefore, navController.currentBackStack.value.size)
        assertEquals(NavRoute.TripLog.route, navController.currentDestination?.route)
    }

    @Test
    fun clickingAlreadySelectedMyStampsTab_doesNotGrowBackStack() {
        launchBottomNavBar(startRoute = NavRoute.MyStamps.route)
        val stackSizeBefore = navController.currentBackStack.value.size

        composeTestRule.bottomNavBarRobot { clickTab("My Stamps") }

        assertEquals(stackSizeBefore, navController.currentBackStack.value.size)
        assertEquals(NavRoute.MyStamps.route, navController.currentDestination?.route)
    }
}
