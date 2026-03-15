package com.unstampedpages.app.api

import org.junit.Assert.*
import org.junit.Test

class ApiConfigTest {

    @Test
    fun `baseUrl has default value`() {
        assertEquals("https://api.unstampedpages.com", ApiConfig.baseUrl)
    }

    @Test
    fun `API_VERSION is v1`() {
        assertEquals("v1", ApiConfig.API_VERSION)
    }

    @Test
    fun `CONNECT_TIMEOUT is 30`() {
        assertEquals(30L, ApiConfig.CONNECT_TIMEOUT)
    }

    @Test
    fun `READ_TIMEOUT is 30`() {
        assertEquals(30L, ApiConfig.READ_TIMEOUT)
    }

    @Test
    fun `WRITE_TIMEOUT is 30`() {
        assertEquals(30L, ApiConfig.WRITE_TIMEOUT)
    }

    @Test
    fun `HEADER_AUTHORIZATION is correct`() {
        assertEquals("Authorization", ApiConfig.HEADER_AUTHORIZATION)
    }

    @Test
    fun `HEADER_CONTENT_TYPE is correct`() {
        assertEquals("Content-Type", ApiConfig.HEADER_CONTENT_TYPE)
    }

    @Test
    fun `CONTENT_TYPE_JSON is correct`() {
        assertEquals("application/json", ApiConfig.CONTENT_TYPE_JSON)
    }
}

class UserResponseTest {

    @Test
    fun `UserResponse holds correct values`() {
        val response = UserResponse(
            id = "user123",
            email = "test@example.com",
            createdAt = 1234567890L
        )

        assertEquals("user123", response.id)
        assertEquals("test@example.com", response.email)
        assertEquals(1234567890L, response.createdAt)
    }

    @Test
    fun `UserResponse equals works correctly`() {
        val response1 = UserResponse("123", "test@test.com", 1000L)
        val response2 = UserResponse("123", "test@test.com", 1000L)

        assertEquals(response1, response2)
    }

    @Test
    fun `UserResponse copy works correctly`() {
        val original = UserResponse("123", "test@test.com", 1000L)
        val copy = original.copy(email = "new@test.com")

        assertEquals("new@test.com", copy.email)
        assertEquals("test@test.com", original.email)
    }
}

class AuthResponseTest {

    @Test
    fun `AuthResponse holds correct values`() {
        val userResponse = UserResponse("123", "test@test.com", 0L)
        val response = AuthResponse(
            token = "jwt_token",
            refreshToken = "refresh_token",
            expiresAt = 9999999999L,
            user = userResponse
        )

        assertEquals("jwt_token", response.token)
        assertEquals("refresh_token", response.refreshToken)
        assertEquals(9999999999L, response.expiresAt)
        assertEquals(userResponse, response.user)
    }

    @Test
    fun `AuthResponse contains nested user`() {
        val userResponse = UserResponse("456", "nested@test.com", 2000L)
        val response = AuthResponse("token", "refresh", 1000L, userResponse)

        assertEquals("456", response.user.id)
        assertEquals("nested@test.com", response.user.email)
    }
}

class UserProfileTest {

    @Test
    fun `UserProfile holds correct values`() {
        val profile = UserProfile(
            id = "user456",
            email = "user@example.com",
            displayName = "John Doe",
            avatarUrl = "https://example.com/avatar.jpg",
            preferences = mapOf("theme" to "dark", "notifications" to true)
        )

        assertEquals("user456", profile.id)
        assertEquals("user@example.com", profile.email)
        assertEquals("John Doe", profile.displayName)
        assertEquals("https://example.com/avatar.jpg", profile.avatarUrl)
        assertEquals("dark", profile.preferences["theme"])
        assertEquals(true, profile.preferences["notifications"])
    }

    @Test
    fun `UserProfile with null optional fields`() {
        val profile = UserProfile(
            id = "user789",
            email = "minimal@example.com",
            displayName = null,
            avatarUrl = null,
            preferences = emptyMap()
        )

        assertNull(profile.displayName)
        assertNull(profile.avatarUrl)
        assertTrue(profile.preferences.isEmpty())
    }

    @Test
    fun `UserProfile copy modifies values`() {
        val original = UserProfile("123", "test@test.com", "Name", null, emptyMap())
        val modified = original.copy(displayName = "New Name")

        assertEquals("New Name", modified.displayName)
        assertEquals("Name", original.displayName)
    }
}

class CountryApiResponseTest {

    @Test
    fun `CountryApiResponse holds correct values`() {
        val response = CountryApiResponse(
            id = "us",
            name = "United States",
            population = 331000000L,
            safetyLevel = "LOW",
            currency = "US Dollar",
            currencyCode = "USD",
            exchangeRateToUSD = 1.0,
            outletType = "Type A/B",
            lastUpdated = 1234567890L
        )

        assertEquals("us", response.id)
        assertEquals("United States", response.name)
        assertEquals(331000000L, response.population)
        assertEquals("LOW", response.safetyLevel)
        assertEquals("US Dollar", response.currency)
        assertEquals("USD", response.currencyCode)
        assertEquals(1.0, response.exchangeRateToUSD, 0.001)
        assertEquals("Type A/B", response.outletType)
        assertEquals(1234567890L, response.lastUpdated)
    }

    @Test
    fun `CountryApiResponse equals works correctly`() {
        val response1 = CountryApiResponse("us", "USA", 331000000L, "LOW", "Dollar", "USD", 1.0, "A/B", 1000L)
        val response2 = CountryApiResponse("us", "USA", 331000000L, "LOW", "Dollar", "USD", 1.0, "A/B", 1000L)

        assertEquals(response1, response2)
    }
}

class ExchangeRatesResponseTest {

    @Test
    fun `ExchangeRatesResponse holds correct values`() {
        val response = ExchangeRatesResponse(
            baseCurrency = "USD",
            rates = mapOf("EUR" to 0.85, "GBP" to 0.73, "JPY" to 110.0),
            lastUpdated = 1234567890L
        )

        assertEquals("USD", response.baseCurrency)
        assertEquals(3, response.rates.size)
        assertEquals(0.85, response.rates["EUR"]!!, 0.001)
        assertEquals(0.73, response.rates["GBP"]!!, 0.001)
        assertEquals(110.0, response.rates["JPY"]!!, 0.001)
        assertEquals(1234567890L, response.lastUpdated)
    }

    @Test
    fun `ExchangeRatesResponse with empty rates`() {
        val response = ExchangeRatesResponse(
            baseCurrency = "EUR",
            rates = emptyMap(),
            lastUpdated = 0L
        )

        assertTrue(response.rates.isEmpty())
    }

    @Test
    fun `ExchangeRatesResponse copy works correctly`() {
        val original = ExchangeRatesResponse("USD", mapOf("EUR" to 0.85), 1000L)
        val modified = original.copy(baseCurrency = "EUR")

        assertEquals("EUR", modified.baseCurrency)
        assertEquals("USD", original.baseCurrency)
    }
}
