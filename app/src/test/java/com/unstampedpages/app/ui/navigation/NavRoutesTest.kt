package com.unstampedpages.app.ui.navigation

import com.unstampedpages.app.R
import org.junit.Assert.*
import org.junit.Test

class NavRoutesTest {

    @Test
    fun `Home route has correct path`() {
        assertEquals("home", NavRoute.Home.route)
    }

    @Test
    fun `Home has correct titleResId`() {
        assertEquals(R.string.nav_home, NavRoute.Home.titleResId)
    }

    @Test
    fun `CountryInfo route has correct path`() {
        assertEquals("country_info", NavRoute.CountryInfo.route)
    }

    @Test
    fun `CountryInfo has correct titleResId`() {
        assertEquals(R.string.nav_countries, NavRoute.CountryInfo.titleResId)
    }

    @Test
    fun `Checklist route has correct path`() {
        assertEquals("checklist", NavRoute.Checklist.route)
    }

    @Test
    fun `Checklist has correct titleResId`() {
        assertEquals(R.string.nav_checklist, NavRoute.Checklist.titleResId)
    }

    @Test
    fun `TripLog route has correct path`() {
        assertEquals("trip_log", NavRoute.TripLog.route)
    }

    @Test
    fun `TripLog has correct titleResId`() {
        assertEquals(R.string.nav_trip_log, NavRoute.TripLog.titleResId)
    }

    @Test
    fun `MyStamps route has correct path`() {
        assertEquals("my_stamps", NavRoute.MyStamps.route)
    }

    @Test
    fun `MyStamps has correct titleResId`() {
        assertEquals(R.string.nav_my_stamps, NavRoute.MyStamps.titleResId)
    }

    @Test
    fun `items contains all navigation routes`() {
        val items = NavRoute.items

        assertEquals(5, items.size)
        assertTrue(items.contains(NavRoute.Home))
        assertTrue(items.contains(NavRoute.CountryInfo))
        assertTrue(items.contains(NavRoute.Checklist))
        assertTrue(items.contains(NavRoute.TripLog))
        assertTrue(items.contains(NavRoute.MyStamps))
    }

    @Test
    fun `items are in correct order`() {
        val items = NavRoute.items

        assertEquals(NavRoute.Home, items[0])
        assertEquals(NavRoute.CountryInfo, items[1])
        assertEquals(NavRoute.Checklist, items[2])
        assertEquals(NavRoute.TripLog, items[3])
        assertEquals(NavRoute.MyStamps, items[4])
    }

    @Test
    fun `all routes are unique`() {
        val routes = NavRoute.items.map { it.route }

        assertEquals(routes.size, routes.distinct().size)
    }

    @Test
    fun `all titleResIds are unique`() {
        val titleResIds = NavRoute.items.map { it.titleResId }

        assertEquals(titleResIds.size, titleResIds.distinct().size)
    }

    @Test
    fun `routes follow snake_case convention`() {
        val snakeCaseRegex = Regex("^[a-z]+(_[a-z]+)*$")

        NavRoute.items.forEach { navRoute ->
            assertTrue(
                "Route '${navRoute.route}' should be snake_case",
                snakeCaseRegex.matches(navRoute.route)
            )
        }
    }
}
