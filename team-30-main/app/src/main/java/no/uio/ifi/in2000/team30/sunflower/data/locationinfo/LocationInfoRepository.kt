package no.uio.ifi.in2000.team30.sunflower.data.locationinfo

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.LocationForecast
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.pow

class LocationInfoRepository {
    /**
     * converts a location forecast object to feels like temperature.
     * @param locationForecast The location forecast data.
     * @return The feels like temperature as a Double.
     */
    fun getFeelsLikeTemp(locationForecast: LocationForecast): Double {
        var feelsLikeTemp = 0.0

        if (locationForecast.properties.timeseries.isNotEmpty()) {
            val airTemperature: Double = locationForecast.properties.timeseries[0].data.instant.details.air_temperature
            val windSpeed: Double = locationForecast.properties.timeseries[0].data.instant.details.wind_speed * 3.6 // Convert wind speed from m/s to km/h
            val humidity: Double = locationForecast.properties.timeseries[0].data.instant.details.relative_humidity

            //Calculate wind chill
            //Wind Chill is a term used to describe what the air temperature feels like to the human skin due to the combination
            //of cold temperatures and winds blowing on exposed skin
            val windChill = if (airTemperature < 10.0 && windSpeed >= 4.8) {
                13.12 + (0.6215 * airTemperature) - (11.37 * (windSpeed.pow(0.16))) + (0.3965 * airTemperature * (windSpeed.pow(
                    0.16
                )))
            } else {
                airTemperature //If wind chill conditions are not met, feels like temperature is the same as air temperature
            }

            //Calculate heat index
            val heatIndex = if (airTemperature > 27.0 && humidity > 40.0) {
                -8.78469475556 + (1.61139411 * airTemperature) + (2.33854883889 * humidity) - (0.14611605 * airTemperature * humidity) - (0.012308094 * (airTemperature * airTemperature)) - (0.0164248277778 * (humidity * humidity)) + (0.002211732 * (airTemperature * airTemperature) * humidity) + (0.00072546 * airTemperature * (humidity * humidity)) - (0.000003582 * (airTemperature * airTemperature) * (humidity * humidity))
            } else {
                airTemperature //If heat index conditions are not met, feels like temperature is the same as air temperature
            }

            //Determine feels like temperature based on conditions
            feelsLikeTemp = when {
                windChill < airTemperature -> windChill
                heatIndex > airTemperature -> heatIndex
                else -> airTemperature
            }
        }
        return feelsLikeTemp
    }

    /**
     * gets current area name based on GPS location.
     * @param locationLat The latitude of the location.
     * @param locationLon The longitude of the location.
     * @param context The application context.
     * @return The current area name as a String.
     */
    @Suppress("DEPRECATION") // getFromLocation deprecated in Java
    fun getCurrentAreaName(locationLat: Double, locationLon: Double, context: Context) : String {
        // fetches areaName by current gps-location (lat, lon)
        var areaName = ""
        val geocoder = Geocoder(context)
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(locationLat, locationLon, 1)
            if (!addresses.isNullOrEmpty()) {
                val area: String = if (addresses[0].subLocality != null) addresses[0].subLocality
                else if (addresses[0].locality != null) addresses[0].locality
                else if (addresses[0].subAdminArea != null) addresses[0].subAdminArea
                else if (addresses[0].thoroughfare != null) addresses[0].thoroughfare
                else addresses[0].countryName

                areaName = if (addresses[0].adminArea != null) "$area, ${addresses[0].adminArea}"
                else area

                Log.d("Address", "Area Name: $areaName")
            } else {
                Log.d("Address", "No address found for the given coordinates.")
            }
        } catch (e: Exception) {
            areaName = "Error getting current area name: $e"
            Log.d("Address", "Geocoder service is not available.")
        }
        return areaName
    }

    /**
     * retrieves the current latitude and longitude.
     * @param locationClient The FusedLocationProviderClient instance.
     * @return A list of Doubles containing latitude and longitude.
     */
    @SuppressLint("MissingPermission") // permission is already handled before this method gets called
    suspend fun getLatLonFromGPS(locationClient: FusedLocationProviderClient): List<Double> {
        val locationInfo: String
        val latLonList: MutableList<Double> = mutableListOf()
        val location = locationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, // there are two types of getCurrentLocation priority: Priority.PRIORITY_HIGH_ACCURACY || Priority.PRIORITY_BALANCED_POWER_ACCURACY
            CancellationTokenSource().token
        ).await()
        if (location == null) {
            locationInfo = "No last known location. Try fetching the current location first"
        } else {
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val currentTime = timeFormat.format(System.currentTimeMillis())
            locationInfo = "Current location is \n" +
                    "lat: ${location.latitude}\n" +
                    "long: ${location.longitude}\n" +
                    "fetched at: $currentTime"
            latLonList.add(location.latitude)
            latLonList.add(location.longitude)
        }

        Log.d("getLocationInfo", locationInfo)
        return latLonList
    }
}