package no.uio.ifi.in2000.team30.sunflower.ui.clock

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team30.sunflower.R
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Timeseries
import no.uio.ifi.in2000.team30.sunflower.ui.shared.SharedComposables.Companion.BottomNavigationBar
import no.uio.ifi.in2000.team30.sunflower.ui.shared.SharedComposables.Companion.LoadingContent
import no.uio.ifi.in2000.team30.sunflower.ui.shared.SharedComposables.Companion.SearchBox
import no.uio.ifi.in2000.team30.sunflower.utils.DateDisplayUtils
import no.uio.ifi.in2000.team30.sunflower.utils.MetAPIDisplayUtils

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
fun ClockScreen(viewModel: ClockScreenViewModel = ClockScreenViewModel(), navController: NavController, navLat: Double, navLon: Double){
    // observe viewmodel states
    val timeListGrouped by viewModel.timeListGrouped.collectAsState()
    val timeZoneId by viewModel.timeZoneId.collectAsState()
    val initializeCalled by viewModel.initializeCalled.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing: Boolean by viewModel.isRefreshing.collectAsState()
    val error by viewModel.error.collectAsState()
    val locationCoordinates by viewModel.locationCoordinates.collectAsState()
    val locationInfoUiState by viewModel.locationInfoUiState.collectAsState()
    val showScrollToTopButton by viewModel.showScrollToTopButton.collectAsState()
    val snackBarHostState by viewModel.snackBarHostState.collectAsState()
    val lazyListState = rememberLazyListState()
    val context: Context = LocalContext.current
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh(locationCoordinates.lat, locationCoordinates.lon, context) }
    )

    Log.d("ClockScreen - Lat Lon", "${locationCoordinates.lat}, ${locationCoordinates.lon}")

    // react to changes in nav coordinates from parameter
    LaunchedEffect(navLat, navLon) {
        if (!initializeCalled) {
            viewModel.initialize(navLat, navLon, context)
        } else if (navLat != locationCoordinates.lat && navLat != locationCoordinates.lon) {
            viewModel.refresh(navLat, navLon, context)
        }
    }

/* ui code */
    Scaffold (
        snackbarHost = { SnackbarHost(modifier = Modifier.padding(16.dp), hostState = snackBarHostState) },
        bottomBar = {
            BottomNavigationBar(navController = navController, lat = locationCoordinates.lat, lon = locationCoordinates.lon, navSource = "ClockScreen")
            if (error != null) {
                LaunchedEffect(snackBarHostState) {
                    val snackBarResult = snackBarHostState.showSnackbar(
                        message = "$error",
                        duration = SnackbarDuration.Indefinite,
                        withDismissAction = false,
                    )
                    if (snackBarResult == SnackbarResult.Dismissed) {
                        viewModel.refresh(navLat, navLon, context)
                    }
                }
            }
        },
        floatingActionButton = {
            Column {
                LaunchedEffect(lazyListState) {
                    snapshotFlow { lazyListState.firstVisibleItemIndex }
                        .collect { viewModel.updateScrollToTopButtonVisibility(it) }
                }
                AnimatedVisibility(visible = showScrollToTopButton) {
                    ScrollToTopButton(lazyListState)
                }
                SearchBox(navController, "ClockScreen")
            }
        }
    ) {
        if (isLoading) LoadingContent()
        else {
            Box(modifier = Modifier
                .pullRefresh(pullRefreshState)
                .background(Color.White)
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Row (verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Place,
                            contentDescription = "Current location icon",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(30.dp)
                        )
                        Text(
                            text = locationInfoUiState.areaName,
                            style = MaterialTheme.typography.headlineLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis, // ellipsis to indicate that the text has overflowed.
                            onTextLayout = { textLayoutResult -> // only show first part of areaName if text is overflowing
                                if (textLayoutResult.hasVisualOverflow) {
                                    viewModel.updateAreaName(locationInfoUiState.areaName.split(",").first())
                                }
                            }
                        )
                    }

                    Box(modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .height(1000.dp)
                    ) {
                        LazyColumn(
                            state = lazyListState,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .heightIn(0.dp, 1000.dp)
                        ) {
                            timeListGrouped.groupedTimeList.forEach { (date, timeseriesList) ->
                                stickyHeader {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White)
                                    ) {

                                        Spacer(modifier = Modifier.padding(4.dp))

                                        Text(
                                            text = date,
                                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.displaySmall,
                                        )
                                    }
                                }

                                items(timeseriesList.size) { index ->
                                    // selects appropriate time and icon to display (ui code)
                                    var formattedTime = DateDisplayUtils.parseAndFormatDate(timeseriesList[index].time, "UTC", "yyyy-MM-dd'T'HH:mm:ssXXX", timeZoneId, "HH:mm")

                                    var weatherIcon: Int = R.drawable.solsikke_fjes
                                    val forecastData = timeseriesList[index].data
                                    if (forecastData.next_1_hours != null) {
                                        weatherIcon = MetAPIDisplayUtils.getWeatherIcon(forecastData.next_1_hours.summary.symbol_code)
                                    } else if (forecastData.next_6_hours != null) {
                                        weatherIcon = MetAPIDisplayUtils.getWeatherIcon(forecastData.next_6_hours.summary.symbol_code)
                                        formattedTime = DateDisplayUtils.formatHourDisplayForNextHours(formattedTime, 6)
                                    } else if (forecastData.next_12_hours != null) {
                                        weatherIcon = MetAPIDisplayUtils.getWeatherIcon(forecastData.next_12_hours.summary.symbol_code)
                                        formattedTime = DateDisplayUtils.formatHourDisplayForNextHours(formattedTime, 12)
                                    }
                                    HourViewCard(
                                        timeseries = timeseriesList[index],
                                        date = date,
                                        formattedTime = formattedTime,
                                        weatherIcon = weatherIcon,
                                        navController = navController
                                    )
                                }
                            }
                        }
                    }
                }
                PullRefreshIndicator(isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
            }
        }
    }
}

@Composable
fun HourViewCard(
    timeseries: Timeseries,
    date: String,
    formattedTime: String,
    weatherIcon: Int,
    navController: NavController,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(215, 215, 215, 150),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 6.dp)
            .clickable {
                navController.navigate("details/${timeseries.time},${weatherIcon},${date}_${formattedTime}")
            }
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formattedTime,
                color = Color.Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold
            )
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "temp",
                    color = Color.Black,
                    fontSize = 18.sp,
                )
                Text(
                    text = "${timeseries.data.instant.details.air_temperature.toInt()}°C",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "wind",
                    color = Color.Black,
                    fontSize = 18.sp,
                )
                Text(
                    text = "${timeseries.data.instant.details.wind_speed} m/s",
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Image(
                painter = painterResource(id = weatherIcon),
                contentDescription = "Icon representing weather state",
                modifier = Modifier
                    .requiredWidth(width = 74.dp)
                    .requiredHeight(height = 74.dp)
            )
        }
    }
}

@Composable
fun ScrollToTopButton(lazyListState: LazyListState) {
    val scope = rememberCoroutineScope()
    FloatingActionButton(
        onClick = {
            scope.launch {
                lazyListState.animateScrollToItem(0)
            }
        },
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
        shape = FloatingActionButtonDefaults.largeShape,
        containerColor = Color(207, 56, 233, 238),
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = "Scroll to top",
            modifier = Modifier.size(28.dp)
        )
    }
}