package no.uio.ifi.in2000.team30.sunflower.ui.home

import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team30.sunflower.BuildConfig
import no.uio.ifi.in2000.team30.sunflower.data.googletimezone.GoogleTimeZoneRepository
import no.uio.ifi.in2000.team30.sunflower.data.locationforecast.LocationForecastRepository
import no.uio.ifi.in2000.team30.sunflower.data.locationinfo.LocationInfoRepository
import no.uio.ifi.in2000.team30.sunflower.data.metalert.MetAlertRepository
import no.uio.ifi.in2000.team30.sunflower.model.googletimezone.GoogleTimeZone
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Geometry
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.LocationForecast
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Meta
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.MetaUnits
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Properties
import no.uio.ifi.in2000.team30.sunflower.model.metalert.Alert
import no.uio.ifi.in2000.team30.sunflower.model.metalert.AlertColor
import java.nio.channels.UnresolvedAddressException

/**
 * UI state for the alerts.
 * @param alerts A list of metalert objects
 */
data class AlertsUiState(
    val alerts: List<Alert> = mutableListOf()
)


/**
 * UI state for LocationForecast
 * @param locationForecast A single LocationForecast object
 */
data class LocationForecastUiState(
    val locationForecast: LocationForecast = LocationForecast(
        type = "",
        geometry = Geometry("", emptyList()),
        properties = Properties(Meta("", MetaUnits("", "", "", "", "", "", "")), emptyList())
    )
)

data class GoogleTimeZoneUiState(
    val timeZone: GoogleTimeZone = GoogleTimeZone(0, 0, "", "", "")
)

/**
 * UI state for LocationPermissionUiState.
 * @param permissionResult A boolean representing permission denied/approved
 */
data class LocationPermissionUiState(
    val permissionResult: Boolean = false
)

data class LocationInfoUiState(
    val areaName: String = "",
    val feelsLikeTemp: Double = 0.0,
    val currentLatFromGPS: Double = 0.0,
    val currentLonFromGPS: Double = 0.0
)

data class LocationCoordinatesState(
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

// default location: Oslo/Norway
data class DefaultCoordinatesState(
    val lat: Double = 59.8939152,
    val lon: Double = 10.702546
)

/**
 * View model for the home screen.
 * Extends the [ViewModel] class from the Android Architecture Components library.
 */
class HomeScreenViewModel: ViewModel(){
    // instances of relevant repositories
    private val metAlertRepository = MetAlertRepository()
    private val locationForecastRepository = LocationForecastRepository()
    private val googleTimeZoneRepository = GoogleTimeZoneRepository()
    private val locationInfoRepository = LocationInfoRepository()

    // states
    private val _currentAlertsUiState = MutableStateFlow(AlertsUiState())
    val currentAlertsUiState: StateFlow<AlertsUiState> = _currentAlertsUiState.asStateFlow()

    private val _locationForecastsUiState = MutableStateFlow(LocationForecastUiState())
    val locationForecastsUiState: StateFlow<LocationForecastUiState> = _locationForecastsUiState.asStateFlow()

    private val _locationPermissionUiState = MutableStateFlow(LocationPermissionUiState())
    val locationPermissionUiState: StateFlow<LocationPermissionUiState> = _locationPermissionUiState.asStateFlow()

    private val _locationInfoUiState = MutableStateFlow(LocationInfoUiState())
    val locationInfoUiState: StateFlow<LocationInfoUiState> = _locationInfoUiState.asStateFlow()

    private val _locationCoordinates = MutableStateFlow(LocationCoordinatesState())
    val locationCoordinates: StateFlow<LocationCoordinatesState> = _locationCoordinates.asStateFlow()

    private val _currentTimeZone = MutableStateFlow(GoogleTimeZoneUiState())
    val currentTimeZone: StateFlow<GoogleTimeZoneUiState> = _currentTimeZone

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _defaultCoordinates = MutableStateFlow(DefaultCoordinatesState())
    val defaultCoordinates: StateFlow<DefaultCoordinatesState> = _defaultCoordinates.asStateFlow()

    private val _snackBarHostState = MutableStateFlow(SnackbarHostState())
    val snackBarHostState: StateFlow<SnackbarHostState> = _snackBarHostState.asStateFlow()

    private var timer: Job? = null
    init {
        handlePermissionResult(isPermissionGranted = false) // initial value of permission handling is false
    }

    private val _initialized = MutableStateFlow(false)
    val initialized: StateFlow<Boolean> = _initialized.asStateFlow()
    @MainThread
    fun initialize(context: Context) {
        if (!_initialized.value) {
            _initialized.value = true
            Log.d("Screen initialization", "Initializing HomeScreen from ViewModel")
            viewModelScope.launch {
                loadLatLonFromGPS(context).await()
                timerLoadEachTwoMinutes( // also calls on loadLocationForecastData()
                    _locationInfoUiState.value.currentLatFromGPS,
                    _locationInfoUiState.value.currentLonFromGPS,
                    context
                )
                loadMetAlertsData(
                    _locationInfoUiState.value.currentLatFromGPS,
                    _locationInfoUiState.value.currentLonFromGPS
                )
                loadTimeZone(
                    _locationInfoUiState.value.currentLatFromGPS,
                    _locationInfoUiState.value.currentLonFromGPS
                )
                //delay(1500) // 1.5s delay - so elements on screen can load/update before
            }
        }
    }

    fun refresh(lat: Double, lon: Double, context: Context) {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                if (lat == 0.0 && lon == 0.0 && _locationPermissionUiState.value.permissionResult) {
                    loadLatLonFromGPS(context).await()
                }
                if (lat != 0.0 && lon != 0.0) {
                    timerLoadEachTwoMinutes(lat, lon, context) // also calls on loadLocationForecastData()
                    loadMetAlertsData(lat, lon)
                    loadTimeZone(lat, lon)
                }
            } catch (e: Exception) {
                handleError(e, "refreshing data from API")
                Log.e("HomeScreenViewModel refresh error", "Error refreshing viewmodel $e, ${e.message}")
            } finally {
                Log.d("Screen refresh", "HomeScreen refreshed.")
                _isRefreshing.value = false
            }
        }
    }

    private fun handleError(e: Exception, reason: String?) {
        when {
            e is UnresolvedAddressException -> _error.value = "Error $reason. Please connect to the internet and swipe down to refresh."
            e is ConnectTimeoutException -> _error.value = "Error $reason. Connection to server timed out."
            e.message == null -> _error.value = "Error $reason. Please try again."
            else -> _error.value = "Error $reason. Please try again later.\n\n${e.message}"
        }
    }

    private fun setLocationCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            _locationCoordinates.update { locationCoordinates ->
                locationCoordinates.copy(lat = lat, lon = lon)
            }
        }
    }

    /* load metalerts data from the repository and update UI state */
    /* @param permissionResult A boolean representing permission denied/approved */
    private fun loadMetAlertsData(lat: Double? = null, lon: Double? = null, alertColors: List<AlertColor>? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // update UI state with the received data
                _currentAlertsUiState.update { currentAlerts ->
                    val metAlertsList: List<Alert>?

                    // get new metalerts data from the repository
                    metAlertsList = metAlertRepository.getCurrentAlertsList(lat, lon, alertColors)

                    // copy the currentAlerts state, replacing the 'alerts' list (containing Alert objects) with new data
                    currentAlerts.copy(alerts = metAlertsList)
                }
            } catch (e: Exception) {
                // update error state
                handleError(e, "getting weather warning alerts data from API")
                Log.e("HomeScreenViewModel load metalert data error", "Error loading data from MetAlert API: $e, ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /* load locationforecast data from the repository and update UI state */
    private fun loadLocationForecastData(lat: Double, lon: Double, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // update UI state with the received data
                _locationForecastsUiState.update { currentLocationData ->

                    // get new data from the repository
                    val locationForecastData = locationForecastRepository.getLocationForecast(lat, lon)
                    loadLocationInfo(locationForecastData, context)

                    // copy the state, replacing the ui state with new data
                    currentLocationData.copy(locationForecast = locationForecastData)
                }
            } catch (e: Exception) {
                // update error state
                handleError(e, "getting forecast data from API")
                Log.e("HomeScreenViewModel load location forecast error", "Error loading forecast from location (LocationForecast API): $e, ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadTimeZone(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // update UI state with the received data
                _currentTimeZone.update { currentTimeZone ->
                    val timezone: GoogleTimeZone = googleTimeZoneRepository.getGoogleTimeZone(BuildConfig.MAPS_API_KEY, lat, lon, System.currentTimeMillis())
                    currentTimeZone.copy(timeZone = timezone)
                }
            } catch (e: Exception) {
                // update error state
                handleError(e, "getting current timezone data from API")
                Log.e("HomeScreenViewModel load time zone error", "Error loading time zone (GoogleTimeZone API): $e, ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadLocationInfo(locationForecast: LocationForecast, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _locationInfoUiState.update { locationInfoUiState ->
                    val areaName: String = locationInfoRepository.getCurrentAreaName(_locationCoordinates.value.lat, _locationCoordinates.value.lon, context)
                    val feelsLikeTemp: Double = locationInfoRepository.getFeelsLikeTemp(locationForecast)
                    locationInfoUiState.copy(areaName = areaName, feelsLikeTemp = feelsLikeTemp)
                }
            } catch (e: Exception) {
                // update error state
                handleError(e, "getting current location info data")
                Log.e("HomeScreenViewModel get location info", "Error getting location info (areaName & feelsLikeTemp): $e, ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadLatLonFromGPS(context: Context) : Deferred<Unit> {
        return viewModelScope.async {
            _isLoading.value = true
            _error.value = null
            try {
                _locationInfoUiState.update { locationInfoUiState ->
                    val gps = locationInfoRepository.getLatLonFromGPS(LocationServices.getFusedLocationProviderClient(context))
                    locationInfoUiState.copy(currentLatFromGPS = gps[0], currentLonFromGPS = gps[1])
                }
            } catch (e: Exception) {
                // update error state
                handleError(e, "getting coordinates from GPS")
                Log.e("HomeScreenViewModel get coordinates from GPS error", "Error getting coordinates from GPS: $e, ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /* function to load and handle the permission result */
    fun handlePermissionResult(isPermissionGranted: Boolean) {
        // Update UI state based on permission result (false/true)
        _locationPermissionUiState.value =
            LocationPermissionUiState(permissionResult = isPermissionGranted)

        // Logging
        if (isPermissionGranted) {
            Log.d("LocationPermission", "Location permission granted")
        } else {
            Log.d("LocationPermission", "Location permission denied")
        }
    }

    private fun timerLoadEachTwoMinutes(lat: Double, lon: Double, context: Context) {
        // cancel previous job timer if it exists
        timer?.cancel()
        timer = viewModelScope.launch {
            while (true) {
                setLocationCoordinates(lat, lon)
                loadLocationForecastData(lat, lon, context)
                delay(120000) // Delay for 2 minute
            }
        }
    }

    fun updateAreaName(name: String) {
        viewModelScope.launch {
            _locationInfoUiState.update { locationInfoUiState ->
                locationInfoUiState.copy(areaName = name)
            }
        }
    }
}
