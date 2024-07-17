package no.uio.ifi.in2000.team30.sunflower.data.locationforecast

import android.annotation.SuppressLint
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic

class LocationForecastDataSourceTest {
    private var mockedLog: MockedStatic<Log>? = null
    @SuppressLint("CheckResult")
    @Before
    fun setUp() {
        mockedLog = mockStatic(Log::class.java) // mock static methods of Log
    }

    @After
    fun tearDown() {
        mockedLog!!.close() // close mock after each test
    }

    @Test
    fun `test fetchWeatherInfo deserializes correctly and returns correct location forecast (mock data)`() {
        val lat = 59.8939243
        val lon = 10.6203136
        runTest {
            val mockEngine = MockEngine { request ->
                assertEquals("/weatherapi/locationforecast/2.0/compact", request.url.encodedPath)
                assertEquals("Parameters [lat=[$lat], lon=[$lon]]", request.url.parameters.toString())
                respond(
            """
                    {
                      "type": "Feature",
                      "geometry": {
                        "type": "Point",
                        "coordinates": [10.6203136, 59.8939243, 1001.0]
                      },
                      "properties": {
                        "meta": {
                          "updated_at": "2019-12-03T13:52:13Z",
                          "units": {
                            "air_pressure_at_sea_level": "hPa",
                            "air_temperature": "celsius",
                            "cloud_area_fraction": "%",
                            "precipitation_amount": "mm",
                            "relative_humidity": "%",
                            "wind_from_direction": "degrees",
                            "wind_speed": "m/s"
                          }
                        },
                        "timeseries": [
                          {
                            "time": "2019-12-03T14:00:00Z",
                            "data": {
                              "instant": {
                                "details": {
                                  "air_pressure_at_sea_level": 1017.23,
                                  "air_temperature": 17.1,
                                  "cloud_area_fraction": 95.2,
                                  "dew_point_temperature": 8.1,
                                  "relative_humidity": 81.1,
                                  "wind_from_direction": 121.3,
                                  "wind_speed": 5.9
                                }
                              }
                            }
                          }
                        ]
                      }
                    }
                """,
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }

            val client = HttpClient(mockEngine) {
                install(ContentNegotiation) {
                    gson()
                }
            }

            val dataSource = LocationForecastDataSource(client)
            val forecast = dataSource.fetchWeatherInfo(lat, lon)

            assertEquals("Feature", forecast.type)
            assertEquals(59.8939243, forecast.geometry.coordinates[1])
            assertEquals(10.6203136, forecast.geometry.coordinates[0])
            assertEquals(1017.23, forecast.properties.timeseries[0].data.instant.details.air_pressure_at_sea_level)
            assertEquals(17.1, forecast.properties.timeseries[0].data.instant.details.air_temperature)
            assertEquals(95.2, forecast.properties.timeseries[0].data.instant.details.cloud_area_fraction)
            assertEquals(81.1, forecast.properties.timeseries[0].data.instant.details.relative_humidity)
            assertEquals(121.3, forecast.properties.timeseries[0].data.instant.details.wind_from_direction)
            assertEquals(5.9, forecast.properties.timeseries[0].data.instant.details.wind_speed)
        }
    }

    @Test
    fun `test fetchWeatherInfo handles invalid coordinates (mock data)`() {
        val lat = 100.0
        val lon = 200.0
        runTest {
            val mockEngine = MockEngine { request ->
                assertEquals("/weatherapi/locationforecast/2.0/compact", request.url.encodedPath)
                assertEquals("Parameters [lat=[$lat], lon=[$lon]]", request.url.parameters.toString())
                respond(
            "", // blank response
                    HttpStatusCode.BadRequest,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }

            val client = HttpClient(mockEngine) {
                install(ContentNegotiation) {
                    gson()
                }
            }

            val dataSource = LocationForecastDataSource(client)
            //val forecast = dataSource.fetchWeatherInfo(lat, lon) // invalid coordinates

            // assert that exception is thrown when fetching weather info with invalid coordinates
            val exception = assertThrows<Exception> {
                dataSource.fetchWeatherInfo(lat, lon)
            }

            assertTrue(exception.message!!.contains("Failed to fetch weather data:"))
        }
    }

    /*
    @Test
    fun `test fetchWeatherInfo deserializes correctly and returns valid data for valid coordinates (API data)`() {
        val dataSource = LocationForecastDataSource()
        runTest {
            val forecast = dataSource.fetchWeatherInfo(59.9436145,10.7182883) // <-- Ole-Johan Dahls hus
            assertEquals("Feature", forecast.type)
            assertTrue(forecast.geometry.coordinates.toString().startsWith("[10.7183, 59.9436"))
            assertEquals(Double::class, forecast.properties.timeseries[0].data.instant.details.wind_speed::class)
            assertEquals(Double::class, forecast.properties.timeseries[0].data.instant.details.air_temperature::class)
            assertEquals("celsius", forecast.properties.meta.units.air_temperature)
        }
    }
     */

    /*
    @Test
    fun `test fetchWeatherInfo handles invalid coordinates (API data)`() {
        val dataSource = LocationForecastDataSource()
        runTest {
            val forecast = dataSource.fetchWeatherInfo(100.0, 200.0) // invalid coordinates
            assertNotEquals("Feature", forecast.type) // if invalid coordinates, forecast.type = "$e"
        }
    }
     */
}