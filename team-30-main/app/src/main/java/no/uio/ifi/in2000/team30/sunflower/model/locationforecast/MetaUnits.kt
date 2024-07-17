package no.uio.ifi.in2000.team30.sunflower.model.locationforecast

/**
 * data class, representing the forecasts metadata units containing
 * information about each measured metric and their respective measured units.
 * current json hierarchy: "properties" -> "meta" -> "units" -> here
 */
data class MetaUnits(
    val air_pressure_at_sea_level: String = "",
    val air_temperature: String = "",
    val cloud_area_fraction: String = "",
    val precipitation_amount: String = "",
    val relative_humidity: String = "",
    val wind_from_direction: String = "",
    val wind_speed: String = "",
)
