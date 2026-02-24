package com.unstampedpages.app.analytics

import android.content.Context
import android.util.Log

/**
 * Analytics manager for tracking user events.
 * Currently stubbed for future NewRelic integration.
 *
 * To enable NewRelic:
 * 1. Add dependency: implementation("com.newrelic.agent.android:android-agent:7.3.0")
 * 2. Add NewRelic application token in AndroidManifest.xml
 * 3. Initialize NewRelic.withApplicationToken() in Application.onCreate()
 * 4. Replace stub implementations with actual NewRelic calls
 */
object AnalyticsManager {

    private const val TAG = "AnalyticsManager"
    private var isInitialized = false

    /**
     * Initialize analytics SDK.
     * Call this in Application.onCreate() or MainActivity.onCreate()
     */
    fun initialize(context: Context) {
        if (isInitialized) return

        // TODO: Initialize NewRelic
        // NewRelic.withApplicationToken("YOUR_TOKEN").start(context)

        isInitialized = true
        Log.d(TAG, "Analytics initialized (stub)")
    }

    /**
     * Track a custom event with optional attributes.
     */
    fun trackEvent(eventName: String, attributes: Map<String, Any> = emptyMap()) {
        if (!isInitialized) {
            Log.w(TAG, "Analytics not initialized, skipping event: $eventName")
            return
        }

        // TODO: Replace with NewRelic.recordCustomEvent()
        // NewRelic.recordCustomEvent("UnstampedPages", eventName, attributes)

        Log.d(TAG, "Event tracked (stub): $eventName, attributes: $attributes")
    }

    /**
     * Track screen view.
     */
    fun trackScreenView(screenName: String) {
        trackEvent("screen_view", mapOf("screen_name" to screenName))
    }

    /**
     * Track user action.
     */
    fun trackUserAction(action: String, details: Map<String, Any> = emptyMap()) {
        trackEvent("user_action", mapOf("action" to action) + details)
    }

    /**
     * Track error or exception.
     */
    fun trackError(error: Throwable, context: String? = null) {
        // TODO: Replace with NewRelic.recordHandledException()
        // NewRelic.recordHandledException(error)

        val attributes = mutableMapOf<String, Any>(
            "error_type" to (error::class.simpleName ?: "Unknown"),
            "error_message" to (error.message ?: "No message")
        )
        context?.let { attributes["context"] = it }

        trackEvent("error", attributes)
    }

    /**
     * Set user identifier for analytics.
     */
    fun setUserId(userId: String) {
        // TODO: Replace with NewRelic.setUserId()
        // NewRelic.setUserId(userId)

        Log.d(TAG, "User ID set (stub): $userId")
    }

    /**
     * Set custom attribute for all future events.
     */
    fun setAttribute(name: String, value: Any) {
        // TODO: Replace with NewRelic.setAttribute()
        // NewRelic.setAttribute(name, value)

        Log.d(TAG, "Attribute set (stub): $name = $value")
    }
}

/**
 * Pre-defined analytics events for the app.
 */
object AnalyticsEvents {
    // Navigation events
    const val HOME_VIEWED = "home_viewed"
    const val COUNTRY_INFO_VIEWED = "country_info_viewed"
    const val CHECKLIST_VIEWED = "checklist_viewed"
    const val TRIP_LOG_VIEWED = "trip_log_viewed"

    // Country events
    const val COUNTRY_SELECTED = "country_selected"
    const val COUNTRY_DETAILS_VIEWED = "country_details_viewed"

    // Checklist events
    const val CHECKLIST_ITEM_ADDED = "checklist_item_added"
    const val CHECKLIST_ITEM_CHECKED = "checklist_item_checked"
    const val CHECKLIST_ITEM_DELETED = "checklist_item_deleted"

    // Trip log events
    const val JOURNAL_ENTRY_CREATED = "journal_entry_created"
    const val JOURNAL_ENTRY_UPDATED = "journal_entry_updated"
    const val JOURNAL_ENTRY_DELETED = "journal_entry_deleted"
}
