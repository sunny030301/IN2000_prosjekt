package no.uio.ifi.in2000.team30.sunflower.model.locationforecast

/**
 * data class, representing the outer JSON object containing
 * information about type, geometry, and properties.
 * current json hierarchy: here (root)
 */
data class LocationForecast (
    val type: String,
    val geometry: Geometry,
    val properties: Properties
)