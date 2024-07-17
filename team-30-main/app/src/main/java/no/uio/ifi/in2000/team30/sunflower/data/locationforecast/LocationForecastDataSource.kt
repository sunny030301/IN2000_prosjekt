package no.uio.ifi.in2000.team30.sunflower.data.locationforecast

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.LocationForecast

/**
 * data source class for fetching Location Forecast from the API
 */
class LocationForecastDataSource (
    // HTTP-client used to make requests
    private val client: HttpClient = HttpClient{
        install(ContentNegotiation) {
            gson()
        }
        defaultRequest {
            url("https://gw-uio.intark.uh-it.no/in2000/")
            header("X-Gravitee-API-Key","8414c56d-01e3-447f-8736-563dc1cb141e")
        }
    }
) {
    /**
     * fetches weather info from the API
     * @return A LocationForecast object
     */
    suspend fun fetchWeatherInfo(lat: Double, lon: Double): LocationForecast {
        // API-endpoint, JSON-file with LocationForecast data
        val path =
            "weatherapi/locationforecast/2.0/compact?lat=${lat}&lon=${lon}" // gets gps-location from `lat` and `lon`

        // fetch location forecast from the API
        val response: HttpResponse =
            client.get(path) {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, "in2000-v24, team-30")
                }
            }
        Log.d("API call", "fetchWeatherInfo() in LocationForecastDataSource")
        if (response.status != HttpStatusCode.OK) throw Exception("Failed to fetch weather data: ${response.status.description}")

        // deserialize (through .body()) and return the response to a LocationForecast object
        return response.body<LocationForecast>()
    }
}