package no.uio.ifi.in2000.team30.sunflower.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team30.sunflower.R

class SharedComposables {
    companion object {
        @Composable
        fun BottomNavigationBar(navController: NavController, modifier: Modifier = Modifier, lat: Double?, lon: Double?, navSource: String) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((-66).dp),
                modifier = modifier
                    .padding(bottom = 14.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(64.dp))
                        .fillMaxWidth(0.9f)
                        .height(60.dp)
                        .background(Color(0, 0, 0, 40))
                )
                NavigationBar(
                    containerColor = Color(0xfffdfdfd),
                    modifier = Modifier
                        .clip(RoundedCornerShape(64.dp))
                        .fillMaxWidth(0.9f)
                        .heightIn(0.dp, 60.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(72.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier
                            .fillMaxWidth()
                    ) {
                        // if source is itself, do not do anything onClick
                        if (navSource == "HomeScreen") {
                            IconButton(
                                onClick = {  },
                                content = { Icon(Icons.Filled.Home, contentDescription = "Go to home screen", modifier = Modifier.size(32.dp)) },
                                modifier = Modifier
                                    .requiredSize(size = 32.dp)
                            )
                        } else {
                            IconButton(
                                onClick = { navController.navigate("home_screen/${lat},${lon}") },
                                content = { Icon(Icons.Filled.Home, contentDescription = "Go to home screen", modifier = Modifier.size(32.dp)) },
                                modifier = Modifier
                                    .requiredSize(size = 32.dp)
                            )
                        }

                        // if source is itself, do not do anything onClick
                        if (navSource == "ClockScreen") {
                            IconButton(
                                onClick = {  },
                                content = { Icon(Icons.Filled.DateRange, contentDescription = "Go to weather details", modifier = Modifier.size(32.dp)) },
                                modifier = Modifier
                                    .requiredSize(size = 32.dp)
                            )
                        } else {
                            IconButton(
                                onClick = { navController.navigate("details/${lat},${lon}") },
                                content = { Icon(Icons.Filled.DateRange, contentDescription = "Go to weather details", modifier = Modifier.size(32.dp)) },
                                modifier = Modifier
                                    .requiredSize(size = 32.dp)
                            )
                        }
                    }
                }
            }
        }

        @Composable
        fun SearchBox(navController: NavController, navSource: String) {
            FloatingActionButton(
                onClick = {
                    navController.navigate("search/${navSource}")
                },
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                shape = FloatingActionButtonDefaults.largeShape,
                containerColor = Color(207, 56, 233, 238),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search icon",
                )
            }
        }

        @Composable
        fun LoadingContent() {
            Column(modifier = Modifier.fillMaxSize().offset(y = (-60).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Sunflower",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.padding(14.dp))
                Image(
                    painter = painterResource(id = R.drawable.solsikke_fjes),
                    contentDescription = "Background showing current weather",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.BottomCenter,
                )
                Spacer(modifier = Modifier.padding(30.dp))
                // show a loading indicator
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }

    }
}