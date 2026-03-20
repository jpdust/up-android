package com.unstampedpages.app.api

import android.util.Log
import com.unstampedpages.app.data.local.entity.ChecklistItem
import com.unstampedpages.app.data.local.entity.TripLogEntry

private const val API_NOT_YET_IMPLEMENTED = "API not yet implemented"

/**
 * Stub implementation of ApiService for development.
 *
 * Replace this with actual implementation using Retrofit when backend is ready:
 *
 * ```kotlin
 * class ApiServiceImpl(private val retrofit: Retrofit) : ApiService {
 *     private val api = retrofit.create(UnstampedPagesApi::class.java)
 *     // ... implement methods using api calls
 * }
 * ```
 */
class ApiServiceImpl : ApiService {

    companion object {
        private const val TAG = "ApiServiceImpl"
    }

    override suspend fun registerUser(email: String, password: String): Result<UserResponse> {
        Log.d(TAG, "registerUser (stub): $email")
        return Result.failure(NotImplementedError(API_NOT_YET_IMPLEMENTED))
    }

    override suspend fun loginUser(email: String, password: String): Result<AuthResponse> {
        Log.d(TAG, "loginUser (stub): $email")
        return Result.failure(NotImplementedError(API_NOT_YET_IMPLEMENTED))
    }

    override suspend fun getUserProfile(): Result<UserProfile> {
        Log.d(TAG, "getUserProfile (stub)")
        return Result.failure(NotImplementedError(API_NOT_YET_IMPLEMENTED))
    }

    override suspend fun updateUserProfile(profile: UserProfile): Result<UserProfile> {
        Log.d(TAG, "updateUserProfile (stub): ${profile.id}")
        return Result.failure(NotImplementedError(API_NOT_YET_IMPLEMENTED))
    }

    override suspend fun syncChecklistItems(items: List<ChecklistItem>): Result<List<ChecklistItem>> {
        Log.d(TAG, "syncChecklistItems (stub): ${items.size} items")
        return Result.failure(NotImplementedError(API_NOT_YET_IMPLEMENTED))
    }

    override suspend fun getChecklistItems(): Result<List<ChecklistItem>> {
        Log.d(TAG, "getChecklistItems (stub)")
        return Result.failure(NotImplementedError(API_NOT_YET_IMPLEMENTED))
    }

    override suspend fun syncTripLogEntries(entries: List<TripLogEntry>): Result<List<TripLogEntry>> {
        Log.d(TAG, "syncTripLogEntries (stub): ${entries.size} entries")
        return Result.failure(NotImplementedError(API_NOT_YET_IMPLEMENTED))
    }

    override suspend fun getTripLogEntries(): Result<List<TripLogEntry>> {
        Log.d(TAG, "getTripLogEntries (stub)")
        return Result.failure(NotImplementedError(API_NOT_YET_IMPLEMENTED))
    }

    override suspend fun getCountryData(countryId: String): Result<CountryApiResponse> {
        Log.d(TAG, "getCountryData (stub): $countryId")
        return Result.failure(NotImplementedError(API_NOT_YET_IMPLEMENTED))
    }

    override suspend fun getExchangeRates(): Result<ExchangeRatesResponse> {
        Log.d(TAG, "getExchangeRates (stub)")
        return Result.failure(NotImplementedError(API_NOT_YET_IMPLEMENTED))
    }
}

/**
 * Factory for creating ApiService instances.
 *
 * Usage:
 * ```kotlin
 * val apiService = ApiServiceFactory.create()
 * ```
 *
 * When implementing with Retrofit:
 * ```kotlin
 * object ApiServiceFactory {
 *     fun create(authToken: String? = null): ApiService {
 *         val okHttpClient = OkHttpClient.Builder()
 *             .connectTimeout(ApiConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
 *             .readTimeout(ApiConfig.READ_TIMEOUT, TimeUnit.SECONDS)
 *             .writeTimeout(ApiConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
 *             .addInterceptor { chain ->
 *                 val request = chain.request().newBuilder()
 *                     .header(ApiConfig.HEADER_CONTENT_TYPE, ApiConfig.CONTENT_TYPE_JSON)
 *                 authToken?.let {
 *                     request.header(ApiConfig.HEADER_AUTHORIZATION, "Bearer $it")
 *                 }
 *                 chain.proceed(request.build())
 *             }
 *             .build()
 *
 *         val retrofit = Retrofit.Builder()
 *             .baseUrl("${ApiConfig.baseUrl}/${ApiConfig.API_VERSION}/")
 *             .client(okHttpClient)
 *             .addConverterFactory(GsonConverterFactory.create())
 *             .build()
 *
 *         return ApiServiceImpl(retrofit)
 *     }
 * }
 * ```
 */
object ApiServiceFactory {
    fun create(): ApiService {
        return ApiServiceImpl()
    }
}
