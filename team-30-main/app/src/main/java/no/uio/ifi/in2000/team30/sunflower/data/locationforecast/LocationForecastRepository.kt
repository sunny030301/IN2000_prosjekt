package no.uio.ifi.in2000.team30.sunflower.data.locationforecast

import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.LocationForecast

/**
 * repository, fetches location forecast from the API.
 * @param locationForecastDataSource - data source to use for fetching location forecast.
 */
class LocationForecastRepository(private val locationForecastDataSource: LocationForecastDataSource = LocationForecastDataSource()) {
    /**
     * calls the data source method to fetch weather info
     * @return a LocationForecast object
     */
    suspend fun getLocationForecast(lat: Double, lon: Double): LocationForecast {
        return locationForecastDataSource.fetchWeatherInfo(lat, lon)
    }
}