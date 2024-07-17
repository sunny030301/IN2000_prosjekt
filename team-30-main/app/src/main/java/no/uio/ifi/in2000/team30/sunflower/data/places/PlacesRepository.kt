package no.uio.ifi.in2000.team30.sunflower.data.places

import no.uio.ifi.in2000.team30.sunflower.model.places.autocomplete.Place
import no.uio.ifi.in2000.team30.sunflower.model.places.placedetails.PlaceDetails

class PlacesRepository(private val placesDataSource: PlacesDataSource = PlacesDataSource()) {
    suspend fun getPlaces(key: String, input: String): List<Place> {
        return placesDataSource.fetchPlaces(key, input)
    }

    suspend fun getPlaceDetails(key: String, input: String): PlaceDetails {
        return placesDataSource.fetchPlaceDetails(key, input)
    }
}