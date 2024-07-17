package no.uio.ifi.in2000.team30.sunflower.ui.search

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team30.sunflower.BuildConfig
import no.uio.ifi.in2000.team30.sunflower.data.places.PlacesRepository
import no.uio.ifi.in2000.team30.sunflower.model.places.autocomplete.Place
import no.uio.ifi.in2000.team30.sunflower.model.places.placedetails.Geometry
import no.uio.ifi.in2000.team30.sunflower.model.places.placedetails.Location
import no.uio.ifi.in2000.team30.sunflower.model.places.placedetails.PlaceDetails
import no.uio.ifi.in2000.team30.sunflower.model.places.placedetails.Result
import java.nio.channels.UnresolvedAddressException

/* data class representing UI state of place details */
data class PlaceDetailsUiState(
    val placeDetails: PlaceDetails = PlaceDetails(Result(Geometry(Location(0.0, 0.0)), "", "", types = listOf("")))
)

/* data class representing UI state of the list of places */
data class PlaceListUiState(
    val placeList: List<Place> = listOf(Place("", ""))
)

class PlacesViewModel : ViewModel() {
    // initialize PlacesRepository for fetching data
    private val placesRepository = PlacesRepository()

    // mutable state for search input value
    private val _searchInputValue = MutableStateFlow("")
    var searchInputValue: StateFlow<String> = _searchInputValue.asStateFlow()

    // mutable state for places list based on search input value
    private val _placesList = MutableStateFlow(PlaceListUiState())
    val placesList: StateFlow<PlaceListUiState> = _placesList.asStateFlow()

    // mutable state for selected place details
    private val _selectedPlaceDetails = MutableStateFlow(PlaceDetailsUiState())
    var selectedPlaceDetails: StateFlow<PlaceDetailsUiState?> = _selectedPlaceDetails.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _snackBarHostState = MutableStateFlow(SnackbarHostState())
    val snackBarHostState: StateFlow<SnackbarHostState> = _snackBarHostState.asStateFlow()

    private var searchJob: Job? = null
    /* updates places list state based on search input value */
    fun updatePlacesList(value: String) {
        searchJob?.cancel()  // cancel previous job if still running
        searchJob = viewModelScope.launch {
            _error.value = null
            setSearchInputValue(value)
            delay(250)  // delay to wait before executing search
            try {
                _placesList.value = PlaceListUiState(
                    placesRepository.getPlaces(
                        key = BuildConfig.MAPS_API_KEY,
                        input = _searchInputValue.value
                    )
                )
            } catch (e: Exception) {
                handleError(e, "updated places list")
                Log.e("PlacesViewModel update places list error", "Error loading places list $e, ${e.message}")
            }
        }
    }

    /* set the search input value and update state */
    private fun setSearchInputValue(value: String) {
        viewModelScope.launch {
            _searchInputValue.value = value
        }
    }

    /* get place details and update state in selectedPlaceDetails */
    fun getDetailsOfSelectedPlace(place: Place) {
        viewModelScope.launch {
            try {
                _selectedPlaceDetails.update { currentPlaceDetails ->
                    val details = placesRepository.getPlaceDetails(BuildConfig.MAPS_API_KEY, place.id)
                    currentPlaceDetails.copy(placeDetails = details)
                }
            } catch (e: Exception) {
                handleError(e, "detailed information about selected place")
                Log.e("PlacesViewModel get details of selected place error", "Error loading details of selected place $e, ${e.message}")
            }
        }
    }

    private fun handleError(e: Exception, reason: String?) {
        when {
            e is UnresolvedAddressException -> _error.value = "Error getting $reason data. Please connect to the internet and try again."
            e.message == null -> _error.value = "Error getting $reason data. Please try again."
            else -> _error.value = "Error getting $reason data. Please try again later.\n${e.message}"
        }
    }

    fun resetStates() {
        _searchInputValue.value = ""
        _selectedPlaceDetails.value = PlaceDetailsUiState()
    }
}