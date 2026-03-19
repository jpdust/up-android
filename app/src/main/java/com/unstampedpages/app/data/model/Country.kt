package com.unstampedpages.app.data.model

import androidx.compose.ui.graphics.Color

data class Country(
    val id: String,
    val name: String,
    val safetyLevel: SafetyLevel,
    val visaRequirement: String,
    val currency: String,
    val currencyCode: String,
    val exchangeRateToUSD: Double,
    val outletType: String,
    val continent: Continent,
    val flagEmoji: String,
    val imageUrl: String? = null
)

enum class SafetyLevel(val displayName: String, val color: Color) {
    LOW("Low Risk", Color(0xFF4CAF50)),
    MEDIUM("Medium Risk", Color(0xFFFFC107)),
    HIGH("High Risk", Color(0xFFFF9800)),
    EXTREME("Extreme Risk", Color(0xFFFF5722))
}

enum class Continent(val displayName: String) {
    NORTH_AMERICA("North America"),
    SOUTH_AMERICA("South America"),
    EUROPE("Europe"),
    AFRICA("Africa"),
    ASIA("Asia"),
    OCEANIA("Oceania"),
    ANTARCTICA("Antarctica")
}

data class CountryMapData(
    val countryId: String,
    val centerX: Float,
    val centerY: Float,
    val radius: Float
)
