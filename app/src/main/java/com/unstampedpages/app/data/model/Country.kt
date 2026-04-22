package com.unstampedpages.app.data.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.unstampedpages.app.R

data class TravelAdvisories(
    val us: String,
    val uk: String,
    val au: String,
    val ca: String
)

data class Country(
    val id: String,
    val name: String,
    val safetyLevel: SafetyLevel,
    val visaRequirement: VisaRequirement,
    val currency: String,
    val currencyCode: String,
    val exchangeRateToUSD: Double,
    val outletType: String,
    val continent: Continent,
    val flagEmoji: String,
    val travelAdvisories: TravelAdvisories? = null,
    val imageUrl: String? = null,
    val passportValidity: String? = null
)

enum class SafetyLevel(@StringRes val displayNameResId: Int, val color: Color) {
    LOW(R.string.safety_low_risk, Color(0xFF4CAF50)),
    MEDIUM(R.string.safety_medium_risk, Color(0xFFFFC107)),
    HIGH(R.string.safety_high_risk, Color(0xFF8B0000)),
    EXTREME(R.string.safety_extreme_risk, Color(0xFFFF5722))
}

enum class VisaRequirement(@StringRes val displayNameResId: Int, val color: Color) {
    VISA_NOT_REQUIRED(R.string.visa_not_required, Color(0xFF4CAF50)),      // Green
    E_VISA(R.string.visa_evisa, Color(0xFF00BCD4)),                        // Turquoise
    VISA_ON_ARRIVAL(R.string.visa_on_arrival, Color(0xFFFFC107)),          // Yellow
    VISA_REQUIRED(R.string.visa_required, Color(0xFF9E9E9E)),              // Gray
    RESTRICTED(R.string.visa_restricted, Color(0xFF000000))                // Black
}

enum class Continent(@StringRes val displayNameResId: Int) {
    NORTH_AMERICA(R.string.continent_north_america),
    SOUTH_AMERICA(R.string.continent_south_america),
    EUROPE(R.string.continent_europe),
    AFRICA(R.string.continent_africa),
    ASIA(R.string.continent_asia),
    OCEANIA(R.string.continent_oceania),
    ANTARCTICA(R.string.continent_antarctica)
}

data class CountryMapData(
    val countryId: String,
    val centerX: Float,
    val centerY: Float,
    val radius: Float
)
