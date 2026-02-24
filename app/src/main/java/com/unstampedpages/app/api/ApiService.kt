package com.unstampedpages.app.api

import com.unstampedpages.app.data.local.entity.ChecklistItem
import com.unstampedpages.app.data.local.entity.TripLogEntry

/**
 * API Service interface for future backend integration.
 *
 * This interface defines the contract for communicating with Java-based APIs
 * for storing customer information and syncing data.
 *
 * Implementation options:
 * - Retrofit with OkHttp for REST APIs
 * - gRPC for high-performance communication
 * - GraphQL with Apollo Client
 */
interface ApiService {

    /**
     * User/Customer endpoints
     */
    suspend fun registerUser(email: String, password: String): Result<UserResponse>
    suspend fun loginUser(email: String, password: String): Result<AuthResponse>
    suspend fun getUserProfile(): Result<UserProfile>
    suspend fun updateUserProfile(profile: UserProfile): Result<UserProfile>

    /**
     * Checklist sync endpoints
     */
    suspend fun syncChecklistItems(items: List<ChecklistItem>): Result<List<ChecklistItem>>
    suspend fun getChecklistItems(): Result<List<ChecklistItem>>

    /**
     * Trip log sync endpoints
     */
    suspend fun syncTripLogEntries(entries: List<TripLogEntry>): Result<List<TripLogEntry>>
    suspend fun getTripLogEntries(): Result<List<TripLogEntry>>

    /**
     * Country data endpoints (for live updates)
     */
    suspend fun getCountryData(countryId: String): Result<CountryApiResponse>
    suspend fun getExchangeRates(): Result<ExchangeRatesResponse>
}

/**
 * API response data classes
 */
data class UserResponse(
    val id: String,
    val email: String,
    val createdAt: Long
)

data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val expiresAt: Long,
    val user: UserResponse
)

data class UserProfile(
    val id: String,
    val email: String,
    val displayName: String?,
    val avatarUrl: String?,
    val preferences: Map<String, Any>
)

data class CountryApiResponse(
    val id: String,
    val name: String,
    val population: Long,
    val safetyLevel: String,
    val currency: String,
    val currencyCode: String,
    val exchangeRateToUSD: Double,
    val outletType: String,
    val lastUpdated: Long
)

data class ExchangeRatesResponse(
    val baseCurrency: String,
    val rates: Map<String, Double>,
    val lastUpdated: Long
)

/**
 * API Configuration
 */
object ApiConfig {
    // Base URL for the API (to be configured)
    var baseUrl: String = "https://api.unstampedpages.com"

    // API version
    const val API_VERSION = "v1"

    // Timeouts (in seconds)
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // Headers
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_CONTENT_TYPE = "Content-Type"
    const val CONTENT_TYPE_JSON = "application/json"
}
