package no.uio.ifi.in2000.team30.sunflower.model.metalert

/**
 * data class, representing the outer GeoJSON object containing
 * information about features, language, lastChange and type.
 * current json hierarchy: here (root)
 */
data class MetAlert(
    val features: List<Alert>,
    val lang: String,
    val lastChange: String,
    val type: String
)