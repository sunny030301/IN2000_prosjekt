package no.uio.ifi.in2000.team30.sunflower.data.metalert

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
import no.uio.ifi.in2000.team30.sunflower.model.metalert.MetAlert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic


class MetAlertDataSourceTest {
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
    fun `fetchCurrentWeatherAlerts returns correct alert data (mock data)`() {
        runTest {
            val mockEngine = MockEngine { request ->
                assertEquals("/weatherapi/metalerts/2.0/current.json", request.url.encodedPath)
                respond(
                    """
                    {
                      "type": "FeatureCollection",
                      "features": [
                        {
                          "geometry": {
                            "coordinates": [
                              [
                                [8.68, 58.4702],
                                [8.1467, 58.1732],
                                [7.9947, 58.1543],
                                [8.0677, 58.0428],
                                [8.2053, 58.0777],
                                [8.2055, 58.0777],
                                [8.2647, 58.0928],
                                [8.267, 58.0933],
                                [8.2698, 58.0943],
                                [8.2725, 58.0955],
                                [8.4982, 58.2053],
                                [8.4985, 58.2055],
                                [8.6798, 58.2953],
                                [8.682, 58.2965],
                                [8.8337, 58.387],
                                [8.68, 58.4702],
                                [8.68, 58.4702]
                              ]
                            ],
                            "type": "Polygon"
                          },
                          "properties": {
                            "altitude_above_sea_level": 0,
                            "area": "Torungen - Oksøy",
                            "awarenessResponse": "Følg med",
                            "awarenessSeriousness": "Utfordrende situasjon",
                            "awareness_level": "2; yellow; Moderate",
                            "awareness_type": "1; Wind",
                            "ceiling_above_sea_level": 274,
                            "certainty": "Likely",
                            "consequences": "Sjøen bygger seg opp og det kan være farlig å være ute i småbåt ",
                            "contact": "https://www.met.no/kontakt-oss",
                            "county": [],
                            "description": "Nordøstlig periodevis stiv kuling 15 m/s. Minkende fredag ettermiddag.",
                            "event": "gale",
                            "eventAwarenessName": "Kuling",
                            "eventEndingTime": "2024-05-03T13:00:00+00:00",
                            "geographicDomain": "marine",
                            "id": "2.49.0.1.578.0.20240502143145.095",
                            "instruction": "Vurder å la båten ligge.",
                            "resources": [
                              {
                                "description": "CAP file",
                                "mimeType": "application/xml",
                                "uri": "https://api.met.no/weatherapi/metalerts/2.0/current?cap=2.49.0.1.578.0.20240502143145.095"
                              }
                            ],
                            "riskMatrixColor": "Yellow",
                            "severity": "Moderate",
                            "status": "Actual",
                            "title": "Kuling, gult nivå, Torungen - Oksøy, 2024-05-01T18:00:00+00:00, 2024-05-03T13:00:00+00:00",
                            "type": "Update",
                            "web": "https://www.met.no/vaer-og-klima/ekstremvaervarsler-og-andre-farevarsler/vaerfenomener-som-kan-gi-farevarsel-fra-met/kuling-stormvarsel-for-kyst-og-naere-fiskebanker"
                          },
                          "type": "Feature",
                          "when": {
                            "interval": [
                              "2024-05-01T18:00:00+00:00",
                              "2024-05-03T13:00:00+00:00"
                            ]
                          }
                        }
                      ]
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
            val dataSource = MetAlertDataSource(client)
            val alerts = dataSource.fetchCurrentWeatherAlerts()

            assertEquals(1, alerts.features.size)
            assertEquals("Feature", alerts.features[0].type)
            assertEquals("Update", alerts.features[0].properties.type)
            assertEquals("Moderate", alerts.features[0].properties.severity)
            assertEquals("Vurder å la båten ligge.", alerts.features[0].properties.instruction)
            assertEquals("Yellow", alerts.features[0].properties.riskMatrixColor)
            assertEquals("Polygon", alerts.features[0].geometry.type)
        }
    }

    @Test
    fun `test fetchCurrentWeatherAlerts returns blank data with invalid coordinates (mock data)`() {
        runTest {
            val mockEngine = MockEngine { request ->
                assertEquals("/weatherapi/metalerts/2.0/current.json", request.url.encodedPath)
                respond(
                    """
                    {
                        "features": [],
                        "lang": "no",
                        "lastChange": "2024-05-02T17:25:53+00:00",
                        "type": "FeatureCollection"
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
            val dataSource = MetAlertDataSource(client)
            val alerts: MetAlert = dataSource.fetchCurrentWeatherAlerts(100.0, 200.0)
            assertTrue(alerts.features.isEmpty())
            assertTrue(alerts.lastChange.isNotBlank())
            assertEquals("FeatureCollection", alerts.type)
        }
    }

    /*
    @Test
    fun `test fetchCurrentWeatherAlerts deserializes correctly and and returns valid data (all current weather alerts) (from API)`() {
        val dataSource = MetAlertDataSource()
        runTest {
            val weatherAlerts: MetAlert = dataSource.fetchCurrentWeatherAlerts()
            assertEquals("FeatureCollection", weatherAlerts.type)
            assertTrue(weatherAlerts.lastChange.isNotBlank())
            if (weatherAlerts.features.isNotEmpty()) {
                assertEquals("Feature", weatherAlerts.features[0].type)
                assertEquals("Alert", weatherAlerts.features[0].properties.type)
                assertTrue(weatherAlerts.features[0].geometry.coordinates.isNotEmpty())
                assertTrue(weatherAlerts.features[0].`when`.interval.isNotEmpty())
                assertTrue(weatherAlerts.features[0].properties.area.isNotBlank())
                assertTrue(weatherAlerts.features[0].properties.instruction.isNotBlank())
            }
        }
    }
     */

    /*
    @Test
    fun `test fetchCurrentWeatherAlerts returns valid data with valid coordinates (API data)`() {
        val dataSource = MetAlertDataSource()
        runTest {
            val weatherAlerts: MetAlert = dataSource.fetchCurrentWeatherAlerts(59.9436145,10.7182883) // <-- Ole-Johan Dahls hus
            assertEquals("FeatureCollection", weatherAlerts.type)
            assertTrue(weatherAlerts.lastChange.isNotBlank())
            if (weatherAlerts.features.isNotEmpty()) {
                assertTrue(weatherAlerts.features[0].geometry.coordinates.isNotEmpty())
                assertEquals("Alert", weatherAlerts.features[0].properties.type)
                assertTrue(weatherAlerts.features[0].`when`.interval.isNotEmpty())
                assertTrue(weatherAlerts.features[0].properties.area.isNotBlank())
                assertTrue(weatherAlerts.features[0].properties.instruction.isNotBlank())
            }
        }
    }
     */

    /*
    @Test
    fun `test fetchCurrentWeatherAlerts returns blank data with invalid coordinates (API data)`() {
        val dataSource = MetAlertDataSource()
        runTest {
            val weatherAlerts: MetAlert = dataSource.fetchCurrentWeatherAlerts(100.0, 200.0) // <-- Ole-Johan Dahls hus
            assertEquals("FeatureCollection", weatherAlerts.type)
            assertTrue(weatherAlerts.lastChange.isNotBlank())
        }
    }
     */
}