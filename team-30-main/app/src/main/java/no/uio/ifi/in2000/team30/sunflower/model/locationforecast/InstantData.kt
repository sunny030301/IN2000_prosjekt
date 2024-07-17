package no.uio.ifi.in2000.team30.sunflower.model.locationforecast

/**
 * data class, representing the forecasts instant timeseries data containing
 * information about air pressure at sea level, air temperature, cloud area,
 * relative humidity, wind from direction, wind speed.
 * current json hierarchy: "properties" -> "timeseries" -> "instant" -> "details" -> here
 */
data class InstantData(
    val air_pressure_at_sea_level: Double = 0.0,
    val air_temperature: Double = 0.0,
    val cloud_area_fraction: Double = 0.0,
    val relative_humidity: Double = 0.0,
    val wind_from_direction: Double = 0.0,
    val wind_speed: Double = 0.0,
)

