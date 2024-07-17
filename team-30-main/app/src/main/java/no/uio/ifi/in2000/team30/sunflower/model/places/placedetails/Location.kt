package no.uio.ifi.in2000.team30.sunflower.model.places.placedetails

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val lat: Double,
    val lng: Double,
)
