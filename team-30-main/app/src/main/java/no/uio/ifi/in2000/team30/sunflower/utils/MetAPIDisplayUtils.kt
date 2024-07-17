package no.uio.ifi.in2000.team30.sunflower.utils

import no.uio.ifi.in2000.team30.sunflower.R

class MetAPIDisplayUtils {
    companion object {
        /* returns corresponding weather icon from symbolCode (icons from metno/weathericons https://github.com/metno/weathericons/tree/main/weather) */
        fun getWeatherIcon(symbolCode: String) : Int {
            val weatherIconMap = mapOf(
                "cloudy" to R.drawable.cloudy,
                "fog" to R.drawable.fog,
                "heavyrain" to R.drawable.heavyrain,
                "heavyrainandthunder" to R.drawable.heavyrainandthunder,
                "heavysleet" to R.drawable.heavysleet,
                "heavysleetandthunder" to R.drawable.heavysleetandthunder,
                "heavysnow" to R.drawable.heavysnow,
                "heavysnowandthunder" to R.drawable.heavysnowandthunder,
                "lightrain" to R.drawable.lightrain,
                "lightrainandthunder" to R.drawable.lightrainandthunder,
                "lightsleet" to R.drawable.lightsleet,
                "lightsleetandthunder" to R.drawable.lightsleetandthunder,
                "lightsnow" to R.drawable.lightsnow,
                "lightsnowandthunder" to R.drawable.lightsnowandthunder,
                "rain" to R.drawable.rain,
                "rainandthunder" to R.drawable.rainandthunder,
                "sleet" to R.drawable.sleet,
                "sleetandthunder" to R.drawable.sleetandthunder,
                "snow" to R.drawable.snow,
                "snowandthunder" to R.drawable.snowandthunder,
                "clearsky_day" to R.drawable.clearsky_day,
                "fair_day" to R.drawable.fair_day,
                "heavyrainshowers_day" to R.drawable.heavyrainshowers_day,
                "heavyrainshowersandthunder_day" to R.drawable.heavyrainshowersandthunder_day,
                "heavysleetshowers_day" to R.drawable.heavysleetshowers_day,
                "heavysleetshowersandthunder_day" to R.drawable.heavysleetshowersandthunder_day,
                "heavysnowshowers_day" to R.drawable.heavysnowshowers_day,
                "heavysnowshowersandthunder_day" to R.drawable.heavysnowshowersandthunder_day,
                "lightrainshowers_day" to R.drawable.lightrainshowers_day,
                "lightrainshowersandthunder_day" to R.drawable.lightrainshowersandthunder_day,
                "lightsleetshowers_day" to R.drawable.lightsleetshowers_day,
                "lightsnowshowers_day" to R.drawable.lightsnowshowers_day,
                "lightssleetshowersandthunder_day" to R.drawable.lightssleetshowersandthunder_day,
                "lightssnowshowersandthunder_day" to R.drawable.lightssnowshowersandthunder_day,
                "partlycloudy_day" to R.drawable.partlycloudy_day,
                "rainshowers_day" to R.drawable.rainshowers_day,
                "rainshowersandthunder_day" to R.drawable.rainshowersandthunder_day,
                "sleetshowers_day" to R.drawable.sleetshowers_day,
                "sleetshowersandthunder_day" to R.drawable.sleetshowersandthunder_day,
                "snowshowers_day" to R.drawable.snowshowers_day,
                "snowshowersandthunder_day" to R.drawable.snowshowersandthunder_day,
                "clearsky_night" to R.drawable.clearsky_night,
                "fair_night" to R.drawable.fair_night,
                "heavyrainshowers_night" to R.drawable.heavyrainshowers_night,
                "heavyrainshowersandthunder_night" to R.drawable.heavyrainshowersandthunder_night,
                "heavysleetshowers_night" to R.drawable.heavysleetshowers_night,
                "heavysleetshowersandthunder_night" to R.drawable.heavysleetshowersandthunder_night,
                "heavysnowshowers_night" to R.drawable.heavysnowshowers_night,
                "heavysnowshowersandthunder_night" to R.drawable.heavysnowshowersandthunder_night,
                "lightrainshowers_night" to R.drawable.lightrainshowers_night,
                "lightrainshowersandthunder_night" to R.drawable.lightrainshowersandthunder_night,
                "lightsleetshowers_night" to R.drawable.lightsleetshowers_night,
                "lightsnowshowers_night" to R.drawable.lightsnowshowers_night,
                "lightssleetshowersandthunder_night" to R.drawable.lightssleetshowersandthunder_night,
                "lightssnowshowersandthunder_night" to R.drawable.lightssnowshowersandthunder_night,
                "partlycloudy_night" to R.drawable.partlycloudy_night,
                "rainshowers_night" to R.drawable.rainshowers_night,
                "rainshowersandthunder_night" to R.drawable.rainshowersandthunder_night,
                "sleetshowers_night" to R.drawable.sleetshowers_night,
                "sleetshowersandthunder_night" to R.drawable.sleetshowersandthunder_night,
                "snowshowers_night" to R.drawable.snowshowers_night,
                "snowshowersandthunder_night" to R.drawable.snowshowersandthunder_night
            )
            return weatherIconMap[symbolCode] ?: R.drawable.solsikke_fjes
        }

        /* returns readable text format of symbol code */
        fun symbolCodeToText(symbolCode: String) : String {
            val symbolCodeToTextMap = mapOf(
                "clearsky" to "Clear sky",
                "fair" to "Fair",
                "partlycloudy" to "Partly cloudy",
                "cloudy" to "Cloudy",
                "lightrainshowers" to "Light rain showers",
                "rainshowers" to "Rain showers",
                "heavyrainshowers" to "Heavy rain showers",
                "lightrainshowersandthunder" to "Light rain showers and thunder",
                "rainshowersandthunder" to "Rain showers and thunder",
                "heavyrainshowersandthunder" to "Heavy rain showers and thunder",
                "lightsleetshowers" to "Light sleet showers",
                "sleetshowers" to "Sleet showers",
                "heavysleetshowers" to "Heavy sleet showers",
                "lightssleetshowersandthunder" to "Light sleet showers and thunder",
                "sleetshowersandthunder" to "Sleet showers and thunder",
                "heavysleetshowersandthunder" to "Heavy sleet showers and thunder",
                "lightsnowshowers" to "Light snow showers",
                "snowshowers" to "Snow showers",
                "heavysnowshowers" to "Heavy snow showers",
                "lightssnowshowersandthunder" to "Light snow showers and thunder",
                "snowshowersandthunder"	to "Snow showers and thunder",
                "heavysnowshowersandthunder" to "Heavy snow showers and thunder",
                "lightrain" to "Light rain",
                "rain" to "Rain",
                "heavyrain" to "Heavy rain",
                "lightrainandthunder" to "Light rain and thunder",
                "rainandthunder" to "Rain and thunder",
                "heavyrainandthunder" to "Heavy rain and thunder",
                "lightsleet" to "Light sleet",
                "sleet"	to "Sleet",
                "heavysleet" to "Heavy sleet",
                "lightsleetandthunder" to "Light sleet and thunder",
                "sleetandthunder" to "Sleet and thunder",
                "heavysleetandthunder" to "Heavy sleet and thunder",
                "lightsnow" to "Light snow",
                "snow" to "Snow",
                "heavysnow" to "Heavy snow",
                "lightsnowandthunder" to "Light snow and thunder",
                "snowandthunder" to "Snow and thunder",
                "heavysnowandthunder" to "Heavy snow and thunder",
                "fog" to "Fog"
            )
            if (symbolCode.isNotBlank()) {
                val text = symbolCodeToTextMap.keys.filter { key ->
                    symbolCode.startsWith(key)
                }
                if (text.isNotEmpty()) return symbolCodeToTextMap[text[0]]!!
            }
            return "Unknown weather status"
        }

        /* returns corresponding weather warning alert icon from event name and event color (icons from nrkno/yr-warning-icons https://nrkno.github.io/yr-warning-icons/) */
        fun getDangerAlertIcon(eventName: String, eventColor: String) : Int? {
            val warningIconsMap = mapOf(
                "avalanches" to mapOf(
                    "orange" to R.drawable.icon_warning_avalanches_orange,
                    "red" to R.drawable.icon_warning_avalanches_red,
                    "yellow" to R.drawable.icon_warning_avalanches_yellow
                ),
                "drivingconditions" to mapOf(
                    "orange" to R.drawable.icon_warning_drivingconditions_orange,
                    "red" to R.drawable.icon_warning_drivingconditions_red,
                    "yellow" to R.drawable.icon_warning_drivingconditions_yellow
                ),
                "extreme" to mapOf(
                    "generic" to R.drawable.icon_warning_extreme
                ),
                "flood" to mapOf(
                    "orange" to R.drawable.icon_warning_flood_orange,
                    "red" to R.drawable.icon_warning_flood_red,
                    "yellow" to R.drawable.icon_warning_flood_yellow
                ),
                "forestfire" to mapOf(
                    "orange" to R.drawable.icon_warning_forestfire_orange,
                    "red" to R.drawable.icon_warning_forestfire_red,
                    "yellow" to R.drawable.icon_warning_forestfire_yellow
                ),
                "generic" to mapOf(
                    "orange" to R.drawable.icon_warning_generic_orange,
                    "red" to R.drawable.icon_warning_generic_red,
                    "yellow" to R.drawable.icon_warning_generic_yellow
                ),
                "ice" to mapOf(
                    "orange" to R.drawable.icon_warning_ice_orange,
                    "red" to R.drawable.icon_warning_ice_red,
                    "yellow" to R.drawable.icon_warning_ice_yellow
                ),
                "landslide" to mapOf(
                    "orange" to R.drawable.icon_warning_landslide_orange,
                    "red" to R.drawable.icon_warning_landslide_red,
                    "yellow" to R.drawable.icon_warning_landslide_yellow
                ),
                "lightning" to mapOf(
                    "orange" to R.drawable.icon_warning_lightning_orange,
                    "red" to R.drawable.icon_warning_lightning_red,
                    "yellow" to R.drawable.icon_warning_lightning_yellow
                ),
                "polarlow" to mapOf(
                    "orange" to R.drawable.icon_warning_polarlow_orange,
                    "red" to R.drawable.icon_warning_polarlow_red,
                    "yellow" to R.drawable.icon_warning_polarlow_yellow
                ),
                "rain" to mapOf(
                    "orange" to R.drawable.icon_warning_rain_orange,
                    "red" to R.drawable.icon_warning_rain_red,
                    "yellow" to R.drawable.icon_warning_rain_yellow
                ),
                "rainflood" to mapOf(
                    "orange" to R.drawable.icon_warning_rainflood_orange,
                    "red" to R.drawable.icon_warning_rainflood_red,
                    "yellow" to R.drawable.icon_warning_rainflood_yellow
                ),
                "snow" to mapOf(
                    "orange" to R.drawable.icon_warning_snow_orange,
                    "red" to R.drawable.icon_warning_snow_red,
                    "yellow" to R.drawable.icon_warning_snow_yellow
                ),
                "stormsurge" to mapOf(
                    "orange" to R.drawable.icon_warning_stormsurge_orange,
                    "red" to R.drawable.icon_warning_stormsurge_red,
                ),
                "wind" to mapOf(
                    "orange" to R.drawable.icon_warning_wind_orange,
                    "red" to R.drawable.icon_warning_wind_red,
                    "yellow" to R.drawable.icon_warning_wind_yellow
                )
            )
            return warningIconsMap[eventName.lowercase()]?.get(eventColor.lowercase()) ?: warningIconsMap["generic"]?.get(eventColor.lowercase())
        }
    }
}
