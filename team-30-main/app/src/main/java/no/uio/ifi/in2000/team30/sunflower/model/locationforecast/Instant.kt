package no.uio.ifi.in2000.team30.sunflower.model.locationforecast

/**
 * data class, representing the forecasts instant timeseries containing
 * information about details.
 * current json hierarchy: "properties" -> "timeseries" -> "instant" -> here
 */
data class Instant(
    val details: InstantData
)