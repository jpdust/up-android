package com.unstampedpages.app.analytics

import org.junit.Assert.*
import org.junit.Test

class AnalyticsEventsTest {

    @Test
    fun `HOME_VIEWED has correct value`() {
        assertEquals("home_viewed", AnalyticsEvents.HOME_VIEWED)
    }

    @Test
    fun `COUNTRY_INFO_VIEWED has correct value`() {
        assertEquals("country_info_viewed", AnalyticsEvents.COUNTRY_INFO_VIEWED)
    }

    @Test
    fun `CHECKLIST_VIEWED has correct value`() {
        assertEquals("checklist_viewed", AnalyticsEvents.CHECKLIST_VIEWED)
    }

    @Test
    fun `TRIP_LOG_VIEWED has correct value`() {
        assertEquals("trip_log_viewed", AnalyticsEvents.TRIP_LOG_VIEWED)
    }

    @Test
    fun `COUNTRY_SELECTED has correct value`() {
        assertEquals("country_selected", AnalyticsEvents.COUNTRY_SELECTED)
    }

    @Test
    fun `COUNTRY_DETAILS_VIEWED has correct value`() {
        assertEquals("country_details_viewed", AnalyticsEvents.COUNTRY_DETAILS_VIEWED)
    }

    @Test
    fun `CHECKLIST_ITEM_ADDED has correct value`() {
        assertEquals("checklist_item_added", AnalyticsEvents.CHECKLIST_ITEM_ADDED)
    }

    @Test
    fun `CHECKLIST_ITEM_CHECKED has correct value`() {
        assertEquals("checklist_item_checked", AnalyticsEvents.CHECKLIST_ITEM_CHECKED)
    }

    @Test
    fun `CHECKLIST_ITEM_DELETED has correct value`() {
        assertEquals("checklist_item_deleted", AnalyticsEvents.CHECKLIST_ITEM_DELETED)
    }

    @Test
    fun `JOURNAL_ENTRY_CREATED has correct value`() {
        assertEquals("journal_entry_created", AnalyticsEvents.JOURNAL_ENTRY_CREATED)
    }

    @Test
    fun `JOURNAL_ENTRY_UPDATED has correct value`() {
        assertEquals("journal_entry_updated", AnalyticsEvents.JOURNAL_ENTRY_UPDATED)
    }

    @Test
    fun `JOURNAL_ENTRY_DELETED has correct value`() {
        assertEquals("journal_entry_deleted", AnalyticsEvents.JOURNAL_ENTRY_DELETED)
    }

    @Test
    fun `all event names are unique`() {
        val events = listOf(
            AnalyticsEvents.HOME_VIEWED,
            AnalyticsEvents.COUNTRY_INFO_VIEWED,
            AnalyticsEvents.CHECKLIST_VIEWED,
            AnalyticsEvents.TRIP_LOG_VIEWED,
            AnalyticsEvents.COUNTRY_SELECTED,
            AnalyticsEvents.COUNTRY_DETAILS_VIEWED,
            AnalyticsEvents.CHECKLIST_ITEM_ADDED,
            AnalyticsEvents.CHECKLIST_ITEM_CHECKED,
            AnalyticsEvents.CHECKLIST_ITEM_DELETED,
            AnalyticsEvents.JOURNAL_ENTRY_CREATED,
            AnalyticsEvents.JOURNAL_ENTRY_UPDATED,
            AnalyticsEvents.JOURNAL_ENTRY_DELETED
        )

        assertEquals(events.size, events.distinct().size)
    }

    @Test
    fun `event names follow snake_case convention`() {
        val events = listOf(
            AnalyticsEvents.HOME_VIEWED,
            AnalyticsEvents.COUNTRY_INFO_VIEWED,
            AnalyticsEvents.CHECKLIST_VIEWED,
            AnalyticsEvents.TRIP_LOG_VIEWED,
            AnalyticsEvents.COUNTRY_SELECTED,
            AnalyticsEvents.COUNTRY_DETAILS_VIEWED,
            AnalyticsEvents.CHECKLIST_ITEM_ADDED,
            AnalyticsEvents.CHECKLIST_ITEM_CHECKED,
            AnalyticsEvents.CHECKLIST_ITEM_DELETED,
            AnalyticsEvents.JOURNAL_ENTRY_CREATED,
            AnalyticsEvents.JOURNAL_ENTRY_UPDATED,
            AnalyticsEvents.JOURNAL_ENTRY_DELETED
        )

        val snakeCaseRegex = Regex("^[a-z]+(_[a-z]+)*$")
        events.forEach { event ->
            assertTrue("Event '$event' should be snake_case", snakeCaseRegex.matches(event))
        }
    }
}
