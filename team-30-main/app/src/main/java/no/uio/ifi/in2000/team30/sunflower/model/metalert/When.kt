package no.uio.ifi.in2000.team30.sunflower.model.metalert

/**
 * data class, representing when the weather alert is effective containing
 * information about the interval.
 * current json hierarchy: "features" -> "when" -> here
 */
data class When(
    val interval: List<String>
)