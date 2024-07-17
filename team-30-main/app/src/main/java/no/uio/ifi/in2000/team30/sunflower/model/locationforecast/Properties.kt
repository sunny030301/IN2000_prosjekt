package no.uio.ifi.in2000.team30.sunflower.model.locationforecast

/**
 * data class, representing LocationForecasts properties containing
 * information about metadata and timeseries.
 * current json hierarchy: "properties" -> here
 */
data class Properties (
    val meta: Meta,
    val timeseries: List<Timeseries>
)