package no.uio.ifi.in2000.team30.sunflower.model.places.autocomplete

import kotlinx.serialization.Serializable

@Serializable
data class StructuredFormatting(
    val main_text: String = "",
    val main_text_matched_substrings: List<MainTextMatchedSubstring>,
    val secondary_text: String = ""
)