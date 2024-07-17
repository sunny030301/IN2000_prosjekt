package no.uio.ifi.in2000.team30.sunflower.domain

import no.uio.ifi.in2000.team30.sunflower.BuildConfig
import no.uio.ifi.in2000.team30.sunflower.data.googletimezone.GoogleTimeZoneRepository
import no.uio.ifi.in2000.team30.sunflower.data.locationforecast.LocationForecastRepository
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Timeseries
import no.uio.ifi.in2000.team30.sunflower.utils.DateDisplayUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GroupTimeListByDateUseCase {
    private val locationForecastRepository = LocationForecastRepository()
    private val googleTimeZoneRepository = GoogleTimeZoneRepository()

    private suspend fun getTimeZoneId(lat: Double, lon: Double): String {
        return googleTimeZoneRepository.getGoogleTimeZone(BuildConfig.MAPS_API_KEY, lat, lon, System.currentTimeMillis()).timeZoneId
    }

    private suspend fun getLocationForecastTimeseries(lat: Double, lon: Double) : List<Timeseries> {
        return locationForecastRepository.getLocationForecast(lat, lon).properties.timeseries
    }

    suspend fun getTimeListData(lat: Double, lon: Double) : Pair<List<Timeseries>, String>{
        val timezoneId: String = getTimeZoneId(lat, lon)
        val locationForecastTemp: List<Timeseries> = getLocationForecastTimeseries(lat, lon)

        return Pair(locationForecastTemp, timezoneId)
    }

    fun groupTimeListByDate(locationForecastTemp: List<Timeseries>, timezoneId: String) : Map<String, List<Timeseries>> {
        val todayAndTomorrowPair: Pair<Date, Date> = DateDisplayUtils.getDatesTodayTomorrowFromCalendar()

        // formats today and tomorrows date to "EEE, d MMM"
        val dateFormat = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
        val todayDateStr: String = dateFormat.format(todayAndTomorrowPair.first)
        val tomorrowDateStr: String = dateFormat.format(todayAndTomorrowPair.second)

        val groupedByDate = locationForecastTemp.groupBy { timeseries ->
            val dateStr = DateDisplayUtils.parseAndFormatDate(timeseries.time, "UTC", "yyyy-MM-dd'T'HH:mm:ssXXX", timezoneId, "EEE, d MMM")
            val relativeDateStr = DateDisplayUtils.getRelativeDateString(dateStr, todayDateStr, tomorrowDateStr)
            when (relativeDateStr) {
                "today" -> "${relativeDateStr.replaceFirstChar { it.uppercase() }}, ${dateStr.substring(5)}"
                "tomorrow" -> dateStr
                else -> dateStr
            }
        }
        return groupedByDate
    }
}
