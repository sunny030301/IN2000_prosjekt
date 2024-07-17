package no.uio.ifi.in2000.team30.sunflower.ui.clock

import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team30.sunflower.data.locationinfo.LocationInfoRepository
import no.uio.ifi.in2000.team30.sunflower.domain.GroupTimeListByDateUseCase
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Timeseries
import java.nio.channels.UnresolvedAddressException

data class TimeList(
    val timeList: List<Timeseries> = emptyList(),
)

data class TimeListGrouped(
    val groupedTimeList: Map<String, List<Timeseries>> = emptyMap(),
)

data class LocationInfoUiState(
    val areaName: String = "",
)

data class LocationCoordinatesState(
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

class ClockScreenViewModel : ViewModel() {
    // repository and domain
    private val groupTimeListByDateUseCase = GroupTimeListByDateUseCase()
    private val locationInfoRepository = LocationInfoRepository()

    // states
    private val _isLoading = MutableStateFlow(true)    // private mutable state flow to represent UI state
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()      // public immutable state flow to expose UI state to screen

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _locationInfoUiState = MutableStateFlow(LocationInfoUiState())
    val locationInfoUiState: StateFlow<LocationInfoUiState> = _locationInfoUiState.asStateFlow()

    private val _locationCoordinates = MutableStateFlow(LocationCoordinatesState())
    val locationCoordinates: StateFlow<LocationCoordinatesState> = _locationCoordinates.asStateFlow()

    private val _showScrollToTopButton = MutableStateFlow(false)
    val showScrollToTopButton: StateFlow<Boolean> = _showScrollToTopButton.asStateFlow()

    private val _timeListDataState = MutableStateFlow(TimeList())
    val timeListDataState: StateFlow<TimeList> = _timeListDataState.asStateFlow()

    private val _timeZoneId = MutableStateFlow("")
    val timeZoneId: StateFlow<String> = _timeZoneId.asStateFlow()

    private val _timeListGrouped = MutableStateFlow(TimeListGrouped())
    val timeListGrouped: StateFlow<TimeListGrouped> = _timeListGrouped.asStateFlow()

    private val _snackBarHostState = MutableStateFlow(SnackbarHostState())
    val snackBarHostState: StateFlow<SnackbarHostState> = _snackBarHostState.asStateFlow()

    private val _initializeCalled = MutableStateFlow(false)
    val initializeCalled: StateFlow<Boolean> = _initializeCalled.asStateFlow()
    @MainThread
    fun initialize(lat: Double, lon: Double, context: Context) {
        if (!_initializeCalled.value) {
            _initializeCalled.value = true
            Log.d("Screen initialization", "Initializing ClockScreen from ViewModel")
            viewModelScope.launch {
                setLocationCoordinates(lat, lon)
                loadForecastTimeListData(lat, lon)
                loadLocationInfo(context)
            }
        }
    }

    fun refresh(lat: Double, lon: Double, context: Context) {
        viewModelScope.launch {
            _isRefreshing.value = true
            Log.d("Screen refresh", "Attempting ClockScreen refresh...")
            try {
                setLocationCoordinates(lat, lon)
                loadForecastTimeListData(lat, lon)
                loadLocationInfo(context)
            } catch (e: Exception) {
                handleError(e, "refreshing data from API")
                Log.e("ClockScreenViewModel refresh error", "Error refreshing viewmodel $e, ${e.message}")
            } finally {
                Log.d("Screen refresh", "ClockScreen refreshed.")
                _isRefreshing.value = false
            }
        }
    }

    private fun handleError(e: Exception, reason: String?) {
        when {
            e is UnresolvedAddressException -> _error.value = "Error $reason. Please connect to the internet and swipe down to refresh."
            e is ConnectTimeoutException -> _error.value = "Error $reason. Connection to server timed out."
            e.message == null -> _error.value = "Error $reason. Please try again."
            else -> _error.value = "Error $reason. Please try again later.\n${e.message}"
        }
    }

    /* load timezone and LocationForecast data from the repositories and update timeListGrouped UI state */
    private fun loadForecastTimeListData(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val timeListData = groupTimeListByDateUseCase.getTimeListData(lat, lon)
                val groupedByDate = groupTimeListByDateUseCase.groupTimeListByDate(timeListData.first, timeListData.second)
                _timeListDataState.value = TimeList(timeListData.first)
                _timeZoneId.value = timeListData.second
                _timeListGrouped.value = TimeListGrouped(groupedByDate)
            } catch (e: Exception) {
                handleError(e, "getting forecast time list")
                Log.e("ClockScreenViewModel load forecast time list error", "Error loading forecast time list $e, ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadLocationInfo(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _locationInfoUiState.update { locationInfoUiState ->
                    val areaName: String = locationInfoRepository.getCurrentAreaName(_locationCoordinates.value.lat, _locationCoordinates.value.lon, context)
                    locationInfoUiState.copy(areaName = areaName)
                }
            } catch (e: Exception) {
                // update error state
                handleError(e, "getting current area name")
                Log.e("ClockScreenViewModel get current area name error", "Error getting current area name): $e, ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun setLocationCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            _locationCoordinates.update { locationCoordinates ->
                locationCoordinates.copy(lat = lat, lon = lon)
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

    fun updateScrollToTopButtonVisibility(firstVisibleItemIndex: Int) {
        viewModelScope.launch {
            _showScrollToTopButton.value = firstVisibleItemIndex > 0
        }
    }
}