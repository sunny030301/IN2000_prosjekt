package no.uio.ifi.in2000.team30.sunflower.model.locationforecast

/**
 * data class, representing the forecasts metadata properties containing
 * metadata info about last updated time and measured units.
 * current json hierarchy: "properties" -> "meta" -> here
 */
data class Meta(
    val updated_at: String,
    val units: MetaUnits,
)
