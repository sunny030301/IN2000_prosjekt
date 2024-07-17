package no.uio.ifi.in2000.team30.sunflower.model.places.autocomplete

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val id: String = "",
    val name: String = "",
    val main_text: String = "",
    val secondary_text: String = "",
    val types: List<String> = emptyList()
)