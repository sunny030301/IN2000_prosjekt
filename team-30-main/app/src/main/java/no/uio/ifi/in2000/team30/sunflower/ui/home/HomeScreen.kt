package no.uio.ifi.in2000.team30.sunflower.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.ComponentRegistry
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team30.sunflower.R
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Instant
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.InstantData
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.LocationForecast
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Meta
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.MetaUnits
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.NextXHours
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.NextXHoursDetails
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.NextXHoursSummary
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.TimeseriesData
import no.uio.ifi.in2000.team30.sunflower.model.metalert.Alert
import no.uio.ifi.in2000.team30.sunflower.model.metalert.AlertColor
import no.uio.ifi.in2000.team30.sunflower.model.metalert.Geometry
import no.uio.ifi.in2000.team30.sunflower.model.metalert.Properties
import no.uio.ifi.in2000.team30.sunflower.model.metalert.Resource
import no.uio.ifi.in2000.team30.sunflower.model.metalert.When
import no.uio.ifi.in2000.team30.sunflower.ui.shared.SharedComposables.Companion.BottomNavigationBar
import no.uio.ifi.in2000.team30.sunflower.ui.shared.SharedComposables.Companion.LoadingContent
import no.uio.ifi.in2000.team30.sunflower.ui.shared.SharedComposables.Companion.SearchBox
import no.uio.ifi.in2000.team30.sunflower.ui.theme.SunflowerTheme
import no.uio.ifi.in2000.team30.sunflower.utils.DateDisplayUtils
import no.uio.ifi.in2000.team30.sunflower.utils.MetAPIDisplayUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = viewModel(), navController: NavController, navLat: Double = 0.0, navLon: Double = 0.0) {
    // observe viewmodel states
    val isLoading: Boolean by viewModel.isLoading.collectAsState()
    val error: String? by viewModel.error.collectAsState()
    val snackBarHostState by viewModel.snackBarHostState.collectAsState()
    val alertsUiState by viewModel.currentAlertsUiState.collectAsState()
    val locationPermissionUiState by viewModel.locationPermissionUiState.collectAsState()
    val locationForecastUiState by viewModel.locationForecastsUiState.collectAsState()
    val locationInfoUiState by viewModel.locationInfoUiState.collectAsState()
    val locationCoordinates by viewModel.locationCoordinates.collectAsState()
    val timeZoneUiState by viewModel.currentTimeZone.collectAsState()
    val defaultCoordinates by viewModel.defaultCoordinates.collectAsState()
    val context: Context = LocalContext.current
    val initialized by viewModel.initialized.collectAsState()
    val isRefreshing: Boolean by viewModel.isRefreshing.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh(locationCoordinates.lat, locationCoordinates.lon, context) }
    )

    Log.d("HomeScreen - Lat lon", "${locationCoordinates.lat}, ${locationCoordinates.lon}")

    // react to changes in nav coordinates from parameter
    LaunchedEffect(navLat, navLon, locationPermissionUiState.permissionResult) {
        if (locationPermissionUiState.permissionResult) {
            // if location is not initialized, fetch location
            if (!initialized) {
                viewModel.initialize(context)
            }
        } else if (!initialized) {
            viewModel.refresh(defaultCoordinates.lat, defaultCoordinates.lon, context) // if location permission denied, default to default location
            snackBarHostState.showSnackbar(
                message = "Permission to get current location from device denied.\nPlease enable location permission on your device.\nDefaulting to Oslo/Norway as location.",
                duration = SnackbarDuration.Long,
            )
        }
        if (initialized && navLat != locationCoordinates.lon && navLon != locationCoordinates.lon) {
            viewModel.refresh(navLat, navLon, context)
        }
    }

/* ui code */
    if (isLoading) LoadingContent()
    else {
        Scaffold (
            snackbarHost = { SnackbarHost(modifier = Modifier.padding(16.dp), hostState = snackBarHostState) },
            bottomBar = {
                BottomNavigationBar(navController = navController, lat = locationCoordinates.lat, lon = locationCoordinates.lon, navSource = "HomeScreen")
                if (error != null) {
                    LaunchedEffect(snackBarHostState) {
                        val snackbarResult = snackBarHostState.showSnackbar(
                            message = "$error",
                            duration = SnackbarDuration.Indefinite,
                            withDismissAction = false,
                        )
                        if (snackbarResult == SnackbarResult.Dismissed) {
                            viewModel.refresh(locationCoordinates.lat, locationCoordinates.lon, context)
                        }
                    }
                }
            },
            floatingActionButton = {
                SearchBox(navController, "HomeScreen")
            }
        ) {
            Box (modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxSize()
            ) {
                val gradient = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(255, 255, 255, 50)
                    )
                )
                var background = R.drawable.background
                if (locationForecastUiState.locationForecast.properties.timeseries.isNotEmpty()) {
                    if (locationForecastUiState.locationForecast.properties.timeseries[0].data.next_1_hours?.summary?.symbol_code?.contains("rain")!!) {
                        background = R.drawable.background_rain
                    } else if (locationForecastUiState.locationForecast.properties.timeseries[0].data.instant.details.air_temperature.toInt() <= 0) {
                        background = R.drawable.background_snow
                    }
                }
                Image(
                    painter = painterResource(background),
                    contentDescription = "Background showing current weather",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.BottomCenter,
                    modifier = Modifier
                        .matchParentSize()
                        .verticalScroll(rememberScrollState())
                )
                Box(modifier = Modifier
                    .matchParentSize()
                    .background(gradient)
                )

                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.padding(top = 8.dp))

                    if (alertsUiState.alerts.isNotEmpty()) {
                        LazyColumn(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.heightIn(0.dp, 1000.dp)
                        ) {
                            items(alertsUiState.alerts.size) { index ->
                                Log.d("HomeScreen", "alertsUiState.alerts.size = ${alertsUiState.alerts.size}")
                                DangerCard(alertsUiState.alerts[index])
                            }
                        }
                    }

                    WeatherCard(
                        locationForecast = locationForecastUiState.locationForecast,
                        feelsLike = locationInfoUiState.feelsLikeTemp,
                        areaName = locationInfoUiState.areaName,
                        onOverflow = { newAreaName ->
                            viewModel.updateAreaName(newAreaName)
                        },
                        timezoneId = timeZoneUiState.timeZone.timeZoneId
                    )
                }
                Column (
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 120.dp, vertical = 16.dp)
                ) {
                    // mascot
                    DynamicMascot(locationForecastUiState.locationForecast)
                }
                PullRefreshIndicator(isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
            }
        }
    }
}

@Composable
fun DangerCard(alert: Alert) {
    // get screenWidth
    val localConfiguration = LocalConfiguration.current
    val screenWidth = localConfiguration.screenWidthDp.dp

    val context: Context = LocalContext.current

    val dangerColor: AlertColor = when (alert.properties.riskMatrixColor) {
        "Yellow" -> AlertColor.YELLOW
        "Orange" -> AlertColor.ORANGE
        "Red" -> AlertColor.RED
        else -> AlertColor.YELLOW
    }

    val dangerCardTextColor: Color = when (dangerColor) {
        AlertColor.YELLOW -> Color(44, 44, 44)
        AlertColor.ORANGE -> Color(44, 44, 44)
        AlertColor.RED -> Color.White
    }

    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
            .clickable {
                // TODO: Navigate to DangerDetailsScreen
            }
    ) {
        Card (
            modifier = Modifier,
            colors = CardDefaults.cardColors(
                containerColor = when (dangerColor) {
                    AlertColor.RED -> Color(198, 0, 0).copy(0.62f)
                    AlertColor.ORANGE -> Color(241, 185, 149)
                    AlertColor.YELLOW -> Color(235, 191, 0).copy(0.62f)
                },
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    // row, to arrange icon image box and weather alert name/details horizontally
                    Row(modifier = Modifier.width(screenWidth-130.dp)) {
                        Column {
                            /* alert title */
                            Row (Modifier.padding(top = 8.dp)){
                                /* alert event name */
                                Text(
                                    modifier = Modifier.padding(start = 20.dp),
                                    text = alert.properties.eventAwarenessName,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = dangerCardTextColor,
                                )
                            }
                            /* alert area name */
                            Text(
                                modifier = Modifier.padding(start = 20.dp),
                                text = alert.properties.area,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = dangerCardTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis, // ellipsis to indicate that the text has overflowed.
                            )

                            /* alert time expire */
                            Row {
                                // formats time to display
                                val formattedTime = DateDisplayUtils.parseAndFormatDate(alert.`when`.interval[1], "UTC", "yyyy-MM-dd'T'HH:mm:ssXXX", "UTC", "HH:mm")
                                // gets today and tomorrows date
                                val todayAndTomorrowPair: Pair<Date, Date> = DateDisplayUtils.getDatesTodayTomorrowFromCalendar()

                                // prepare today and tomorrow date comparison with alertEndDate, formats today/tomorrow dates and alertEndDate to "dd. MMM yyyy"
                                val todayDateStr = DateDisplayUtils.parseAndFormatDate(todayAndTomorrowPair.first.toString(), todayAndTomorrowPair.first.toString().split(" ")[4], "EEE MMM dd HH:mm:ss zzz yyyy", "UTC", "dd. MMM yyyy")
                                val tomorrowDateStr = DateDisplayUtils.parseAndFormatDate(todayAndTomorrowPair.second.toString(), todayAndTomorrowPair.first.toString().split(" ")[4], "EEE MMM dd HH:mm:ss zzz yyyy", "UTC", "dd. MMM yyyy")
                                val alertEndDate = DateDisplayUtils.parseAndFormatDate(alert.`when`.interval[1], "UTC", "yyyy-MM-dd'T'HH:mm:ssXXX", "UTC", "dd. MMM yyyy")

                                // set dateText string to today/tomorrow (relative date) depending on if alertEndDate is todayDateStr/tomorrowDateStr
                                val dateText = DateDisplayUtils.getRelativeDateString(alertEndDate, todayDateStr, tomorrowDateStr)

                                Text(
                                    modifier = Modifier.padding(start = 20.dp),
                                    text = "Expires $formattedTime ${dateText.lowercase()}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = dangerCardTextColor
                                )
                            }
                            /* alert instruction */
                            Row {
                                Text(
                                    modifier = Modifier
                                        .padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
                                        .width(240.dp),
                                    text = alert.properties.instruction,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = dangerCardTextColor,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis, // ellipsis to indicate that the text has overflowed.
                                )
                            }
                        }
                    }
                    /* box, displaying alert icon */
                    Box(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(100.dp),
                    ) {
                        val imageLoader = ImageLoader.Builder(context)
                            .components(fun ComponentRegistry.Builder.() { add(SvgDecoder.Factory()) })
                            .build()

                        val warningEvent = alert.properties.event
                        val warningColor = alert.properties.riskMatrixColor
                        val dangerAlertIcon = MetAPIDisplayUtils.getDangerAlertIcon(warningEvent, warningColor)
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = dangerAlertIcon,
                                imageLoader = imageLoader
                            ),
                            contentDescription = "Danger alert icon",
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DynamicMascot (locationForecast: LocationForecast) {
    // get screenWidth and screenHeight
    val localConfiguration = LocalConfiguration.current
    val screenWidth = localConfiguration.screenWidthDp.dp
    val screenHeight = localConfiguration.screenHeightDp.dp

    // calculates mascot size, so it doesn't appear too large on wider screens
    val imageWidth = screenWidth * 0.45f // calculating width of image as 45% of screen width
    val imageHeight: Dp = if (screenWidth > 550.dp) 280.dp // sets imageHeight to 280.dp, so mascot doesn't appear too large on screens with screenWidth > 550.dp
    else screenHeight/3
    Log.d("MASCOT_SIZE_DEBUG", "screenWidth = $screenWidth, imageHeight = $imageHeight")

    //Variables for dynamic mascot
    val defaultIconId = R.drawable.solsikke_sol
    val snowyIconId = R.drawable.solsikke_snoo
    val rainyIconId = R.drawable.solsikke_regn
    val sunnyIconId = R.drawable.solsikke_sol

    var iconId = defaultIconId

    if (locationForecast.properties.timeseries.isNotEmpty()) {
        if (locationForecast.properties.timeseries[0].data.next_1_hours?.summary?.symbol_code?.contains("rain")!!) {
            iconId = rainyIconId
        } else if (locationForecast.properties.timeseries[0].data.instant.details.air_temperature.toInt() > 0) {
            iconId = sunnyIconId
        } else if (locationForecast.properties.timeseries[0].data.instant.details.air_temperature.toInt() <= 0) {
            iconId = snowyIconId
        }
    }

    // state, manages x, y z position/degrees (using Animatables)
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }

    val scope = rememberCoroutineScope()
    Image(
        painter = painterResource(id = iconId),
        contentDescription = "Mascot - representing current weather",
        modifier = Modifier
            .width(imageWidth)
            .height(imageHeight)
            .padding(bottom = 88.dp)
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
                rotationZ = rotation.value
            }
            .clickable(
                indication = null, // disables ripple effect when clicked
                interactionSource = remember { MutableInteractionSource() } // must be included to disable ripple
            ) {
                scope.launch {
                    // play happy animation when good weather
                    if (iconId == sunnyIconId) {
                        // jump left
                        offsetX.animateTo(-75f, animationSpec = tween(300))
                        rotation.animateTo(-15f, animationSpec = tween(300))
                        offsetY.animateTo(-50f, animationSpec = tween(300))

                        // fall down
                        offsetY.animateTo(0f, animationSpec = tween(300))
                        rotation.animateTo(0f, animationSpec = tween(300))

                        // jump right
                        offsetX.animateTo(75f, animationSpec = tween(300))
                        rotation.animateTo(15f, animationSpec = tween(300))
                        offsetY.animateTo(-50f, animationSpec = tween(300))

                        // fall down
                        offsetY.animateTo(0f, animationSpec = tween(300))
                        rotation.animateTo(0f, animationSpec = tween(300))

                        // original position
                        offsetX.animateTo(0f, animationSpec = tween(300))
                        offsetY.animateTo(0f, animationSpec = tween(300))
                        rotation.animateTo(0f, animationSpec = tween(300))
                    } else {
                        // fall down slightly
                        offsetY.animateTo(25f, animationSpec = tween(450))
                        rotation.animateTo(-7.5f, animationSpec = tween(450))

                        // fall further down
                        offsetY.animateTo(50f, animationSpec = tween(450))
                        rotation.animateTo(7.5f, animationSpec = tween(450))

                        // fall to bottom
                        offsetY.animateTo(100f, animationSpec = tween(450))
                        rotation.animateTo(-10f, animationSpec = tween(450))

                        // move back to original position
                        offsetX.animateTo(0f, animationSpec = tween(450))
                        offsetY.animateTo(0f, animationSpec = tween(450))
                        rotation.animateTo(0f, animationSpec = tween(450))
                    }
                }
            }
    )
}

@Composable
fun WeatherCard(locationForecast: LocationForecast, areaName: String, feelsLike: Double, timezoneId: String, onOverflow: (String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
        ) {
            Row (verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Place,
                    contentDescription = "Current location icon",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(30.dp)
                )
                Text(
                    text = areaName,
                    style = MaterialTheme.typography.headlineLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis, // ellipsis to indicate that the text has overflowed.
                    onTextLayout = { textLayoutResult -> // only show first part of areaName if text is overflowing
                        if (textLayoutResult.hasVisualOverflow) {
                            onOverflow(areaName.split(",").first())
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.padding(4.dp))

            // check if timeseries is not empty before accessing its elements
            if (locationForecast.properties.timeseries.isNotEmpty()) {
                Row (modifier = Modifier){
                    Text(
                        text = "${locationForecast.properties.timeseries[0].data.instant.details.air_temperature.roundToInt()}°${locationForecast.properties.meta.units.air_temperature.uppercase()[0]}",
                        fontSize = 86.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.weight(1f)) // spacer with Modifier.weight(1f) puts next composable furthermost right

                    Text(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .align(Alignment.Top),
                        text = "Feels like ${feelsLike.roundToInt()}°${locationForecast.properties.meta.units.air_temperature.uppercase()[0]}",
                        fontSize = 20.sp,
                    )
                }

                val symbolCode = locationForecast.properties.timeseries[0].data.next_1_hours?.summary?.symbol_code!!
                val forecastStatus = MetAPIDisplayUtils.symbolCodeToText(symbolCode)
                Row {
                    Column {
                        Text(
                            text = forecastStatus,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.padding(2.dp))

                        // format current time
                        val currentTimeFormat = SimpleDateFormat("MMMM dd, HH:mm", Locale.getDefault())
                        currentTimeFormat.timeZone = TimeZone.getTimeZone(timezoneId) // set timezone based on timezoneId
                        val currentTime = currentTimeFormat.format(Date())
                        Text(
                            text = currentTime,
                            fontSize = 20.sp,
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f)) // spacer with Modifier.weight(1f) puts next composable furthermost right

                    // todo (remove because of wcag?)
                    var weatherIcon: Int = R.drawable.solsikke_fjes
                    if (locationForecast.properties.timeseries.isNotEmpty()) {
                        val forecastData = locationForecast.properties.timeseries[0].data
                        if (forecastData.next_1_hours != null)         weatherIcon = MetAPIDisplayUtils.getWeatherIcon(forecastData.next_1_hours.summary.symbol_code)
                        else if (forecastData.next_6_hours != null)    weatherIcon = MetAPIDisplayUtils.getWeatherIcon(forecastData.next_6_hours.summary.symbol_code)
                        else if (forecastData.next_12_hours != null)   weatherIcon = MetAPIDisplayUtils.getWeatherIcon(forecastData.next_12_hours.summary.symbol_code)
                    }
                    Image(
                        painter = painterResource(id = weatherIcon),
                        contentDescription = "Icon representing weather state",
                        modifier = Modifier
                            .requiredWidth(width = 74.dp)
                            .requiredHeight(height = 74.dp)
                    )
                }
            } else {
                Text(
                    text = "No temperature data available",
                    fontSize = 20.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DangerCardPreview() {
    SunflowerTheme {
        val testAlert = Alert(
            Geometry(emptyList(), ""),
            properties = Properties(
                MunicipalityId = "",
                administrativeId = "",
                altitude_above_sea_level = 0,
                area = "Indre Skagerrak",
                awarenessResponse = "Følg med",
                awarenessSeriousness = "Utfordrende situasjon",
                awareness_level = "2; yellow; Moderate",
                awareness_type = "1; Wind",
                ceiling_above_sea_level = 274,
                certainty = "Likely",
                consequences = "Middels høye bølger. Bølgekammene er ved å brytes opp til sjørokk.",
                contact = "https://www.met.no/kontakt-oss",
                county = emptyList(), // Assuming empty county list for now
                description = "Nordøst sterk kuling 20 m/s.",
                event = "gale",
                eventAwarenessName = "Kuling",
                eventEndingTime = "2024-03-11T15:00:00+00:00",
                geographicDomain = "marine",
                incidentName = "",
                instruction = "Ikke dra ut i småbåt.",
                resources = listOf(
                    Resource(
                        description = "CAP file",
                        mimeType = "application/xml",
                        uri = "https://api.met.no/weatherapi/metalerts/2.0/test?cap=2.49.0.1.578.0.240311100000.0818_1_0"
                    )
                ),
                riskMatrixColor = "Yellow",
                severity = "Moderate",
                status = "Actual",
                title = "Kuling, Ytre Skagerrak, 11 mars 10:00 UTC til 11 mars 15:00 UTC. ",
                triggerLevel = "17.2 m/s",
                type = "Update",
                web = "https://www.met.no/vaer-og-klima/Ekstremvaervarsler-og-andre-farevarsler",
                id = "2.49.0.1.578.0.240311100000.0818_1_0",
                municipality = emptyList(),
            ),
            "",
            When(interval = listOf("2024-03-11T10:00:00+00:00", "2024-03-11T15:00:00+00:00"))
        )
        DangerCard(testAlert)
    }
}

//Preview of WeatherCard composable using dummy-data
@Preview(showBackground = true)
@Composable
fun WeatherCardPreview() {
    SunflowerTheme {
        // making a preview
        val locationForecast = LocationForecast(
            type = "Some Type",
            geometry = no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Geometry(
                "Some Type",
                emptyList()
            ),
            properties = no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Properties(
                Meta(
                    "Some Time",
                    MetaUnits(
                        "",
                        "C",
                        "",
                        "",
                        "",
                        "",
                        ""
                    )
                ),
                listOf(
                    no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Timeseries(
                        time = "2024-03-18T17:00:00Z",
                        data = TimeseriesData(
                            instant = Instant(
                                details = InstantData(
                                    air_pressure_at_sea_level = 0.0,
                                    air_temperature = 24.4,
                                    cloud_area_fraction = 20.0,
                                    relative_humidity = 2.0,
                                    wind_from_direction = 1.2,
                                    wind_speed = 0.8


                                )
                            ),
                            NextXHours(
                                NextXHoursSummary("cloudy"),
                                NextXHoursDetails(2.2)
                            ),
                            NextXHours(
                                NextXHoursSummary("cloudy"),
                                NextXHoursDetails(4.2)
                            ),
                            NextXHours(
                                NextXHoursSummary("cloudy"),
                                NextXHoursDetails(6.2)
                            ),
                        )
                    )
                )
            )
        )
        WeatherCard(locationForecast, "Area name", 20.0, "UTC", onOverflow = { })
    }
}