package com.unstampedpages.app.ui.screens.home

import org.junit.Assert.*
import org.junit.Test

class HomeFeatureTest {

    // ==================== Constructor Tests ====================

    @Test
    fun `HomeFeature can be created with all required fields`() {
        val feature = HomeFeature(
            id = "test_id",
            title = "Test Title",
            description = "Test Description",
            route = HomeRoute.COUNTRIES
        )

        assertEquals("test_id", feature.id)
        assertEquals("Test Title", feature.title)
        assertEquals("Test Description", feature.description)
        assertEquals(HomeRoute.COUNTRIES, feature.route)
    }

    @Test
    fun `HomeFeature can have empty id`() {
        val feature = HomeFeature(
            id = "",
            title = "Title",
            description = "Description",
            route = HomeRoute.CHECKLIST
        )

        assertEquals("", feature.id)
    }

    @Test
    fun `HomeFeature can have empty title`() {
        val feature = HomeFeature(
            id = "id",
            title = "",
            description = "Description",
            route = HomeRoute.TRIP_LOG
        )

        assertEquals("", feature.title)
    }

    @Test
    fun `HomeFeature can have empty description`() {
        val feature = HomeFeature(
            id = "id",
            title = "Title",
            description = "",
            route = HomeRoute.MY_STAMPS
        )

        assertEquals("", feature.description)
    }

    @Test
    fun `HomeFeature can have long description`() {
        val longDescription = "A".repeat(500)
        val feature = HomeFeature(
            id = "id",
            title = "Title",
            description = longDescription,
            route = HomeRoute.COUNTRIES
        )

        assertEquals(longDescription, feature.description)
    }

    // ==================== Equality Tests ====================

    @Test
    fun `HomeFeature equality works correctly`() {
        val feature1 = HomeFeature("id", "Title", "Desc", HomeRoute.COUNTRIES)
        val feature2 = HomeFeature("id", "Title", "Desc", HomeRoute.COUNTRIES)
        val feature3 = HomeFeature("id2", "Title", "Desc", HomeRoute.COUNTRIES)

        assertEquals(feature1, feature2)
        assertNotEquals(feature1, feature3)
    }

    @Test
    fun `HomeFeature equality considers all fields`() {
        val base = HomeFeature("id", "Title", "Desc", HomeRoute.COUNTRIES)
        val differentId = HomeFeature("id2", "Title", "Desc", HomeRoute.COUNTRIES)
        val differentTitle = HomeFeature("id", "Title2", "Desc", HomeRoute.COUNTRIES)
        val differentDesc = HomeFeature("id", "Title", "Desc2", HomeRoute.COUNTRIES)
        val differentRoute = HomeFeature("id", "Title", "Desc", HomeRoute.CHECKLIST)

        assertNotEquals(base, differentId)
        assertNotEquals(base, differentTitle)
        assertNotEquals(base, differentDesc)
        assertNotEquals(base, differentRoute)
    }

    // ==================== HashCode Tests ====================

    @Test
    fun `HomeFeature hashCode is consistent`() {
        val feature = HomeFeature("id", "Title", "Desc", HomeRoute.COUNTRIES)
        val hash1 = feature.hashCode()
        val hash2 = feature.hashCode()

        assertEquals(hash1, hash2)
    }

    @Test
    fun `equal HomeFeatures have same hashCode`() {
        val feature1 = HomeFeature("id", "Title", "Desc", HomeRoute.COUNTRIES)
        val feature2 = HomeFeature("id", "Title", "Desc", HomeRoute.COUNTRIES)

        assertEquals(feature1.hashCode(), feature2.hashCode())
    }

    // ==================== Copy Tests ====================

    @Test
    fun `HomeFeature copy works correctly`() {
        val original = HomeFeature("id", "Title", "Desc", HomeRoute.COUNTRIES)
        val copy = original.copy(title = "New Title")

        assertEquals("Title", original.title)
        assertEquals("New Title", copy.title)
        assertEquals(original.id, copy.id)
        assertEquals(original.description, copy.description)
        assertEquals(original.route, copy.route)
    }

    @Test
    fun `HomeFeature copy can update all fields`() {
        val original = HomeFeature("id", "Title", "Desc", HomeRoute.COUNTRIES)
        val copy = original.copy(
            id = "new_id",
            title = "New Title",
            description = "New Desc",
            route = HomeRoute.MY_STAMPS
        )

        assertEquals("new_id", copy.id)
        assertEquals("New Title", copy.title)
        assertEquals("New Desc", copy.description)
        assertEquals(HomeRoute.MY_STAMPS, copy.route)
    }

    // ==================== Destructuring Tests ====================

    @Test
    fun `HomeFeature destructuring works correctly`() {
        val feature = HomeFeature("test_id", "Test Title", "Test Desc", HomeRoute.TRIP_LOG)
        val (id, title, description, route) = feature

        assertEquals("test_id", id)
        assertEquals("Test Title", title)
        assertEquals("Test Desc", description)
        assertEquals(HomeRoute.TRIP_LOG, route)
    }

    // ==================== toString Tests ====================

    @Test
    fun `HomeFeature toString contains field values`() {
        val feature = HomeFeature("my_id", "My Title", "My Desc", HomeRoute.CHECKLIST)
        val string = feature.toString()

        assertTrue(string.contains("my_id"))
        assertTrue(string.contains("My Title"))
        assertTrue(string.contains("My Desc"))
        assertTrue(string.contains("CHECKLIST"))
    }
}

class HomeRouteTest {

    // ==================== Enum Entries Tests ====================

    @Test
    fun `HomeRoute has 4 routes`() {
        assertEquals(4, HomeRoute.entries.size)
    }

    @Test
    fun `HomeRoute contains COUNTRIES`() {
        assertTrue(HomeRoute.entries.contains(HomeRoute.COUNTRIES))
    }

    @Test
    fun `HomeRoute contains CHECKLIST`() {
        assertTrue(HomeRoute.entries.contains(HomeRoute.CHECKLIST))
    }

    @Test
    fun `HomeRoute contains TRIP_LOG`() {
        assertTrue(HomeRoute.entries.contains(HomeRoute.TRIP_LOG))
    }

    @Test
    fun `HomeRoute contains MY_STAMPS`() {
        assertTrue(HomeRoute.entries.contains(HomeRoute.MY_STAMPS))
    }

    // ==================== Display Name Tests ====================

    @Test
    fun `COUNTRIES has correct displayName`() {
        assertEquals("Countries", HomeRoute.COUNTRIES.displayName)
    }

    @Test
    fun `CHECKLIST has correct displayName`() {
        assertEquals("Checklist", HomeRoute.CHECKLIST.displayName)
    }

    @Test
    fun `TRIP_LOG has correct displayName`() {
        assertEquals("Trip Log", HomeRoute.TRIP_LOG.displayName)
    }

    @Test
    fun `MY_STAMPS has correct displayName`() {
        assertEquals("My Stamps", HomeRoute.MY_STAMPS.displayName)
    }

    @Test
    fun `all routes have non-empty displayName`() {
        HomeRoute.entries.forEach { route ->
            assertTrue(
                "Route ${route.name} should have non-empty displayName",
                route.displayName.isNotBlank()
            )
        }
    }

    @Test
    fun `all displayNames are unique`() {
        val displayNames = HomeRoute.entries.map { it.displayName }
        assertEquals(displayNames.size, displayNames.distinct().size)
    }

    // ==================== valueOf Tests ====================

    @Test
    fun `valueOf COUNTRIES returns COUNTRIES`() {
        assertEquals(HomeRoute.COUNTRIES, HomeRoute.valueOf("COUNTRIES"))
    }

    @Test
    fun `valueOf CHECKLIST returns CHECKLIST`() {
        assertEquals(HomeRoute.CHECKLIST, HomeRoute.valueOf("CHECKLIST"))
    }

    @Test
    fun `valueOf TRIP_LOG returns TRIP_LOG`() {
        assertEquals(HomeRoute.TRIP_LOG, HomeRoute.valueOf("TRIP_LOG"))
    }

    @Test
    fun `valueOf MY_STAMPS returns MY_STAMPS`() {
        assertEquals(HomeRoute.MY_STAMPS, HomeRoute.valueOf("MY_STAMPS"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf with invalid name throws IllegalArgumentException`() {
        HomeRoute.valueOf("INVALID")
    }

    // ==================== Ordinal Tests ====================

    @Test
    fun `COUNTRIES has ordinal 0`() {
        assertEquals(0, HomeRoute.COUNTRIES.ordinal)
    }

    @Test
    fun `CHECKLIST has ordinal 1`() {
        assertEquals(1, HomeRoute.CHECKLIST.ordinal)
    }

    @Test
    fun `TRIP_LOG has ordinal 2`() {
        assertEquals(2, HomeRoute.TRIP_LOG.ordinal)
    }

    @Test
    fun `MY_STAMPS has ordinal 3`() {
        assertEquals(3, HomeRoute.MY_STAMPS.ordinal)
    }

    // ==================== Name Property Tests ====================

    @Test
    fun `name property returns correct values`() {
        assertEquals("COUNTRIES", HomeRoute.COUNTRIES.name)
        assertEquals("CHECKLIST", HomeRoute.CHECKLIST.name)
        assertEquals("TRIP_LOG", HomeRoute.TRIP_LOG.name)
        assertEquals("MY_STAMPS", HomeRoute.MY_STAMPS.name)
    }

    // ==================== Equality Tests ====================

    @Test
    fun `same route equals itself`() {
        assertEquals(HomeRoute.COUNTRIES, HomeRoute.COUNTRIES)
    }

    @Test
    fun `different routes are not equal`() {
        assertNotEquals(HomeRoute.COUNTRIES, HomeRoute.CHECKLIST)
    }
}

class HomeFeatureProviderTest {

    // ==================== getFeatures Tests ====================

    @Test
    fun `getFeatures returns 4 features`() {
        val features = HomeFeatureProvider.getFeatures()
        assertEquals(4, features.size)
    }

    @Test
    fun `getFeatures returns features in expected order`() {
        val features = HomeFeatureProvider.getFeatures()

        assertEquals("explore_countries", features[0].id)
        assertEquals("travel_checklist", features[1].id)
        assertEquals("trip_journal", features[2].id)
        assertEquals("passport_stamps", features[3].id)
    }

    @Test
    fun `getFeatures contains explore countries feature`() {
        val features = HomeFeatureProvider.getFeatures()
        val exploreCountries = features.find { it.id == "explore_countries" }

        assertNotNull(exploreCountries)
        assertEquals("Explore Countries", exploreCountries?.title)
        assertEquals(HomeRoute.COUNTRIES, exploreCountries?.route)
    }

    @Test
    fun `getFeatures contains travel checklist feature`() {
        val features = HomeFeatureProvider.getFeatures()
        val checklist = features.find { it.id == "travel_checklist" }

        assertNotNull(checklist)
        assertEquals("Travel Checklist", checklist?.title)
        assertEquals(HomeRoute.CHECKLIST, checklist?.route)
    }

    @Test
    fun `getFeatures contains trip journal feature`() {
        val features = HomeFeatureProvider.getFeatures()
        val journal = features.find { it.id == "trip_journal" }

        assertNotNull(journal)
        assertEquals("Trip Journal", journal?.title)
        assertEquals(HomeRoute.TRIP_LOG, journal?.route)
    }

    @Test
    fun `getFeatures contains passport stamps feature`() {
        val features = HomeFeatureProvider.getFeatures()
        val stamps = features.find { it.id == "passport_stamps" }

        assertNotNull(stamps)
        assertEquals("My Passport Stamps", stamps?.title)
        assertEquals(HomeRoute.MY_STAMPS, stamps?.route)
    }

    @Test
    fun `all features have unique ids`() {
        val features = HomeFeatureProvider.getFeatures()
        val ids = features.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `all features have non-empty titles`() {
        HomeFeatureProvider.getFeatures().forEach { feature ->
            assertTrue(
                "Feature ${feature.id} should have non-empty title",
                feature.title.isNotBlank()
            )
        }
    }

    @Test
    fun `all features have non-empty descriptions`() {
        HomeFeatureProvider.getFeatures().forEach { feature ->
            assertTrue(
                "Feature ${feature.id} should have non-empty description",
                feature.description.isNotBlank()
            )
        }
    }

    @Test
    fun `all features have valid routes`() {
        HomeFeatureProvider.getFeatures().forEach { feature ->
            assertTrue(
                "Feature ${feature.id} should have a valid route",
                HomeRoute.entries.contains(feature.route)
            )
        }
    }

    @Test
    fun `each route has exactly one feature`() {
        val features = HomeFeatureProvider.getFeatures()
        HomeRoute.entries.forEach { route ->
            val featuresForRoute = features.filter { it.route == route }
            assertEquals(
                "Route $route should have exactly one feature",
                1,
                featuresForRoute.size
            )
        }
    }

    // ==================== getFeatureById Tests ====================

    @Test
    fun `getFeatureById returns feature for valid id`() {
        val feature = HomeFeatureProvider.getFeatureById("explore_countries")

        assertNotNull(feature)
        assertEquals("Explore Countries", feature?.title)
    }

    @Test
    fun `getFeatureById returns null for invalid id`() {
        val feature = HomeFeatureProvider.getFeatureById("invalid_id")

        assertNull(feature)
    }

    @Test
    fun `getFeatureById returns null for empty id`() {
        val feature = HomeFeatureProvider.getFeatureById("")

        assertNull(feature)
    }

    @Test
    fun `getFeatureById is case sensitive`() {
        val feature = HomeFeatureProvider.getFeatureById("EXPLORE_COUNTRIES")

        assertNull(feature)
    }

    @Test
    fun `getFeatureById works for all feature ids`() {
        val ids = listOf("explore_countries", "travel_checklist", "trip_journal", "passport_stamps")
        ids.forEach { id ->
            val feature = HomeFeatureProvider.getFeatureById(id)
            assertNotNull("Feature with id '$id' should exist", feature)
            assertEquals(id, feature?.id)
        }
    }

    // ==================== getFeaturesByRoute Tests ====================

    @Test
    fun `getFeaturesByRoute returns features for COUNTRIES`() {
        val features = HomeFeatureProvider.getFeaturesByRoute(HomeRoute.COUNTRIES)

        assertEquals(1, features.size)
        assertEquals("explore_countries", features[0].id)
    }

    @Test
    fun `getFeaturesByRoute returns features for CHECKLIST`() {
        val features = HomeFeatureProvider.getFeaturesByRoute(HomeRoute.CHECKLIST)

        assertEquals(1, features.size)
        assertEquals("travel_checklist", features[0].id)
    }

    @Test
    fun `getFeaturesByRoute returns features for TRIP_LOG`() {
        val features = HomeFeatureProvider.getFeaturesByRoute(HomeRoute.TRIP_LOG)

        assertEquals(1, features.size)
        assertEquals("trip_journal", features[0].id)
    }

    @Test
    fun `getFeaturesByRoute returns features for MY_STAMPS`() {
        val features = HomeFeatureProvider.getFeaturesByRoute(HomeRoute.MY_STAMPS)

        assertEquals(1, features.size)
        assertEquals("passport_stamps", features[0].id)
    }

    @Test
    fun `getFeaturesByRoute returns list not null`() {
        HomeRoute.entries.forEach { route ->
            val features = HomeFeatureProvider.getFeaturesByRoute(route)
            assertNotNull(features)
        }
    }

    // ==================== Consistency Tests ====================

    @Test
    fun `getFeatures returns same result on multiple calls`() {
        val features1 = HomeFeatureProvider.getFeatures()
        val features2 = HomeFeatureProvider.getFeatures()

        assertEquals(features1.size, features2.size)
        features1.zip(features2).forEach { (f1, f2) ->
            assertEquals(f1, f2)
        }
    }

    @Test
    fun `feature descriptions are informative`() {
        HomeFeatureProvider.getFeatures().forEach { feature ->
            assertTrue(
                "Feature ${feature.id} description should be at least 20 characters",
                feature.description.length >= 20
            )
        }
    }
}

class HomeContentTest {

    // ==================== App Title Tests ====================

    @Test
    fun `APP_TITLE is UNSTAMPED PAGES`() {
        assertEquals("UNSTAMPED PAGES", HomeContent.APP_TITLE)
    }

    @Test
    fun `APP_TITLE is uppercase`() {
        assertEquals(HomeContent.APP_TITLE.uppercase(), HomeContent.APP_TITLE)
    }

    @Test
    fun `APP_TITLE is not empty`() {
        assertTrue(HomeContent.APP_TITLE.isNotBlank())
    }

    // ==================== App Tagline Tests ====================

    @Test
    fun `APP_TAGLINE is correct`() {
        assertEquals("Your Adventure Awaits", HomeContent.APP_TAGLINE)
    }

    @Test
    fun `APP_TAGLINE is not empty`() {
        assertTrue(HomeContent.APP_TAGLINE.isNotBlank())
    }

    // ==================== Welcome Title Tests ====================

    @Test
    fun `WELCOME_TITLE is correct`() {
        assertEquals("Welcome, Explorer", HomeContent.WELCOME_TITLE)
    }

    @Test
    fun `WELCOME_TITLE is not empty`() {
        assertTrue(HomeContent.WELCOME_TITLE.isNotBlank())
    }

    // ==================== Welcome Message Tests ====================

    @Test
    fun `WELCOME_MESSAGE is not empty`() {
        assertTrue(HomeContent.WELCOME_MESSAGE.isNotBlank())
    }

    @Test
    fun `WELCOME_MESSAGE contains key words`() {
        assertTrue(HomeContent.WELCOME_MESSAGE.contains("journeys"))
        assertTrue(HomeContent.WELCOME_MESSAGE.contains("countries"))
        assertTrue(HomeContent.WELCOME_MESSAGE.contains("travels"))
    }

    @Test
    fun `WELCOME_MESSAGE is reasonably long`() {
        assertTrue(HomeContent.WELCOME_MESSAGE.length >= 50)
    }

    // ==================== Quote Tests ====================

    @Test
    fun `QUOTE_TEXT is not empty`() {
        assertTrue(HomeContent.QUOTE_TEXT.isNotBlank())
    }

    @Test
    fun `QUOTE_TEXT contains quotation marks`() {
        assertTrue(HomeContent.QUOTE_TEXT.contains("\""))
    }

    @Test
    fun `QUOTE_TEXT mentions travel or world`() {
        assertTrue(
            HomeContent.QUOTE_TEXT.contains("travel") ||
            HomeContent.QUOTE_TEXT.contains("world")
        )
    }

    @Test
    fun `QUOTE_AUTHOR is correct`() {
        assertEquals("— Saint Augustine", HomeContent.QUOTE_AUTHOR)
    }

    @Test
    fun `QUOTE_AUTHOR starts with em dash`() {
        assertTrue(HomeContent.QUOTE_AUTHOR.startsWith("—"))
    }

    @Test
    fun `QUOTE_AUTHOR is not empty`() {
        assertTrue(HomeContent.QUOTE_AUTHOR.isNotBlank())
    }

    // ==================== Content Relationship Tests ====================

    @Test
    fun `all content values are distinct`() {
        val values = listOf(
            HomeContent.APP_TITLE,
            HomeContent.APP_TAGLINE,
            HomeContent.WELCOME_TITLE,
            HomeContent.WELCOME_MESSAGE,
            HomeContent.QUOTE_TEXT,
            HomeContent.QUOTE_AUTHOR
        )
        assertEquals(values.size, values.distinct().size)
    }

    @Test
    fun `content values are consistent with travel theme`() {
        val allContent = listOf(
            HomeContent.APP_TITLE,
            HomeContent.APP_TAGLINE,
            HomeContent.WELCOME_TITLE,
            HomeContent.WELCOME_MESSAGE,
            HomeContent.QUOTE_TEXT
        ).joinToString(" ").lowercase()

        // Should contain at least one travel-related word
        val travelWords = listOf("adventure", "travel", "journey", "explore", "world", "pages")
        assertTrue(
            "Content should contain travel-related words",
            travelWords.any { allContent.contains(it) }
        )
    }
}
