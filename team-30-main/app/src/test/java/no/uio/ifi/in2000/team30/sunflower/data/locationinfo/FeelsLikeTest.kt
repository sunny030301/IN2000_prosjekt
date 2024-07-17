package no.uio.ifi.in2000.team30.sunflower.data.locationinfo

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Geometry
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Instant
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.InstantData
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.LocationForecast
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Meta
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.MetaUnits
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.NextXHours
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.NextXHoursDetails
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.NextXHoursSummary
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Properties
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Timeseries
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.TimeseriesData
import org.junit.Test
import kotlin.math.pow

class FeelsLikeTest {
    @Test
    fun `test getFeelsLikeTemp returns correct data`() {
        runTest {
            val mockForecast = LocationForecast(
                type = "Mock",
                geometry = Geometry("Point", listOf(0.0, 0.0, 1001.0)),
                properties = Properties(
                    meta = Meta(
                        units = MetaUnits("C", "hPa", "%", "m/s", "degrees"),
                        updated_at = "2019-12-03T13:52:13Z"
                    ),
                    timeseries = listOf(Timeseries("2019-12-03T14:00:00Z", TimeseriesData(Instant(
                        InstantData(
                            0.0,
                            5.0,
                            0.0,
                            50.0,
                            0.0,
                            5.0 / 3.6
                        )), NextXHours(NextXHoursSummary(""), NextXHoursDetails(0.0)), NextXHours(NextXHoursSummary(""), NextXHoursDetails(0.0)), NextXHours(NextXHoursSummary(""), NextXHoursDetails(0.0)))))
                )
            )

            val locationInfoRepository = LocationInfoRepository()
            val feelsLikeTemp = locationInfoRepository.getFeelsLikeTemp(mockForecast)
            // calculate expected wind chill manually
            val expectedWindChill = 13.12 + (0.6215 * 5.0) - (11.37 * (5.0.pow(0.16))) + (0.3965 * 5.0 * (5.0.pow(0.16)))

            assertEquals(expectedWindChill, feelsLikeTemp)
        }
    }
}
