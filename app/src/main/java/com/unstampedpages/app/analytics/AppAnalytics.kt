package com.unstampedpages.app.analytics

import com.newrelic.agent.android.NewRelic

/**
 * App-wide New Relic analytics.
 *
 * All user interactions are stored in two event tables:
 *
 *   UserAction  — every deliberate tap, selection, or form input across all screens,
 *                 including bottom-navigation tab taps ([ACTION_TAB_SELECTED]).
 *                 Filtered by [SCREEN_*] and [ACTION_*] attributes.
 *
 *   MapGesture  — high-frequency pinch-zoom and pan gestures on the world map,
 *                 kept separate to avoid skewing UserAction counts and to allow
 *                 independent retention / sampling if needed.
 *
 * Attribute keys are camelCase. All values are strings or primitives — no PII.
 *
 * Example NRQL:
 *   SELECT count(*) FROM UserAction FACET screen, action SINCE 7 days ago
 *   SELECT count(*) FROM UserAction WHERE screen = 'countries' FACET action
 *   SELECT average(zoomLevel) FROM MapGesture WHERE action = 'zoomed'
 */
object AppAnalytics {

    // ── Event tables ─────────────────────────────────────────────────────────────

    private const val EVENT_USER_ACTION  = "UserAction"
    private const val EVENT_MAP_GESTURE  = "MapGesture"

    // ── Attribute keys ──────────────────────────────────────────────────────────

    private const val ATTR_SCREEN        = "screen"
    private const val ATTR_ACTION        = "action"
    private const val ATTR_DIRECTION     = "direction"
    private const val ATTR_ZOOMED_IN     = "zoomedIn"
    private const val ATTR_ZOOM_LEVEL    = "zoomLevel"
    private const val ATTR_COUNTRY_ID    = "countryId"
    private const val ATTR_COUNTRY_NAME  = "countryName"
    private const val ATTR_SOURCE        = "source"
    private const val ATTR_CURRENCY_CODE = "currencyCode"

    // ── Screen constants ─────────────────────────────────────────────────────────

    const val SCREEN_HOME      = "home"
    const val SCREEN_COUNTRIES = "countries"
    const val SCREEN_CHECKLIST = "checklist"
    const val SCREEN_TRIP_LOG  = "tripLog"
    const val SCREEN_STAMPS    = "stamps"

    // ── Action constants — UserAction ────────────────────────────────────────────

    const val ACTION_TAB_SELECTED              = "tabSelected"
    const val ACTION_FILTER_DEFAULT           = "filterDefault"
    const val ACTION_FILTER_SECURITY_RISK     = "filterSecurityRisk"
    const val ACTION_FILTER_VISA_REQUIREMENTS = "filterVisaRequirements"
    const val ACTION_FILTER_PASSPORT_VALIDITY = "filterPassportValidity"
    const val ACTION_FILTER_YELLOW_FEVER      = "filterYellowFever"
    const val ACTION_FILTER_MALARIA           = "filterMalaria"
    const val ACTION_FILTER_TRAFFIC_SIDE     = "filterTrafficSide"
    const val ACTION_LEGEND_OPENED            = "legendOpened"
    const val ACTION_LEGEND_CLOSED        = "legendClosed"
    const val ACTION_SEARCH_FOCUSED       = "searchFocused"
    const val ACTION_COUNTRY_SELECTED     = "countrySelected"
    const val ACTION_ADVISORY_US_OPENED   = "advisoryUsOpened"
    const val ACTION_ADVISORY_UK_OPENED   = "advisoryUkOpened"
    const val ACTION_ADVISORY_CA_OPENED   = "advisoryCaOpened"
    const val ACTION_ADVISORY_AU_OPENED   = "advisoryAuOpened"
    const val ACTION_USD_CHANGED          = "usdChanged"
    const val ACTION_FOREIGN_CHANGED      = "foreignChanged"
    const val ACTION_DETAIL_DISMISSED     = "countryInfoDismissed"

    // ── Action constants — MapGesture ────────────────────────────────────────────

    const val ACTION_ZOOMED = "zoomed"
    const val ACTION_PANNED = "panned"

    // ── Pan directions ───────────────────────────────────────────────────────────

    const val PAN_LEFT  = "left"
    const val PAN_RIGHT = "right"
    const val PAN_UP    = "up"
    const val PAN_DOWN  = "down"

    // ── Source values ────────────────────────────────────────────────────────────

    const val SOURCE_MAP    = "map"
    const val SOURCE_SEARCH = "search"

    // ── Bottom navigation (UserAction) ──────────────────────────────────────────

    /**
     * User taps a bottom navigation tab.
     *
     * @param tab One of [SCREEN_HOME], [SCREEN_COUNTRIES], [SCREEN_CHECKLIST],
     *            [SCREEN_TRIP_LOG], [SCREEN_STAMPS].
     */
    fun trackTabSelected(tab: String) {
        NewRelic.recordCustomEvent(
            EVENT_USER_ACTION,
            mapOf(
                ATTR_SCREEN to tab,
                ATTR_ACTION to ACTION_TAB_SELECTED
            )
        )
    }

    // ── Countries tab — map filter selection (UserAction) ───────────────────────

    /** User selects the Default map filter. */
    fun trackDefaultFilterSelected() = recordMapFilter(ACTION_FILTER_DEFAULT)

    /** User selects the Security Risk map filter. */
    fun trackSecurityRiskFilterSelected() = recordMapFilter(ACTION_FILTER_SECURITY_RISK)

    /** User selects the Visa Requirements map filter. */
    fun trackVisaRequirementsFilterSelected() = recordMapFilter(ACTION_FILTER_VISA_REQUIREMENTS)

    /** User selects the Passport Validity map filter. */
    fun trackPassportValidityFilterSelected() = recordMapFilter(ACTION_FILTER_PASSPORT_VALIDITY)

    /** User selects the Yellow Fever map filter. */
    fun trackYellowFeverFilterSelected() = recordMapFilter(ACTION_FILTER_YELLOW_FEVER)

    /** User selects the Malaria map filter. */
    fun trackMalariaFilterSelected() = recordMapFilter(ACTION_FILTER_MALARIA)

    fun trackTrafficSideFilterSelected() = recordMapFilter(ACTION_FILTER_TRAFFIC_SIDE)

    private fun recordMapFilter(action: String) {
        NewRelic.recordCustomEvent(
            EVENT_USER_ACTION,
            mapOf(
                ATTR_SCREEN to SCREEN_COUNTRIES,
                ATTR_ACTION to action
            )
        )
    }

    // ── Countries tab — map controls (UserAction) ────────────────────────────────

    /** User taps the compass icon to open the map legend. */
    fun trackMapLegendOpened() {
        NewRelic.recordCustomEvent(
            EVENT_USER_ACTION,
            mapOf(
                ATTR_SCREEN to SCREEN_COUNTRIES,
                ATTR_ACTION to ACTION_LEGEND_OPENED
            )
        )
    }

    /** User taps the X button to close the map legend. */
    fun trackMapLegendClosed() {
        NewRelic.recordCustomEvent(
            EVENT_USER_ACTION,
            mapOf(
                ATTR_SCREEN to SCREEN_COUNTRIES,
                ATTR_ACTION to ACTION_LEGEND_CLOSED
            )
        )
    }

    // ── Countries tab — map gestures (MapGesture) ────────────────────────────────

    /**
     * Pinch-to-zoom gesture completed.
     *
     * @param zoomedIn `true` if the final scale is larger than the starting scale.
     * @param zoomLevel The scale factor at gesture end (1× = default, >1 = zoomed in).
     */
    fun trackMapZoomed(zoomedIn: Boolean, zoomLevel: Float) {
        NewRelic.recordCustomEvent(
            EVENT_MAP_GESTURE,
            mapOf(
                ATTR_ACTION     to ACTION_ZOOMED,
                ATTR_ZOOMED_IN  to zoomedIn,
                ATTR_ZOOM_LEVEL to zoomLevel
            )
        )
    }

    /**
     * Pan gesture completed.
     *
     * @param direction Dominant direction: [PAN_LEFT], [PAN_RIGHT], [PAN_UP], or [PAN_DOWN].
     */
    fun trackMapPanned(direction: String) {
        NewRelic.recordCustomEvent(
            EVENT_MAP_GESTURE,
            mapOf(
                ATTR_ACTION    to ACTION_PANNED,
                ATTR_DIRECTION to direction
            )
        )
    }

    // ── Countries tab — search and selection (UserAction) ────────────────────────

    /** User focuses the country search bar. */
    fun trackCountrySearchFocused() {
        NewRelic.recordCustomEvent(
            EVENT_USER_ACTION,
            mapOf(
                ATTR_SCREEN to SCREEN_COUNTRIES,
                ATTR_ACTION to ACTION_SEARCH_FOCUSED
            )
        )
    }

    /**
     * User selects a country via map tap or search result.
     *
     * @param source [SOURCE_MAP] or [SOURCE_SEARCH].
     */
    fun trackCountrySelected(countryId: String, countryName: String, source: String) {
        NewRelic.recordCustomEvent(
            EVENT_USER_ACTION,
            mapOf(
                ATTR_SCREEN       to SCREEN_COUNTRIES,
                ATTR_ACTION       to ACTION_COUNTRY_SELECTED,
                ATTR_COUNTRY_ID   to countryId,
                ATTR_COUNTRY_NAME to countryName,
                ATTR_SOURCE       to source
            )
        )
    }

    // ── Countries tab — travel advisories (UserAction) ───────────────────────────

    /** User taps the US State Department travel advisory chip. */
    fun trackUsAdvisoryOpened(countryId: String, countryName: String) {
        NewRelic.recordCustomEvent(
            EVENT_USER_ACTION,
            mapOf(
                ATTR_SCREEN       to SCREEN_COUNTRIES,
                ATTR_ACTION       to ACTION_ADVISORY_US_OPENED,
                ATTR_COUNTRY_ID   to countryId,
                ATTR_COUNTRY_NAME to countryName
            )
        )
    }

    /** User taps the UK Foreign Office travel advisory chip. */
    fun trackUkAdvisoryOpened(countryId: String, countryName: String) {
        NewRelic.recordCustomEvent(
            EVENT_USER_ACTION,
            mapOf(
                ATTR_SCREEN       to SCREEN_COUNTRIES,
                ATTR_ACTION       to ACTION_ADVISORY_UK_OPENED,
                ATTR_COUNTRY_ID   to countryId,
                ATTR_COUNTRY_NAME to countryName
            )
        )
    }

    /** User taps the Canadian government travel advisory chip. */
    fun trackCaAdvisoryOpened(countryId: String, countryName: String) {
        NewRelic.recordCustomEvent(
            EVENT_USER_ACTION,
            mapOf(
                ATTR_SCREEN       to SCREEN_COUNTRIES,
                ATTR_ACTION       to ACTION_ADVISORY_CA_OPENED,
                ATTR_COUNTRY_ID   to countryId,
                ATTR_COUNTRY_NAME to countryName
            )
        )
    }

    /** User taps the Australian Smartraveller travel advisory chip. */
    fun trackAuAdvisoryOpened(countryId: String, countryName: String) {
        NewRelic.recordCustomEvent(
            EVENT_USER_ACTION,
            mapOf(
                ATTR_SCREEN       to SCREEN_COUNTRIES,
                ATTR_ACTION       to ACTION_ADVISORY_AU_OPENED,
                ATTR_COUNTRY_ID   to countryId,
                ATTR_COUNTRY_NAME to countryName
            )
        )
    }

    // ── Countries tab — currency converter (UserAction) ──────────────────────────

    /** User edits the USD input field in the currency converter. */
    fun trackUsdChanged(countryId: String, countryName: String, currencyCode: String) {
        NewRelic.recordCustomEvent(
            EVENT_USER_ACTION,
            mapOf(
                ATTR_SCREEN        to SCREEN_COUNTRIES,
                ATTR_ACTION        to ACTION_USD_CHANGED,
                ATTR_COUNTRY_ID    to countryId,
                ATTR_COUNTRY_NAME  to countryName,
                ATTR_CURRENCY_CODE to currencyCode
            )
        )
    }

    /** User edits the foreign currency input field in the currency converter. */
    fun trackForeignChanged(countryId: String, countryName: String, currencyCode: String) {
        NewRelic.recordCustomEvent(
            EVENT_USER_ACTION,
            mapOf(
                ATTR_SCREEN        to SCREEN_COUNTRIES,
                ATTR_ACTION        to ACTION_FOREIGN_CHANGED,
                ATTR_COUNTRY_ID    to countryId,
                ATTR_COUNTRY_NAME  to countryName,
                ATTR_CURRENCY_CODE to currencyCode
            )
        )
    }

    // ── Countries tab — detail sheet (UserAction) ────────────────────────────────

    /** User dismisses the country detail bottom sheet. */
    fun trackCountryDetailDismissed(countryId: String, countryName: String) {
        NewRelic.recordCustomEvent(
            EVENT_USER_ACTION,
            mapOf(
                ATTR_SCREEN       to SCREEN_COUNTRIES,
                ATTR_ACTION       to ACTION_DETAIL_DISMISSED,
                ATTR_COUNTRY_ID   to countryId,
                ATTR_COUNTRY_NAME to countryName
            )
        )
    }
}
