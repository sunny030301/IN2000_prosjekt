package no.uio.ifi.in2000.team30.sunflower.model.locationforecast

/**
 * data class, representing the forecasts geometry containing
 * information about type and coordinates.
 * current json hierarchy: "geometry" -> here
 */
data class Geometry (
    val type: String,
    val coordinates: List<Double>
)