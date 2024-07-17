package no.uio.ifi.in2000.team30.sunflower

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team30.sunflower.ui.clock.ClockScreen
import no.uio.ifi.in2000.team30.sunflower.ui.clock.ClockScreenViewModel
import no.uio.ifi.in2000.team30.sunflower.ui.clock.WeatherDetailsScreen
import no.uio.ifi.in2000.team30.sunflower.ui.home.HomeScreen
import no.uio.ifi.in2000.team30.sunflower.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.team30.sunflower.ui.search.PlacesSearchScreen
import no.uio.ifi.in2000.team30.sunflower.ui.search.PlacesViewModel
import no.uio.ifi.in2000.team30.sunflower.ui.theme.SunflowerTheme

class MainActivity : ComponentActivity() {
    private val homeScreenViewModel: HomeScreenViewModel by viewModels()
    private val clockScreenViewModel: ClockScreenViewModel by viewModels()
    private val searchViewModel: PlacesViewModel by viewModels()
    @SuppressLint("MissingPermission", "StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SunflowerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home_screen") {
                        composable("home_screen") {
                            HomeScreen(homeScreenViewModel, navController)
                        }

                        composable("home_screen/{lat},{lon}") {backStackEntry ->
                            val lat = backStackEntry.arguments?.getString("lat")?.toDouble()
                            val lon = backStackEntry.arguments?.getString("lon")?.toDouble()
                            HomeScreen(homeScreenViewModel, navController, lat!!, lon!!)
                            // lat & lon can't be null, as NumberFormatException will occur if conversion to double fails
                        }

                        composable("details/{lat},{lon}") {backStackEntry ->
                            val lat = backStackEntry.arguments?.getString("lat")?.toDouble()
                            val lon = backStackEntry.arguments?.getString("lon")?.toDouble()
                            ClockScreen(clockScreenViewModel, navController = navController, lat!!, lon!!)
                            // lat & lon can't be null, as NumberFormatException will occur if conversion to double fails
                        }

                        composable("details/{time},{weatherIcon},{date}_{formattedTime}") { backStackEntry ->
                            val time = backStackEntry.arguments?.getString("time")
                            val timeseries = clockScreenViewModel.timeListDataState.value.timeList.firstOrNull { it.time == time }
                            val weatherIcon = backStackEntry.arguments?.getString("weatherIcon")?.toInt()
                            val date = backStackEntry.arguments?.getString("date")
                            val formattedTime = backStackEntry.arguments?.getString("formattedTime")
                            timeseries?.let {
                                WeatherDetailsScreen(navController, it, weatherIcon!!, date!!, formattedTime!!)
                            }
                        }

                        composable("search/{navSource}") {backStackEntry ->
                            val navSource = backStackEntry.arguments?.getString("navSource")
                            PlacesSearchScreen(searchViewModel, navController = navController, navSource = navSource)
                        }
                    }
                }
            }
        }
        handleLocationPermission(homeScreenViewModel)
    }

    //RequestMultiplePermissions allows the system to manage the permission request code
    //ActivityResultCallback defines how the app handles the user's response to the permission request
    //return value of registerForActivityResult() which is of type ActivityResultLauncher.
    //launch() displays the system permissions dialog and then the user makes a choice
    //the system asynchronously invokes your implementation of ActivityResultCallback
    private fun handleLocationPermission(viewModel: HomeScreenViewModel){
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    viewModel.handlePermissionResult(true)
                    Log.d("LocationPermission", "Fine location permission granted")
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    viewModel.handlePermissionResult(true)
                    Log.d("LocationPermission", "Coarse location permission granted")

                } else -> {
                // No location access granted.
                    Log.d("LocationPermission", "Location permission denied")
                }
            }
        }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
