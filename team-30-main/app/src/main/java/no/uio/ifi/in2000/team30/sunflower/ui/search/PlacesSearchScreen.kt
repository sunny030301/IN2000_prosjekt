package no.uio.ifi.in2000.team30.sunflower.ui.search

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesSearchScreen(viewModel: PlacesViewModel = viewModel(), navController: NavController, navSource: String?) {
    // collect state of places list from viewmodel
    val placesList by viewModel.placesList.collectAsState()

    // collect state of search input value from viewmodel
    val searchInputValue by viewModel.searchInputValue.collectAsState()

    // collect state of selected place details from viewmodel
    val selectedPlaceDetails by viewModel.selectedPlaceDetails.collectAsState()

    // other states
    val error by viewModel.error.collectAsState()
    val snackBarHostState by viewModel.snackBarHostState.collectAsState()

    // extract the name, latitude, and longitude of the currently selected place
    val currentPlaceName: String = selectedPlaceDetails?.placeDetails?.result?.name ?: "No place selected"
    val currentPlaceLat: Double = selectedPlaceDetails?.placeDetails?.result?.geometry?.location?.lat ?: 0.0
    val currentPlaceLong: Double = selectedPlaceDetails?.placeDetails?.result?.geometry?.location?.lng ?: 0.0

    // access to android-keyboard, so that visibility can be controlled
    val keyboardController = LocalSoftwareKeyboardController.current

    viewModel.resetStates()

    // launch effect to handle navigation once coordinates are updated
    LaunchedEffect(currentPlaceLat, currentPlaceLong) {
        if (currentPlaceLat != 0.0 && currentPlaceLong != 0.0) {
            Log.d("SearchScreen navigation", "Attempting to navigate with lat: $currentPlaceLat, long: $currentPlaceLong")
            when (navSource) {
                "HomeScreen" -> {
                    keyboardController?.hide()
                    navController.navigate("home_screen/${currentPlaceLat},${currentPlaceLong}")
                }
                "ClockScreen" -> {
                    keyboardController?.hide()
                    navController.navigate("details/${currentPlaceLat},${currentPlaceLong}")
                }
                else -> {
                    Log.e("SearchScreen navigation error", "Unknown navSource. navSource: $navSource, currentPlaceLat: $currentPlaceLat, currentPlaceLong: $currentPlaceLong")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Location search")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(modifier = Modifier.padding(16.dp), hostState = snackBarHostState) },
        bottomBar = {
            if (error != null) {
                LaunchedEffect(snackBarHostState) {
                    snackBarHostState.showSnackbar(
                        message = "$error",
                        duration = SnackbarDuration.Indefinite,
                        withDismissAction = false,
                    )
                }
                keyboardController?.hide()
            }
        },
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface {
                Box (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextField (
                        value = searchInputValue, // current value of search input
                        onValueChange = {
                            viewModel.updatePlacesList(it)  // update search input value in viewmodel and trigger search operation
                        },
                        keyboardActions = KeyboardActions(
                            onDone = { }
                        ),
                        placeholder = {
                            Text(text = "Search for a location")
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        maxLines = 1,
                        shape = RoundedCornerShape(64.dp),
                        modifier = Modifier.fillMaxWidth(0.95f) // fill 95% of available width
                    )
                }
            }

            LazyColumn { // lazy loading list of places
                items(placesList.placeList.size) { place -> // iterate over each place in the list
                    if (placesList.placeList[place].name.isNotBlank()) {
                        // if secondary text is empty, do not show supportingContent
                        if (placesList.placeList[place].secondary_text.isEmpty()) {
                            ListItem (
                                headlineContent = { Text(text = placesList.placeList[place].main_text) }, // display place name
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.Place,
                                        contentDescription = "Place icon"
                                    )
                                },
                                trailingContent = { Text(placesList.placeList[place].types[0].replace("_", " ")) },
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .clickable {
                                        viewModel.getDetailsOfSelectedPlace(placesList.placeList[place]) // get details of selected place
                                    },
                            )
                        } else { // if secondary text has value, show it in supportingContent
                            ListItem (
                                headlineContent = { Text(text = placesList.placeList[place].main_text) }, // display place name
                                supportingContent = { Text(text = placesList.placeList[place].secondary_text) },
                                leadingContent = {
                                    Icon(
                                        imageVector = Icons.Rounded.Place,
                                        contentDescription = "Place icon"
                                    )
                                },
                                trailingContent = { Text(placesList.placeList[place].types[0].replace("_", " ")) },
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .clickable {
                                        viewModel.getDetailsOfSelectedPlace(placesList.placeList[place]) // get details of selected place
                                    },
                            )
                        }
                    }
                }
            }
            Log.d("PlacesSearchScreen selected place details",
                "Selected place name: $currentPlaceName\n" + // text displaying selected place details
                    "Selected place lat: $currentPlaceLat\n" + // latitude of selected place
                    "Selected place long: $currentPlaceLong\n" + // longitude of selected place
                    "Selected place ID: ${selectedPlaceDetails?.placeDetails?.result?.place_id ?: "Unknown place id"}" // id of selected place
            )
        }
    }
}