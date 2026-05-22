package com.unstampedpages.app.data.model

/**
 * Represents a country's geographic boundary as a list of polygon coordinates.
 * Coordinates are in latitude/longitude (WGS84).
 */
data class CountryGeometry(
    val countryId: String,
    val polygons: List<List<LatLng>>,
    val holes: List<List<LatLng>> = emptyList()
)

/**
 * Geographic coordinate in latitude/longitude.
 */
data class LatLng(
    val lat: Float,
    val lng: Float
)
