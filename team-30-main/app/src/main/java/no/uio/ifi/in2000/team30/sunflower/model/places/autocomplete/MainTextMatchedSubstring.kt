package no.uio.ifi.in2000.team30.sunflower.model.places.autocomplete

import kotlinx.serialization.Serializable

@Serializable
data class MainTextMatchedSubstring(
    val length: Int,
    val offset: Int
)