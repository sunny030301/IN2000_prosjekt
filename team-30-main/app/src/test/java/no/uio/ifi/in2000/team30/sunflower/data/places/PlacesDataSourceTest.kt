package no.uio.ifi.in2000.team30.sunflower.data.places

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
import io.ktor.serialization.kotlinx.json.json
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito

class PlacesDataSourceTest {
    private var mockedLog: MockedStatic<Log>? = null
    @SuppressLint("CheckResult")
    @Before
    fun setUp() {
        mockedLog = Mockito.mockStatic(Log::class.java) // mock static methods of Log
    }

    @After
    fun tearDown() {
        mockedLog!!.close() // close mock after each test
    }
    @Test
    fun `fetchPlaces returns correct places list (mock data)`() {
        runTest {
            // prepare mock to return specific data
            val json = Json { ignoreUnknownKeys = true; prettyPrint = true; isLenient = true }
            val mockEngine = MockEngine { request ->
                assertEquals("maps.googleapis.com", request.url.host)
                assertEquals("/maps/api/place/autocomplete/json", request.url.encodedPath)
                assertEquals("query", request.url.parameters["input"])
                assertEquals("API_KEY", request.url.parameters["key"])
                respond(
                    """
                {
                  "predictions": [
                    {
                      "description": "Paris, France",
                      "matched_substrings": [{"length": 5, "offset": 0}],
                      "place_id": "ChIJD7fiBh9u5kcRYJSMaMOCCwQ",
                      "reference": "ChIJD7fiBh9u5kcRYJSMaMOCCwQ",
                      "structured_formatting": {
                        "main_text": "Paris",
                        "main_text_matched_substrings": [{"length": 5, "offset": 0}],
                        "secondary_text": "France"
                      },
                      "terms": [
                        {"offset": 0, "value": "Paris"},
                        {"offset": 7, "value": "France"}
                      ],
                      "types": ["locality", "political", "geocode"]
                    },
                    {
                      "description": "Paris, TX, USA",
                      "matched_substrings": [{"length": 5, "offset": 0}],
                      "place_id": "ChIJmysnFgZYSoYRSfPTL2YJuck",
                      "reference": "ChIJmysnFgZYSoYRSfPTL2YJuck",
                      "structured_formatting": {
                        "main_text": "Paris",
                        "main_text_matched_substrings": [{"length": 5, "offset": 0}],
                        "secondary_text": "TX, USA"
                      },
                      "terms": [
                        {"offset": 0, "value": "Paris"},
                        {"offset": 7, "value": "TX"},
                        {"offset": 11, "value": "USA"}
                      ],
                      "types": ["locality", "political", "geocode"]
                    }
                  ],
                  "status": "OK"
                }
                """,
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }
            val client = HttpClient(mockEngine) {
                install(ContentNegotiation) {
                    json(json)
                }
            }
            val dataSource = PlacesDataSource(client)
            val places = dataSource.fetchPlaces("API_KEY", "query")

            // assert expected behavior
            assertEquals(2, places.size)
            assertEquals("Paris, France", places[0].name)
            assertEquals("ChIJD7fiBh9u5kcRYJSMaMOCCwQ", places[0].id)
            assertEquals("Paris, TX, USA", places[1].name)
            assertEquals("ChIJmysnFgZYSoYRSfPTL2YJuck", places[1].id)
        }
    }

    @Test
    fun `fetchPlaceDetails returns correct place details (mock data)`() {
        runTest {
            val placeId = "ChIJN1t_tDeuEmsRUsoyG83frY4"
            // prepare mock to return specific data
            val json = Json { ignoreUnknownKeys = true; prettyPrint = true; isLenient = true }
            val mockEngine = MockEngine { request ->
                assertEquals("maps.googleapis.com", request.url.host)
                assertEquals("/maps/api/place/details/json", request.url.encodedPath)
                assertEquals(placeId, request.url.parameters["place_id"])
                assertEquals("API_KEY", request.url.parameters["key"])
                respond(
                    """
                   {
                    "html_attributions": [],
                    "result": {
                        "address_components": [
                            {"long_name": "48", "short_name": "48", "types": ["street_number"]},
                            {"long_name": "Pirrama Road", "short_name": "Pirrama Rd", "types": ["route"]},
                            {"long_name": "Pyrmont", "short_name": "Pyrmont", "types": ["locality", "political"]},
                            {"long_name": "City of Sydney", "short_name": "City of Sydney", "types": ["administrative_area_level_2", "political"]},
                            {"long_name": "New South Wales", "short_name": "NSW", "types": ["administrative_area_level_1", "political"]},
                            {"long_name": "Australia", "short_name": "AU", "types": ["country", "political"]},
                            {"long_name": "2009", "short_name": "2009", "types": ["postal_code"]}
                        ],
                        "adr_address": "<span class=\"street-address\">48 Pirrama Rd</span>, <span class=\"locality\">Pyrmont</span> <span class=\"region\">NSW</span> <span class=\"postal-code\">2009</span>, <span class=\"country-name\">Australia</span>",
                        "business_status": "OPERATIONAL",
                        "formatted_address": "48 Pirrama Rd, Pyrmont NSW 2009, Australia",
                        "formatted_phone_number": "(02) 9374 4000",
                        "geometry": {
                            "location": {"lat": -33.866489, "lng": 151.1958561},
                            "viewport": {
                                "northeast": {"lat": -33.8655112697085, "lng": 151.1971156302915},
                                "southwest": {"lat": -33.86820923029149, "lng": 151.1944176697085}
                            }
                        },
                        "icon": "https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/generic_business-71.png",
                        "icon_background_color": "#7B9EB0",
                        "icon_mask_base_uri": "https://maps.gstatic.com/mapfiles/place_api/icons/v2/generic_pinlet",
                        "international_phone_number": "+61 2 9374 4000",
                        "name": "Google Workplace 6",
                        "opening_hours": {
                            "open_now": false,
                            "periods": [
                                {"close": {"day": 1, "time": "1700"}, "open": {"day": 1, "time": "0900"}},
                                {"close": {"day": 2, "time": "1700"}, "open": {"day": 2, "time": "0900"}},
                                {"close": {"day": 3, "time": "1700"}, "open": {"day": 3, "time": "0900"}},
                                {"close": {"day": 4, "time": "1700"}, "open": {"day": 4, "time": "0900"}},
                                {"close": {"day": 5, "time": "1700"}, "open": {"day": 5, "time": "0900"}}
                            ],
                            "weekday_text": [
                                "Monday: 9:00 AM – 5:00 PM",
                                "Tuesday: 9:00 AM – 5:00 PM",
                                "Wednesday: 9:00 AM – 5:00 PM",
                                "Thursday: 9:00 AM – 5:00 PM",
                                "Friday: 9:00 AM – 5:00 PM",
                                "Saturday: Closed",
                                "Sunday: Closed"
                            ]
                        },
                        "photos": [
                            {
                                "height": 3024,
                                "html_attributions": ["<a href=\"https://maps.google.com/maps/contrib/117600448889234589608\">Cynthia Wei</a>"],
                                "photo_reference": "Aap_uEC6jqtpflLS8GxQqPHBjlcwBf2sri0ZErk9q1ciHGZ6Zx5HBiiiEsPEO3emtB1PGyWbBQhgPL2r9CshoVlJEG4xzB71QMhGBTqqeaCNk1quO3vTTiP50aM1kmOaBQ-DF1ER7zpu6BQOEtnusKMul0m4KA45wfE3h6Xh2IxjLNzx-IiX",
                                "width": 4032
                            },
                            {
                                "height": 3264,
                                "html_attributions": ["<a href=\"https://maps.google.com/maps/contrib/102493344958625549078\">Heyang Li</a>"],
                                "photo_reference": "Aap_uECyRjHhOQgGaKTW6Z3ZfTEaDhNc44m0F6GrNSFIMffixwI5xqD35QhecdzVY-FUuDtVE1huu8-2HkxgI9Gwvy6W18fU-_E3UUkdSFBQqGK8_slKlT8BZZc66sTX53IEcTDrZfT-E5_YUBYBOm13yxOTOfWfEDABhaxCGC5Hu_XYh0fI",
                                "width": 4912
                            }
                        ],
                        "place_id": "ChIJN1t_tDeuEmsRUsoyG83frY4",
                        "plus_code": {
                            "compound_code": "45MW+C8 Pyrmont NSW, Australia",
                            "global_code": "4RRH45MW+C8"
                        },
                        "rating": 4,
                        "reference": "ChIJN1t_tDeuEmsRUsoyG83frY4",
                        "reviews": [
                            {
                                "author_name": "Luke Archibald",
                                "author_url": "https://www.google.com/maps/contrib/113389359827989670652/reviews",
                                "language": "en",
                                "profile_photo_url": "https://lh3.googleusercontent.com/a-/AOh14GhGGmTmvtD34HiRgwHdXVJUTzVbxpsk5_JnNKM5MA=s128-c0x00000000-cc-rp-mo",
                                "rating": 1,
                                "relative_time_description": "a week ago",
                                "text": "Called regarding paid advertising google pages to the top of its site of a scam furniture website misleading and taking peoples money without ever sending a product - explained the situation,  explained I'd spoken to an ombudsman regarding it.  Listed ticket numbers etc.\n\nThey left the advertisement running.",
                                "time": 1652286798
                            }
                        ],
                        "types": ["point_of_interest", "establishment"],
                        "url": "https://maps.google.com/?cid=10281119596374313554",
                        "user_ratings_total": 939,
                        "utc_offset": 600,
                        "vicinity": "48 Pirrama Road, Pyrmont",
                        "website": "http://google.com/"
                    },
                    "status": "OK"
                }
                """,
                    HttpStatusCode.OK,
                    headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }
            val client = HttpClient(mockEngine) {
                install(ContentNegotiation) {
                    json(json)
                }
            }
            val dataSource = PlacesDataSource(client)
            val placeDetails = dataSource.fetchPlaceDetails("API_KEY", placeId)

            // assert expected behavior
            assertEquals("Google Workplace 6", placeDetails.result.name)
            assertEquals(-33.866489, placeDetails.result.geometry.location.lat)
            assertEquals(151.1958561, placeDetails.result.geometry.location.lng)
            assertEquals(placeId, placeDetails.result.place_id)
        }
    }

    /*
    @Test
    fun `test fetchPlaces returns valid data from search (API data)`() {
        val dataSource = PlacesDataSource()
        runTest {
            val placesList: List<Place> = dataSource.fetchPlaces(BuildConfig.MAPS_API_KEY, "Stovner")
            assertEquals("Stovner, Oslo, Norway", placesList[0].name)
            assertEquals("Stovner", placesList[0].main_text)
            assertEquals("Oslo, Norway", placesList[0].secondary_text)
            assertEquals("ChIJtz4e5rt6QUYRpaki2cW_ib0", placesList[0].id)
        }
    }
     */

    /*
    @Test
    fun `test fetchPlaceDetails returns valid data from search (API data)`() {
        val dataSource = PlacesDataSource()
        runTest {
            val placeDetails: PlaceDetails = dataSource.fetchPlaceDetails(BuildConfig.MAPS_API_KEY, "ChIJtz4e5rt6QUYRpaki2cW_ib0")
            assertEquals("ChIJtz4e5rt6QUYRpaki2cW_ib0", placeDetails.result.place_id)
            assertEquals("Stovner", placeDetails.result.name)
            assertEquals(59.96085569999999, placeDetails.result.geometry.location.lat)
            assertEquals(10.9229984, placeDetails.result.geometry.location.lng)
        }
    }
     */
}
