package com.unstampedpages.app.ui.navigation

import org.junit.Assert.*
import org.junit.Test

class NavRoutesTest {

    @Test
    fun `Home route has correct path`() {
        assertEquals("home", NavRoute.Home.route)
    }

    @Test
    fun `Home has correct title`() {
        assertEquals("Home", NavRoute.Home.title)
    }

    @Test
    fun `CountryInfo route has correct path`() {
        assertEquals("country_info", NavRoute.CountryInfo.route)
    }

    @Test
    fun `CountryInfo has correct title`() {
        assertEquals("Countries", NavRoute.CountryInfo.title)
    }

    @Test
    fun `Checklist route has correct path`() {
        assertEquals("checklist", NavRoute.Checklist.route)
    }

    @Test
    fun `Checklist has correct title`() {
        assertEquals("Checklist", NavRoute.Checklist.title)
    }

    @Test
    fun `TripLog route has correct path`() {
        assertEquals("trip_log", NavRoute.TripLog.route)
    }

    @Test
    fun `TripLog has correct title`() {
        assertEquals("Trip Log", NavRoute.TripLog.title)
    }

    @Test
    fun `MyStamps route has correct path`() {
        assertEquals("my_stamps", NavRoute.MyStamps.route)
    }

    @Test
    fun `MyStamps has correct title`() {
        assertEquals("My Stamps", NavRoute.MyStamps.title)
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
    fun `all titles are unique`() {
        val titles = NavRoute.items.map { it.title }

        assertEquals(titles.size, titles.distinct().size)
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
