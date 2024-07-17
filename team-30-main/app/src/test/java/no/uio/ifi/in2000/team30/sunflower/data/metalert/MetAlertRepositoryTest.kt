package no.uio.ifi.in2000.team30.sunflower.data.metalert

import android.annotation.SuppressLint
import android.util.Log
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team30.sunflower.model.metalert.Alert
import no.uio.ifi.in2000.team30.sunflower.model.metalert.AlertColor
import no.uio.ifi.in2000.team30.sunflower.model.metalert.Geometry
import no.uio.ifi.in2000.team30.sunflower.model.metalert.MetAlert
import no.uio.ifi.in2000.team30.sunflower.model.metalert.Properties
import no.uio.ifi.in2000.team30.sunflower.model.metalert.When
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito

class MetAlertRepositoryTest {
    private lateinit var mockDataSource: MetAlertDataSource
    private lateinit var repository: MetAlertRepository
    private var mockedLog: MockedStatic<Log>? = null
    @SuppressLint("CheckResult")
    @Before
    fun setUp() {
        mockDataSource = mockk() // initialize mock datasource
        repository = MetAlertRepository(mockDataSource) // initialize repository with mocked datasource

        mockedLog = Mockito.mockStatic(Log::class.java) // mock static methods of Log
    }

    @After
    fun tearDown() {
        mockedLog!!.close() // close mock after each test
    }

    @Test
    fun `test getCurrentWeatherAlerts returns correct data (mock data)`() {
        runTest {
            // prepare mock to return specific data
            val mockMetAlert = MetAlert(
                type = "FeatureCollection",
                features = listOf(
                    Alert(Geometry(listOf(listOf(listOf(0.0, 0.0))), type = "Mock"), Properties(riskMatrixColor = "Red"), type = "Mock", When(emptyList()))
                ),
                lang = "",
                lastChange = "",
            )

            coEvery { mockDataSource.fetchCurrentWeatherAlerts() } returns mockMetAlert
            val alerts = repository.getCurrentAlertsList()

            // assert expected behavior
            assertEquals(1, alerts.size)
            assertEquals("Red", alerts[0].properties.riskMatrixColor)
            assertEquals("Mock", alerts[0].type)
        }
    }

    @Test
    fun `test getCurrentAlertsList returns all alerts when no alertcolor filter is applied (mock data)`() {
        runTest {
            // prepare mock to return specific data
            val mockMetAlert = MetAlert(
                type = "FeatureCollection",
                features = listOf(
                    Alert(Geometry(listOf(listOf(listOf(0.0, 0.0))), type = "Mock"), Properties(riskMatrixColor = "Yellow"), type = "Mock", When(emptyList())),
                    Alert(Geometry(listOf(listOf(listOf(0.0, 0.0))), type = "Mock"), Properties(riskMatrixColor = "Orange"), type = "Mock", When(emptyList())),
                    Alert(Geometry(listOf(listOf(listOf(0.0, 0.0))), type = "Mock"), Properties(riskMatrixColor = "Red"), type = "Mock", When(emptyList()))
                ),
                lang = "",
                lastChange = "",
            )

            coEvery { mockDataSource.fetchCurrentWeatherAlerts() } returns mockMetAlert
            val alerts = repository.getCurrentAlertsList()

            // assert expected behavior
            assertEquals(3, alerts.size) // should return all alerts in list
            assertEquals("Mock", alerts[0].type) // should return "Mock" as alert type
        }
    }

    @Test
    fun `test getCurrentAlertsList filters by color (mock data)`() {
        runTest {
            // prepare mock to return specific data
            val mockMetAlert = MetAlert(
                type = "FeatureCollection",
                features = listOf(
                    Alert(Geometry(listOf(listOf(listOf(0.0, 0.0))), type = "Mock"), Properties(riskMatrixColor = "Yellow"), type = "Mock", When(emptyList())),
                    Alert(Geometry(listOf(listOf(listOf(0.0, 0.0))), type = "Mock"), Properties(riskMatrixColor = "Orange"), type = "Mock", When(emptyList())),
                    Alert(Geometry(listOf(listOf(listOf(0.0, 0.0))), type = "Mock"), Properties(riskMatrixColor = "Red"), type = "Mock", When(emptyList()))
                ),
                lang = "",
                lastChange = "",
            )

            coEvery { mockDataSource.fetchCurrentWeatherAlerts() } returns mockMetAlert
            val redAlerts = repository.getCurrentAlertsList(null, null, listOf(AlertColor.RED))

            // assert expected behavior
            assertEquals(1, redAlerts.size) // should only return red alerts
            assertEquals("Red", redAlerts[0].properties.riskMatrixColor) // alert color should be "Red"
        }
    }
}