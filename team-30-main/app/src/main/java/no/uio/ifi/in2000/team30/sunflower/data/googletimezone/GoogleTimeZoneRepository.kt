package no.uio.ifi.in2000.team30.sunflower.data.googletimezone

import no.uio.ifi.in2000.team30.sunflower.model.googletimezone.GoogleTimeZone

class GoogleTimeZoneRepository(private val googleTimeZoneDataSource: GoogleTimeZoneDataSource = GoogleTimeZoneDataSource()) {
    suspend fun getGoogleTimeZone(key: String, lat: Double, lon: Double, timestamp: Long): GoogleTimeZone {
        return googleTimeZoneDataSource.fetchTimeZone(key, lat, lon, timestamp)
    }
}