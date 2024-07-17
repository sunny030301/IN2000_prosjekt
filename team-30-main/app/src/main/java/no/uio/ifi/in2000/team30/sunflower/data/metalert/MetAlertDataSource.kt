package no.uio.ifi.in2000.team30.sunflower.data.metalert
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
import no.uio.ifi.in2000.team30.sunflower.model.metalert.MetAlert

/**
 * data source class for fetching MetAlert - Weather alerts from the Norwegian Meteorological Institute API
 */
class MetAlertDataSource (
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

    // API-endpoint, JSON-file with current weather alerts data
    private var path: String =
        "weatherapi/metalerts/2.0/current.json"

    /**
     * fetches current weather alerts from the API (in selected latitude and longitude, if specified)
     * @return A MetAlert object
     */
    suspend fun fetchCurrentWeatherAlerts(lat: Double = 100.0, lon: Double = 100.0): MetAlert {
        // if lat or lon is specified
        if (lat != 100.0 || lon != 100.0)
            path =
                "weatherapi/metalerts/2.0/current.json?lat=${lat}&lon=${lon}&lang=en" // gets gps-location from `lat` and `lon`
        // fetch current weather alerts from the API
        val response: HttpResponse =
            client.get(path) {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, "in2000-v24, team-30")
                }
            }
        Log.d("API call", "fetchCurrentWeatherAlerts() in MetAlertDataSource")
        if (response.status != HttpStatusCode.OK) throw Exception("Failed to fetch weather alerts data: ${response.status.description}")

        // deserialize (through .body()) and return the response to a LocationForecast object
        return response.body<MetAlert>()
    }
}