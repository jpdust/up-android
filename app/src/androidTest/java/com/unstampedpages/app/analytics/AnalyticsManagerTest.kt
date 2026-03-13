package com.unstampedpages.app.analytics

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnalyticsManagerTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun initialize_doesNotThrow() {
        // Should not throw any exception
        AnalyticsManager.initialize(context)
    }

    @Test
    fun initialize_canBeCalledMultipleTimes() {
        // Should be idempotent
        AnalyticsManager.initialize(context)
        AnalyticsManager.initialize(context)
        AnalyticsManager.initialize(context)
    }

    @Test
    fun trackEvent_doesNotThrowWhenInitialized() {
        AnalyticsManager.initialize(context)

        // Should not throw
        AnalyticsManager.trackEvent("test_event")
    }

    @Test
    fun trackEvent_withAttributes_doesNotThrow() {
        AnalyticsManager.initialize(context)

        // Should not throw
        AnalyticsManager.trackEvent(
            "test_event",
            mapOf("key1" to "value1", "key2" to 123)
        )
    }

    @Test
    fun trackEvent_withEmptyAttributes_doesNotThrow() {
        AnalyticsManager.initialize(context)

        // Should not throw
        AnalyticsManager.trackEvent("test_event", emptyMap())
    }

    @Test
    fun trackScreenView_doesNotThrow() {
        AnalyticsManager.initialize(context)

        // Should not throw
        AnalyticsManager.trackScreenView("HomeScreen")
    }

    @Test
    fun trackUserAction_doesNotThrow() {
        AnalyticsManager.initialize(context)

        // Should not throw
        AnalyticsManager.trackUserAction("button_click")
    }

    @Test
    fun trackUserAction_withDetails_doesNotThrow() {
        AnalyticsManager.initialize(context)

        // Should not throw
        AnalyticsManager.trackUserAction(
            "button_click",
            mapOf("button_id" to "submit", "screen" to "login")
        )
    }

    @Test
    fun trackError_withException_doesNotThrow() {
        AnalyticsManager.initialize(context)

        // Should not throw
        AnalyticsManager.trackError(RuntimeException("Test error"))
    }

    @Test
    fun trackError_withContext_doesNotThrow() {
        AnalyticsManager.initialize(context)

        // Should not throw
        AnalyticsManager.trackError(
            IllegalStateException("Test error"),
            "TestContext"
        )
    }

    @Test
    fun trackError_withNullMessage_doesNotThrow() {
        AnalyticsManager.initialize(context)

        // Create exception with null message
        val exception = RuntimeException()

        // Should not throw
        AnalyticsManager.trackError(exception)
    }

    @Test
    fun setUserId_doesNotThrow() {
        AnalyticsManager.initialize(context)

        // Should not throw
        AnalyticsManager.setUserId("user123")
    }

    @Test
    fun setUserId_withEmptyString_doesNotThrow() {
        AnalyticsManager.initialize(context)

        // Should not throw
        AnalyticsManager.setUserId("")
    }

    @Test
    fun setAttribute_withString_doesNotThrow() {
        AnalyticsManager.initialize(context)

        // Should not throw
        AnalyticsManager.setAttribute("user_type", "premium")
    }

    @Test
    fun setAttribute_withNumber_doesNotThrow() {
        AnalyticsManager.initialize(context)

        // Should not throw
        AnalyticsManager.setAttribute("login_count", 5)
    }

    @Test
    fun setAttribute_withBoolean_doesNotThrow() {
        AnalyticsManager.initialize(context)

        // Should not throw
        AnalyticsManager.setAttribute("is_verified", true)
    }
}

class AnalyticsEventsInstrumentedTest {

    @Test
    fun homeViewed_hasCorrectValue() {
        assertEquals("home_viewed", AnalyticsEvents.HOME_VIEWED)
    }

    @Test
    fun countryInfoViewed_hasCorrectValue() {
        assertEquals("country_info_viewed", AnalyticsEvents.COUNTRY_INFO_VIEWED)
    }

    @Test
    fun checklistViewed_hasCorrectValue() {
        assertEquals("checklist_viewed", AnalyticsEvents.CHECKLIST_VIEWED)
    }

    @Test
    fun tripLogViewed_hasCorrectValue() {
        assertEquals("trip_log_viewed", AnalyticsEvents.TRIP_LOG_VIEWED)
    }

    @Test
    fun countrySelected_hasCorrectValue() {
        assertEquals("country_selected", AnalyticsEvents.COUNTRY_SELECTED)
    }

    @Test
    fun countryDetailsViewed_hasCorrectValue() {
        assertEquals("country_details_viewed", AnalyticsEvents.COUNTRY_DETAILS_VIEWED)
    }

    @Test
    fun checklistItemAdded_hasCorrectValue() {
        assertEquals("checklist_item_added", AnalyticsEvents.CHECKLIST_ITEM_ADDED)
    }

    @Test
    fun checklistItemChecked_hasCorrectValue() {
        assertEquals("checklist_item_checked", AnalyticsEvents.CHECKLIST_ITEM_CHECKED)
    }

    @Test
    fun checklistItemDeleted_hasCorrectValue() {
        assertEquals("checklist_item_deleted", AnalyticsEvents.CHECKLIST_ITEM_DELETED)
    }

    @Test
    fun journalEntryCreated_hasCorrectValue() {
        assertEquals("journal_entry_created", AnalyticsEvents.JOURNAL_ENTRY_CREATED)
    }

    @Test
    fun journalEntryUpdated_hasCorrectValue() {
        assertEquals("journal_entry_updated", AnalyticsEvents.JOURNAL_ENTRY_UPDATED)
    }

    @Test
    fun journalEntryDeleted_hasCorrectValue() {
        assertEquals("journal_entry_deleted", AnalyticsEvents.JOURNAL_ENTRY_DELETED)
    }
}
