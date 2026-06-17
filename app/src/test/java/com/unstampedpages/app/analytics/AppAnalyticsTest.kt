package com.unstampedpages.app.analytics

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [AppAnalytics] public constants.
 *
 * The New Relic [recordCustomEvent] calls require the Android runtime and cannot
 * be executed in JVM unit tests; those are covered by the instrumented test suite.
 * This file verifies every public constant has the exact string value expected by
 * the NR dashboard so that a typo surfaces at test time, not in production data.
 *
 * Schema: two event tables.
 *   UserAction  — screen + action + contextual attributes
 *   MapGesture  — action (zoomed/panned) + gesture-specific attributes
 */
class AppAnalyticsTest {

    // ── Screen constants ─────────────────────────────────────────────────────────

    @Test
    fun `SCREEN_HOME equals home`() {
        assertEquals("home", AppAnalytics.SCREEN_HOME)
    }

    @Test
    fun `SCREEN_COUNTRIES equals countries`() {
        assertEquals("countries", AppAnalytics.SCREEN_COUNTRIES)
    }

    @Test
    fun `SCREEN_CHECKLIST equals checklist`() {
        assertEquals("checklist", AppAnalytics.SCREEN_CHECKLIST)
    }

    @Test
    fun `SCREEN_TRIP_LOG equals tripLog`() {
        assertEquals("tripLog", AppAnalytics.SCREEN_TRIP_LOG)
    }

    @Test
    fun `SCREEN_STAMPS equals stamps`() {
        assertEquals("stamps", AppAnalytics.SCREEN_STAMPS)
    }

    @Test
    fun `all screen constants are distinct`() {
        val screens = listOf(
            AppAnalytics.SCREEN_HOME,
            AppAnalytics.SCREEN_COUNTRIES,
            AppAnalytics.SCREEN_CHECKLIST,
            AppAnalytics.SCREEN_TRIP_LOG,
            AppAnalytics.SCREEN_STAMPS
        )
        assertEquals("All screen constants must be unique", screens.size, screens.distinct().size)
    }

    // ── UserAction action constants ──────────────────────────────────────────────

    @Test
    fun `ACTION_FILTER_DEFAULT equals filterDefault`() {
        assertEquals("filterDefault", AppAnalytics.ACTION_FILTER_DEFAULT)
    }

    @Test
    fun `ACTION_FILTER_SECURITY_RISK equals filterSecurityRisk`() {
        assertEquals("filterSecurityRisk", AppAnalytics.ACTION_FILTER_SECURITY_RISK)
    }

    @Test
    fun `ACTION_FILTER_VISA_REQUIREMENTS equals filterVisaRequirements`() {
        assertEquals("filterVisaRequirements", AppAnalytics.ACTION_FILTER_VISA_REQUIREMENTS)
    }

    @Test
    fun `ACTION_FILTER_PASSPORT_VALIDITY equals filterPassportValidity`() {
        assertEquals("filterPassportValidity", AppAnalytics.ACTION_FILTER_PASSPORT_VALIDITY)
    }

    @Test
    fun `ACTION_FILTER_YELLOW_FEVER equals filterYellowFever`() {
        assertEquals("filterYellowFever", AppAnalytics.ACTION_FILTER_YELLOW_FEVER)
    }

    @Test
    fun `ACTION_FILTER_MALARIA equals filterMalaria`() {
        assertEquals("filterMalaria", AppAnalytics.ACTION_FILTER_MALARIA)
    }

    @Test
    fun `all filter action constants are distinct`() {
        val filters = listOf(
            AppAnalytics.ACTION_FILTER_DEFAULT,
            AppAnalytics.ACTION_FILTER_SECURITY_RISK,
            AppAnalytics.ACTION_FILTER_VISA_REQUIREMENTS,
            AppAnalytics.ACTION_FILTER_PASSPORT_VALIDITY,
            AppAnalytics.ACTION_FILTER_YELLOW_FEVER,
            AppAnalytics.ACTION_FILTER_MALARIA
        )
        assertEquals("All filter action constants must be unique", filters.size, filters.distinct().size)
    }

    @Test
    fun `ACTION_LEGEND_OPENED equals legendOpened`() {
        assertEquals("legendOpened", AppAnalytics.ACTION_LEGEND_OPENED)
    }

    @Test
    fun `ACTION_LEGEND_CLOSED equals legendClosed`() {
        assertEquals("legendClosed", AppAnalytics.ACTION_LEGEND_CLOSED)
    }

    @Test
    fun `ACTION_SEARCH_FOCUSED equals searchFocused`() {
        assertEquals("searchFocused", AppAnalytics.ACTION_SEARCH_FOCUSED)
    }

    @Test
    fun `ACTION_COUNTRY_SELECTED equals countrySelected`() {
        assertEquals("countrySelected", AppAnalytics.ACTION_COUNTRY_SELECTED)
    }

    @Test
    fun `ACTION_ADVISORY_US_OPENED equals advisoryUsOpened`() {
        assertEquals("advisoryUsOpened", AppAnalytics.ACTION_ADVISORY_US_OPENED)
    }

    @Test
    fun `ACTION_ADVISORY_UK_OPENED equals advisoryUkOpened`() {
        assertEquals("advisoryUkOpened", AppAnalytics.ACTION_ADVISORY_UK_OPENED)
    }

    @Test
    fun `ACTION_ADVISORY_CA_OPENED equals advisoryCaOpened`() {
        assertEquals("advisoryCaOpened", AppAnalytics.ACTION_ADVISORY_CA_OPENED)
    }

    @Test
    fun `ACTION_ADVISORY_AU_OPENED equals advisoryAuOpened`() {
        assertEquals("advisoryAuOpened", AppAnalytics.ACTION_ADVISORY_AU_OPENED)
    }

    @Test
    fun `all advisory action constants are distinct`() {
        val advisoryActions = listOf(
            AppAnalytics.ACTION_ADVISORY_US_OPENED,
            AppAnalytics.ACTION_ADVISORY_UK_OPENED,
            AppAnalytics.ACTION_ADVISORY_CA_OPENED,
            AppAnalytics.ACTION_ADVISORY_AU_OPENED
        )
        assertEquals("Advisory action constants must be unique", advisoryActions.size, advisoryActions.distinct().size)
    }

    @Test
    fun `ACTION_USD_CHANGED equals usdChanged`() {
        assertEquals("usdChanged", AppAnalytics.ACTION_USD_CHANGED)
    }

    @Test
    fun `ACTION_FOREIGN_CHANGED equals foreignChanged`() {
        assertEquals("foreignChanged", AppAnalytics.ACTION_FOREIGN_CHANGED)
    }

    @Test
    fun `currency action constants are distinct`() {
        assertDistinct(AppAnalytics.ACTION_USD_CHANGED, AppAnalytics.ACTION_FOREIGN_CHANGED)
    }

    @Test
    fun `ACTION_DETAIL_DISMISSED equals countryInfoDismissed`() {
        assertEquals("countryInfoDismissed", AppAnalytics.ACTION_DETAIL_DISMISSED)
    }

    @Test
    fun `all UserAction action constants are distinct`() {
        val actions = listOf(
            AppAnalytics.ACTION_FILTER_DEFAULT,
            AppAnalytics.ACTION_FILTER_SECURITY_RISK,
            AppAnalytics.ACTION_FILTER_VISA_REQUIREMENTS,
            AppAnalytics.ACTION_FILTER_PASSPORT_VALIDITY,
            AppAnalytics.ACTION_FILTER_YELLOW_FEVER,
            AppAnalytics.ACTION_FILTER_MALARIA,
            AppAnalytics.ACTION_LEGEND_OPENED,
            AppAnalytics.ACTION_LEGEND_CLOSED,
            AppAnalytics.ACTION_SEARCH_FOCUSED,
            AppAnalytics.ACTION_COUNTRY_SELECTED,
            AppAnalytics.ACTION_ADVISORY_US_OPENED,
            AppAnalytics.ACTION_ADVISORY_UK_OPENED,
            AppAnalytics.ACTION_ADVISORY_CA_OPENED,
            AppAnalytics.ACTION_ADVISORY_AU_OPENED,
            AppAnalytics.ACTION_USD_CHANGED,
            AppAnalytics.ACTION_FOREIGN_CHANGED,
            AppAnalytics.ACTION_DETAIL_DISMISSED
        )
        assertEquals("UserAction action constants must be unique", actions.size, actions.distinct().size)
    }

    // ── MapGesture action constants ──────────────────────────────────────────────

    @Test
    fun `ACTION_ZOOMED equals zoomed`() {
        assertEquals("zoomed", AppAnalytics.ACTION_ZOOMED)
    }

    @Test
    fun `ACTION_PANNED equals panned`() {
        assertEquals("panned", AppAnalytics.ACTION_PANNED)
    }

    @Test
    fun `MapGesture action constants are distinct`() {
        assertDistinct(AppAnalytics.ACTION_ZOOMED, AppAnalytics.ACTION_PANNED)
    }

    @Test
    fun `MapGesture actions do not collide with UserAction actions`() {
        val userActions = setOf(
            AppAnalytics.ACTION_FILTER_DEFAULT,
            AppAnalytics.ACTION_FILTER_SECURITY_RISK,
            AppAnalytics.ACTION_FILTER_VISA_REQUIREMENTS,
            AppAnalytics.ACTION_FILTER_PASSPORT_VALIDITY,
            AppAnalytics.ACTION_FILTER_YELLOW_FEVER,
            AppAnalytics.ACTION_FILTER_MALARIA,
            AppAnalytics.ACTION_LEGEND_OPENED,
            AppAnalytics.ACTION_LEGEND_CLOSED,
            AppAnalytics.ACTION_SEARCH_FOCUSED,
            AppAnalytics.ACTION_COUNTRY_SELECTED,
            AppAnalytics.ACTION_ADVISORY_US_OPENED,
            AppAnalytics.ACTION_ADVISORY_UK_OPENED,
            AppAnalytics.ACTION_ADVISORY_CA_OPENED,
            AppAnalytics.ACTION_ADVISORY_AU_OPENED,
            AppAnalytics.ACTION_USD_CHANGED,
            AppAnalytics.ACTION_FOREIGN_CHANGED,
            AppAnalytics.ACTION_DETAIL_DISMISSED
        )
        val gestureActions = setOf(AppAnalytics.ACTION_ZOOMED, AppAnalytics.ACTION_PANNED)
        assertEquals(
            "MapGesture and UserAction action values must not overlap",
            emptySet<String>(),
            userActions.intersect(gestureActions)
        )
    }

    // ── Pan direction constants ───────────────────────────────────────────────────

    @Test
    fun `PAN_LEFT equals left`() {
        assertEquals("left", AppAnalytics.PAN_LEFT)
    }

    @Test
    fun `PAN_RIGHT equals right`() {
        assertEquals("right", AppAnalytics.PAN_RIGHT)
    }

    @Test
    fun `PAN_UP equals up`() {
        assertEquals("up", AppAnalytics.PAN_UP)
    }

    @Test
    fun `PAN_DOWN equals down`() {
        assertEquals("down", AppAnalytics.PAN_DOWN)
    }

    @Test
    fun `all pan direction constants are distinct`() {
        val directions = listOf(
            AppAnalytics.PAN_LEFT,
            AppAnalytics.PAN_RIGHT,
            AppAnalytics.PAN_UP,
            AppAnalytics.PAN_DOWN
        )
        assertEquals("All pan direction constants must be unique", directions.size, directions.distinct().size)
    }

    // ── Source constants ─────────────────────────────────────────────────────────

    @Test
    fun `SOURCE_MAP equals map`() {
        assertEquals("map", AppAnalytics.SOURCE_MAP)
    }

    @Test
    fun `SOURCE_SEARCH equals search`() {
        assertEquals("search", AppAnalytics.SOURCE_SEARCH)
    }

    @Test
    fun `source constants are distinct`() {
        assertDistinct(AppAnalytics.SOURCE_MAP, AppAnalytics.SOURCE_SEARCH)
    }

    // ── Pan direction values match determinePanDirection output ──────────────────

    @Test
    fun `PAN_LEFT matches determinePanDirection for left swipe`() {
        val direction = com.unstampedpages.app.ui.screens.countryinfo.determinePanDirection(-100f, 5f)
        assertEquals(AppAnalytics.PAN_LEFT, direction)
    }

    @Test
    fun `PAN_RIGHT matches determinePanDirection for right swipe`() {
        val direction = com.unstampedpages.app.ui.screens.countryinfo.determinePanDirection(100f, 5f)
        assertEquals(AppAnalytics.PAN_RIGHT, direction)
    }

    @Test
    fun `PAN_UP matches determinePanDirection for upward swipe`() {
        val direction = com.unstampedpages.app.ui.screens.countryinfo.determinePanDirection(5f, -100f)
        assertEquals(AppAnalytics.PAN_UP, direction)
    }

    @Test
    fun `PAN_DOWN matches determinePanDirection for downward swipe`() {
        val direction = com.unstampedpages.app.ui.screens.countryinfo.determinePanDirection(5f, 100f)
        assertEquals(AppAnalytics.PAN_DOWN, direction)
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private fun assertDistinct(a: String, b: String) {
        assert(a != b) { "Expected '$a' and '$b' to be distinct" }
    }
}
