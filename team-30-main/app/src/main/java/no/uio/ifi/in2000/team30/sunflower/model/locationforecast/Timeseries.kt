package no.uio.ifi.in2000.team30.sunflower.model.locationforecast

/**
 * data class, representing the forecasts timeseries properties containing
 * weather data and time.
 * current json hierarchy: "properties" -> "timeseries" -> here
 */
data class Timeseries(
    val time: String,
    val data: TimeseriesData,
)
