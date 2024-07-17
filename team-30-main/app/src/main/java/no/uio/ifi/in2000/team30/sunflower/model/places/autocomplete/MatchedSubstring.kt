package no.uio.ifi.in2000.team30.sunflower.model.places.autocomplete

import kotlinx.serialization.Serializable

@Serializable
data class MatchedSubstring(
    val length: Int,
    val offset: Int
)