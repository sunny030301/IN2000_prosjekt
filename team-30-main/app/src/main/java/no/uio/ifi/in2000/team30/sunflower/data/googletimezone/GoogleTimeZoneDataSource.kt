package no.uio.ifi.in2000.team30.sunflower.data.googletimezone

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team30.sunflower.model.googletimezone.GoogleTimeZone

class GoogleTimeZoneDataSource(
    // HTTP-client used to make requests
    private val client: HttpClient =
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
                })
            }
        }
) {
    suspend fun fetchTimeZone(
        key: String,
        lat: Double,
        lon: Double,
        timestamp: Long
    ): GoogleTimeZone {
        val response = client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = "maps.googleapis.com"
                path("maps/api/timezone/json")

                parameters.append("key", key)
                parameters.append("location", "$lat,$lon")
                parameters.append("timestamp", "${timestamp.takeLowestOneBit()}")
            }
        }
        Log.d("API call", "fetchTimeZone() in GoogleTimeZoneDataSource")
        Log.d(
            "API response",
            "GoogleTimeZoneDataSource Response received: ${response.bodyAsText()}"
        )
        if (response.status != HttpStatusCode.OK) throw Exception("Failed to fetch timezone data: ${response.status.description}")

        return response.body<GoogleTimeZone>()
    }
}