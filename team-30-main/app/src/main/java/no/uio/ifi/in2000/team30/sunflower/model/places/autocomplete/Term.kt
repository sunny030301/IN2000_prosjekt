package no.uio.ifi.in2000.team30.sunflower.model.places.autocomplete

import kotlinx.serialization.Serializable

@Serializable
data class Term(
    val offset: Int,
    val value: String
)