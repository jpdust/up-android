package com.unstampedpages.app.api

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ApiServiceImplTest {

    private lateinit var apiService: ApiServiceImpl

    @Before
    fun setUp() {
        apiService = ApiServiceImpl()
    }

    @Test
    fun `registerUser returns failure with NotImplementedError`() = runTest {
        val result = apiService.registerUser("test@example.com", "password123")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotImplementedError)
    }

    @Test
    fun `loginUser returns failure with NotImplementedError`() = runTest {
        val result = apiService.loginUser("test@example.com", "password123")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotImplementedError)
    }

    @Test
    fun `getUserProfile returns failure with NotImplementedError`() = runTest {
        val result = apiService.getUserProfile()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotImplementedError)
    }

    @Test
    fun `updateUserProfile returns failure with NotImplementedError`() = runTest {
        val profile = UserProfile(
            id = "123",
            email = "test@example.com",
            displayName = "Test User",
            avatarUrl = null,
            preferences = emptyMap()
        )
        val result = apiService.updateUserProfile(profile)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotImplementedError)
    }

    @Test
    fun `syncChecklistItems returns failure with NotImplementedError`() = runTest {
        val result = apiService.syncChecklistItems(emptyList())

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotImplementedError)
    }

    @Test
    fun `getChecklistItems returns failure with NotImplementedError`() = runTest {
        val result = apiService.getChecklistItems()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotImplementedError)
    }

    @Test
    fun `syncTripLogEntries returns failure with NotImplementedError`() = runTest {
        val result = apiService.syncTripLogEntries(emptyList())

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotImplementedError)
    }

    @Test
    fun `getTripLogEntries returns failure with NotImplementedError`() = runTest {
        val result = apiService.getTripLogEntries()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotImplementedError)
    }

    @Test
    fun `getCountryData returns failure with NotImplementedError`() = runTest {
        val result = apiService.getCountryData("US")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotImplementedError)
    }

    @Test
    fun `getExchangeRates returns failure with NotImplementedError`() = runTest {
        val result = apiService.getExchangeRates()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotImplementedError)
    }
}

class ApiServiceFactoryTest {

    @Test
    fun `create returns ApiServiceImpl instance`() {
        val service = ApiServiceFactory.create()

        assertTrue(service is ApiServiceImpl)
    }

    @Test
    fun `create returns new instance each time`() {
        val service1 = ApiServiceFactory.create()
        val service2 = ApiServiceFactory.create()

        assertNotSame(service1, service2)
    }
}

class ApiConfigTest {

    @Test
    fun `baseUrl has default value`() {
        assertEquals("https://api.unstampedpages.com", ApiConfig.baseUrl)
    }

    @Test
    fun `baseUrl can be changed`() {
        val original = ApiConfig.baseUrl
        ApiConfig.baseUrl = "https://staging.unstampedpages.com"

        assertEquals("https://staging.unstampedpages.com", ApiConfig.baseUrl)

        // Reset
        ApiConfig.baseUrl = original
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

class ApiResponseDataClassesTest {

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
}
