package no.uio.ifi.in2000.team30.sunflower.ui.clock

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team30.sunflower.model.locationforecast.Timeseries
import no.uio.ifi.in2000.team30.sunflower.utils.MetAPIDisplayUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailsScreen(navController: NavController, timeseries: Timeseries, weatherIcon: Int, date: String, formattedTime: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$date: $formattedTime") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp) // Add padding for content
                .verticalScroll(rememberScrollState()) // Add vertical scrolling
        ) {
            val weatherDetails = timeseries.data.instant

            // Blue box with title
            Box(
                modifier = Modifier
                    .height(80.dp) // Increase the height
                    .fillMaxWidth()
                    .background(Color.Blue)
            ) {
                // Text for weather details
                Text(
                    text = "WeatherDetails", // Convert the object to a string
                    color = Color.White,
                    lineHeight = 30.sp,
                    fontSize = 24.sp, // Increase font size
                    modifier = Modifier.padding(
                        vertical = 16.dp,
                        horizontal = 24.dp
                    ) // Adjust padding
                )
            }

            // Display weather details in a column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // Increase spacer height

                Text(
                    text = "Air pressure at sea level: ${weatherDetails.details.air_pressure_at_sea_level}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp), // Increase font size
                    modifier = Modifier.padding(bottom = 16.dp) // Increase bottom padding
                )

                Text(
                    text = "Air temperature: ${weatherDetails.details.air_temperature}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp), // Increase font size
                    modifier = Modifier.padding(bottom = 16.dp) // Increase bottom padding
                )

                Text(
                    text = "Cloud area fraction: ${weatherDetails.details.cloud_area_fraction}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp), // Increase font size
                    modifier = Modifier.padding(bottom = 16.dp) // Increase bottom padding
                )

                Text(
                    text = "Relative humidity: ${weatherDetails.details.relative_humidity}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp), // Increase font size
                    modifier = Modifier.padding(bottom = 16.dp) // Increase bottom padding
                )

                Text(
                    text = "Wind from direction: ${weatherDetails.details.wind_from_direction}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp), // Increase font size
                    modifier = Modifier.padding(bottom = 16.dp) // Increase bottom padding
                )

                Text(
                    text = "Wind speed: ${weatherDetails.details.wind_speed}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp), // Increase font size
                    modifier = Modifier.padding(bottom = 16.dp) // Increase bottom padding
                )
                Image(
                    painter = painterResource(id = weatherIcon),
                    contentDescription = "Icon representing weather state",
                    modifier = Modifier
                        .requiredWidth(width = 74.dp)
                        .requiredHeight(height = 74.dp)
                )
                var symbolCodeText = "Unknown weather status"
                val nextXHoursData = timeseries.data
                if (nextXHoursData.next_1_hours != null)         symbolCodeText = MetAPIDisplayUtils.symbolCodeToText(nextXHoursData.next_1_hours.summary.symbol_code)
                else if (nextXHoursData.next_6_hours != null)    symbolCodeText = MetAPIDisplayUtils.symbolCodeToText(nextXHoursData.next_6_hours.summary.symbol_code)
                else if (nextXHoursData.next_12_hours != null)   symbolCodeText = MetAPIDisplayUtils.symbolCodeToText(nextXHoursData.next_12_hours.summary.symbol_code)
                Text(
                    text = symbolCodeText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp), // Increase font size
                    modifier = Modifier.padding(bottom = 16.dp) // Increase bottom padding
                )
            }
        }
    }
}



