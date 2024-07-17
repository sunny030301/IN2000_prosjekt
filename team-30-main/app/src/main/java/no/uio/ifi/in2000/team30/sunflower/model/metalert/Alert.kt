package no.uio.ifi.in2000.team30.sunflower.model.metalert

/**
 * data class, representing the weather alerts features containing
 * information about geometry, properties, type and when.
 * current json hierarchy: "features" -> here
 */
data class Alert(
    val geometry: Geometry,
    val properties: Properties, // event, description, area, awareness_level etc.
    val type: String, // usually "Feature"
    val `when`: When
)