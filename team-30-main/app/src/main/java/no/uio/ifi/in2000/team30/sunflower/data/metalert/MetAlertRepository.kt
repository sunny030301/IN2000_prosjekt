package no.uio.ifi.in2000.team30.sunflower.data.metalert

import android.util.Log
import no.uio.ifi.in2000.team30.sunflower.model.metalert.Alert
import no.uio.ifi.in2000.team30.sunflower.model.metalert.AlertColor
import no.uio.ifi.in2000.team30.sunflower.model.metalert.MetAlert

/**
 * repository, fetches location forecast from the API.
 * @param metAlertDataSource - data source to use for fetching weather alerts.
 */
class MetAlertRepository(private val metAlertDataSource: MetAlertDataSource = MetAlertDataSource()) {
    /**
     * calls the data source method to fetch weather alerts
     * @return a MetAlert object
     */
    private suspend fun getCurrentWeatherAlerts(): MetAlert {
        return metAlertDataSource.fetchCurrentWeatherAlerts()
    }

    /**
     * calls the data source method to fetch weather alerts in selected latitude and longitude
     * @return a MetAlert object
     */
    private suspend fun getCurrentWeatherAlertsInArea(lat: Double, lon: Double): MetAlert {
        return metAlertDataSource.fetchCurrentWeatherAlerts(lat, lon)
    }

    /**
     * calls the data source method to fetch only selected colored weather alerts
     * @return a MutableList of Alert objects (each element in list = Alert of selected color)
     */
    suspend fun getCurrentAlertsList(lat: Double? = null, lon: Double? = null, alertColorsList: List<AlertColor>? = null) : List<Alert> {
        val currentAlerts: MetAlert = when {
            lat != null && lon != null -> getCurrentWeatherAlertsInArea(lat, lon)
            else -> getCurrentWeatherAlerts()
        }

        // capitalize first letter of AlertColor
        // (for comparing AlertColor to riskMatrixColor from MetAlertAPI)
        val capitalizedAlertColorsList: List<String>? = alertColorsList?.map { alertColor ->
            alertColor.toString().lowercase().replaceFirstChar { it.uppercase() }
        }

        // filter alerts
        val filteredAlerts = if (capitalizedAlertColorsList.isNullOrEmpty()) currentAlerts.features
        else currentAlerts.features.filter { alert ->
            capitalizedAlertColorsList.contains(alert.properties.riskMatrixColor)
        }

        // log number and color of alerts retrieved
        val colorsDescription = if (capitalizedAlertColorsList.isNullOrEmpty()) "all AlertColors"
        else capitalizedAlertColorsList.toString()
        Log.d("CURRENT_WEATHER_ALERTS", "Total alerts of color(s) '$colorsDescription' retrieved = ${filteredAlerts.size}")

        return filteredAlerts
    }
}