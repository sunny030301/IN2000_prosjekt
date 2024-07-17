package no.uio.ifi.in2000.team30.sunflower.data.places

import android.annotation.SuppressLint
import android.util.Log
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team30.sunflower.model.places.autocomplete.Place
import no.uio.ifi.in2000.team30.sunflower.model.places.placedetails.Geometry
import no.uio.ifi.in2000.team30.sunflower.model.places.placedetails.Location
import no.uio.ifi.in2000.team30.sunflower.model.places.placedetails.PlaceDetails
import no.uio.ifi.in2000.team30.sunflower.model.places.placedetails.Result
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.MockedStatic
import org.mockito.Mockito

class PlacesRepositoryTest {
    private lateinit var placesDataSource: PlacesDataSource
    private lateinit var placesRepository: PlacesRepository
    private var mockedLog: MockedStatic<Log>? = null
    @SuppressLint("CheckResult")
    @Before
    fun setUp() {
        placesDataSource = mockk()
        placesRepository = PlacesRepository(placesDataSource)

        mockedLog = Mockito.mockStatic(Log::class.java) // mock static methods of Log
    }

    @After
    fun tearDown() {
        mockedLog!!.close() // close mock after each test
    }

    @Test
    fun `test getPlaces returns a list of Place objects from DataSource (mock data)`() {
        runTest {
            val expectedPlaces = listOf(Place("ID_1", "Place1"), Place("ID_2", "Place2"))
            coEvery { placesDataSource.fetchPlaces("dummyKey", "dummyQuery") } returns expectedPlaces

            val result = placesRepository.getPlaces("dummyKey", "dummyQuery")

            assertEquals("Place1", expectedPlaces[0].name)
            assertEquals("ID_1", expectedPlaces[0].id)
            assertEquals("Place2", expectedPlaces[1].name)
            assertEquals("ID_2", expectedPlaces[1].id)
            assertEquals(expectedPlaces, result)
        }
    }

    @Test
    fun `getPlaceDetails returns PlaceDetails from DataSource (mock data)`() {
        runTest {
            val expectedPlaceDetails = PlaceDetails(Result(Geometry(Location(30.0, 10.0)), "Place1", "ID_1", listOf("Type1")))
            coEvery { placesDataSource.fetchPlaceDetails("dummyKey", "placeId") } returns expectedPlaceDetails

            val result = placesRepository.getPlaceDetails("dummyKey", "placeId")

            assertEquals("Place1", expectedPlaceDetails.result.name)
            assertEquals("ID_1", expectedPlaceDetails.result.place_id)
            assertEquals(30.0, expectedPlaceDetails.result.geometry.location.lat)
            assertEquals(10.0, expectedPlaceDetails.result.geometry.location.lng)
            assertEquals("Type1", expectedPlaceDetails.result.types[0])
            assertEquals(expectedPlaceDetails, result)
        }
    }
}