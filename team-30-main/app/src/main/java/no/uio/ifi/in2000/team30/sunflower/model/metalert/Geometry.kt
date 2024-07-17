package no.uio.ifi.in2000.team30.sunflower.model.metalert

/**
 * data class, representing the weather alerts geometry containing
 * information about type and coordinates.
 * current json hierarchy: "features" -> "geometry" -> here
 */
data class Geometry(
    val coordinates: List<List<List<Any>>>, // geojson coordinates, use https://geojson.io/ for testing
    val type: String
)