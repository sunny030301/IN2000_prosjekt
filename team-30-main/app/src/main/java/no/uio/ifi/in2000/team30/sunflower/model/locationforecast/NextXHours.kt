package no.uio.ifi.in2000.team30.sunflower.model.locationforecast

/**
 * data class, representing the forecasts next X hours timeseries containing
 * information about weather summary and details.
 * current json hierarchy: "properties" -> "timeseries" -> "next_X_hours" -> here
 */
data class NextXHours(
    val summary: NextXHoursSummary,
    val details: NextXHoursDetails
)