package no.uio.ifi.in2000.team30.sunflower.model.googletimezone

import kotlinx.serialization.Serializable

@Serializable
data class GoogleTimeZone(
    val dstOffset: Int,
    val rawOffset: Int,
    val status: String,
    val timeZoneId: String,
    val timeZoneName: String
)