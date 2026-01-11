package com.aarevalo.parking.feature.map.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        GoogleMap(modifier = Modifier.fillMaxSize()) {
            // TODO: render downtown Vancouver meters and sort/filter by cost
        }
        // If you don’t have a Maps API key yet, the map will show a blank grid at runtime.
        Text(text = "Map", style = MaterialTheme.typography.labelLarge)
    }
}

