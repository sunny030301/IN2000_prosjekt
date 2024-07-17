package no.uio.ifi.in2000.team30.sunflower.model.metalert

/**
 * data class, representing the weather alerts resources containing
 * information about the the description, type and uri of the resource.
 * current json hierarchy: "features" -> "properties" -> "resources" -> here
 */

// kenny: tror egentlig vi ikke trenger dette, men inkluderer det uansett
data class Resource(
    val description: String, // usually a CAP file
    val mimeType: String,
    val uri: String
)