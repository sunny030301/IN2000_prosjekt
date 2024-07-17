package no.uio.ifi.in2000.team30.sunflower.model.locationforecast

/**
 * data class, representing the forecasts timeseries properties containing
 * weather data for selected timeframes.
 * current json hierarchy: "properties" -> "timeseries" -> "data" -> here
 */
data class TimeseriesData(
    val instant: Instant,
    val next_12_hours: NextXHours?,
    val next_1_hours: NextXHours?,
    val next_6_hours: NextXHours?
)
