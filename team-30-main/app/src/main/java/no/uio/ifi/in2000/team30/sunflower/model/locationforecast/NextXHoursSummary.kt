package no.uio.ifi.in2000.team30.sunflower.model.locationforecast

/**
 * data class, representing the forecasts next X hours details containing
 * symbol code.
 * current json hierarchy: "properties" -> "timeseries" -> "next_X_hours" -> "summary" -> here
 */
data class NextXHoursSummary(
    val symbol_code: String
)