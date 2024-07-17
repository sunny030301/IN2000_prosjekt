package no.uio.ifi.in2000.team30.sunflower.data.locationforecast

import android.annotation.SuppressLint
import android.util.Log
import io.mockk.coEvery
import io.mockk.mockk
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
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito

class LocationForecastRepositoryTest {
    private lateinit var mockDataSource: LocationForecastDataSource
    private lateinit var repository: LocationForecastRepository
    private var mockedLog: MockedStatic<Log>? = null
    @SuppressLint("CheckResult")
    @Before
    fun setUp() {
        mockDataSource = mockk() // initialize mock datasource
        repository = LocationForecastRepository(mockDataSource) // initialize repository with mocked datasource

        mockedLog = Mockito.mockStatic(Log::class.java) // mock static methods of Log
    }

    @After
    fun tearDown() {
        mockedLog!!.close() // close mock after each test
    }

    @Test
    fun `test getLocationForecast returns correct forecast data (mock data)`() {
        runTest {
            // Arrange
            val expectedLat = 59.9437
            val expectedLon = 10.7209
            val mockForecast = LocationForecast(
                type = "Mock",
                geometry = Geometry("Point", listOf(expectedLon, expectedLat, 1001.0)),
                properties = Properties(
                    meta = Meta(
                        units = MetaUnits("C", "hPa", "%", "m/s", "degrees"),
                        updated_at = "2019-12-03T13:52:13Z"
                    ),
                    timeseries = listOf(Timeseries("2019-12-03T14:00:00Z", TimeseriesData(Instant(InstantData(1017.23, 17.1, 95.2, 121.3, 5.9)), NextXHours(NextXHoursSummary(""), NextXHoursDetails(0.0)), NextXHours(NextXHoursSummary(""), NextXHoursDetails(0.0)), NextXHours(NextXHoursSummary(""), NextXHoursDetails(0.0)))))
                )
            )

            coEvery { mockDataSource.fetchWeatherInfo(expectedLat, expectedLon) } returns mockForecast
            val forecast = repository.getLocationForecast(expectedLat, expectedLon)

            // assert expected behavior
            assertEquals(mockForecast, forecast)
            assertEquals("Mock", forecast.type)
            assertEquals(expectedLat, forecast.geometry.coordinates[1])
            assertEquals(expectedLon, forecast.geometry.coordinates[0])
        }
    }

    /*
    @Test
    fun `test getLocationForecast returns LocationForecast object with fetched valid data (from API)`() {
        val repository = LocationForecastRepository()
        runTest {
            val forecast = repository.getLocationForecast(59.9436145,10.7182883) // <-- Ole-Johan Dahls hus
            assertEquals("Feature", forecast.type)
            assertTrue(forecast.geometry.coordinates.toString().startsWith("[10.7183, 59.9436"))
            assertEquals(Double::class, forecast.properties.timeseries[0].data.instant.details.wind_speed::class)
            assertEquals(Double::class, forecast.properties.timeseries[0].data.instant.details.air_temperature::class)
            assertEquals("celsius", forecast.properties.meta.units.air_temperature)
        }
    }
     */
}