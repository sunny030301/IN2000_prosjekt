package no.uio.ifi.in2000.team30.sunflower.model.places.placedetails

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val geometry: Geometry,
    val name: String,
    val place_id: String,
    val types: List<String>
)
