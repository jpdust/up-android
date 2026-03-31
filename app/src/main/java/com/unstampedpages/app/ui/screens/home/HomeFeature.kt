package com.unstampedpages.app.ui.screens.home

/**
 * Represents a feature card displayed on the home screen.
 * This data class encapsulates the content shown in each feature card.
 */
data class HomeFeature(
    val id: String,
    val title: String,
    val description: String,
    val route: HomeRoute
)

/**
 * Navigation routes available from the home screen.
 */
enum class HomeRoute(val displayName: String) {
    COUNTRIES("Countries"),
    CHECKLIST("Checklist"),
    TRIP_LOG("Trip Log"),
    MY_STAMPS("My Stamps")
}

/**
 * Provider for home screen features.
 * Centralizes the feature definitions for testability.
 */
object HomeFeatureProvider {

    /**
     * Returns the list of features to display on the home screen.
     */
    fun getFeatures(): List<HomeFeature> = listOf(
        HomeFeature(
            id = "explore_countries",
            title = "Explore Countries",
            description = "Tap the globe to discover country info, safety levels, and travel tips.",
            route = HomeRoute.COUNTRIES
        ),
        HomeFeature(
            id = "travel_checklist",
            title = "Travel Checklist",
            description = "Never forget essentials. Keep track of what to bring on your adventure.",
            route = HomeRoute.CHECKLIST
        ),
        HomeFeature(
            id = "trip_journal",
            title = "Trip Journal",
            description = "Record your memories. Document each day of your journey.",
            route = HomeRoute.TRIP_LOG
        ),
        HomeFeature(
            id = "passport_stamps",
            title = "My Passport Stamps",
            description = "Upload pictures of your passport stamps to each country you visit as a digital record.",
            route = HomeRoute.MY_STAMPS
        )
    )

    /**
     * Returns a feature by its ID, or null if not found.
     */
    fun getFeatureById(id: String): HomeFeature? {
        return getFeatures().find { it.id == id }
    }

    /**
     * Returns features for a specific route.
     */
    fun getFeaturesByRoute(route: HomeRoute): List<HomeFeature> {
        return getFeatures().filter { it.route == route }
    }
}

/**
 * Home screen content configuration.
 */
object HomeContent {
    const val APP_TITLE = "UNSTAMPED PAGES"
    const val APP_TAGLINE = "Your Adventure Awaits"
    const val WELCOME_TITLE = "Welcome, Explorer"
    const val WELCOME_MESSAGE = "Document your journeys across the globe. " +
            "Discover new countries, track your travels, " +
            "and never forget what to pack."
    const val QUOTE_TEXT = "\"The world is a book, and those who do not travel read only one page.\""
    const val QUOTE_AUTHOR = "— Saint Augustine"
}
