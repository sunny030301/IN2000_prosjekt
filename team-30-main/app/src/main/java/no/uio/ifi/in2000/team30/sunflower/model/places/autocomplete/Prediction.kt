package no.uio.ifi.in2000.team30.sunflower.model.places.autocomplete

import kotlinx.serialization.Serializable

@Serializable
data class Prediction(
    val description: String,
    val matched_substrings: List<MatchedSubstring>,
    val place_id: String,
    val reference: String,
    val structured_formatting: StructuredFormatting,
    val terms: List<Term>,
    val types: List<String>
) {
    fun toPlace() = Place(
        id = place_id,
        name = description,
        main_text = structured_formatting.main_text,
        secondary_text = structured_formatting.secondary_text,
        types = types
    )
}