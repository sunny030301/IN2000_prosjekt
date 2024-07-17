package no.uio.ifi.in2000.team30.sunflower.model.places.autocomplete

import kotlinx.serialization.Serializable

@Serializable
data class Places(
    val predictions: List<Prediction>,
    val status: String
) {
    fun toPlacesList() = predictions.map { it.toPlace() }
}