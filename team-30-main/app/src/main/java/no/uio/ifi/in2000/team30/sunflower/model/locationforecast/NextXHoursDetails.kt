package no.uio.ifi.in2000.team30.sunflower.model.locationforecast

/**
 * data class, representing the forecasts next X hours details containing
 * information about precipitation amount.
 * current json hierarchy: "properties" -> "timeseries" -> "next_X_hours" -> "details" -> here
 */
data class NextXHoursDetails(
    val precipitation_amount: Double,
)
