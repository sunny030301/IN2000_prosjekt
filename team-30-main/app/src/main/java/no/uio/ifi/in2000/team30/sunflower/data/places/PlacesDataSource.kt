package no.uio.ifi.in2000.team30.sunflower.data.places

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team30.sunflower.model.places.autocomplete.Place
import no.uio.ifi.in2000.team30.sunflower.model.places.autocomplete.Places
import no.uio.ifi.in2000.team30.sunflower.model.places.placedetails.PlaceDetails

class PlacesDataSource(
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
    suspend fun fetchPlaces(key: String, input: String): List<Place> {
        val response = client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = "maps.googleapis.com"
                path("maps/api/place/autocomplete/json")

                parameters.append("key", key)
                parameters.append("locationbias", "ipbias")
                parameters.append("types", "street_address|point_of_interest|sublocality|locality|country") // Place types: https://developers.google.com/maps/documentation/places/web-service/supported_types
                parameters.append("input", input)
            }
        }
        Log.d("API call","fetchPlaces() in PlacesDataSource")

        val places: Places = response.body()
        return places.toPlacesList()
    }

    suspend fun fetchPlaceDetails(key: String, placeId: String): PlaceDetails {
        val response = client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = "maps.googleapis.com"
                path("maps/api/place/details/json")

                parameters.append("key", key)
                parameters.append("place_id", placeId)
            }
        }
        Log.d("API call", "fetchPlaceDetails() in PlacesDataSource")
        if (response.status != HttpStatusCode.OK) throw Exception("Failed to fetch places data: ${response.status.description}")

        return response.body<PlaceDetails>()
    }
}