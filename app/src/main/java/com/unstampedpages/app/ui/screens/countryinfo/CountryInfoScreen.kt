package com.unstampedpages.app.ui.screens.countryinfo

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unstampedpages.app.ui.theme.Primary

@Composable
fun CountryInfoScreen(
    viewModel: CountryInfoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSheet by remember { mutableStateOf(false) }

    // Keep a local copy of the country for the exit animation
    // This prevents the content from disappearing before the animation completes
    var displayedCountry by remember { mutableStateOf(uiState.selectedCountry) }

    // Update displayed country when a new one is selected (but not when cleared)
    if (uiState.selectedCountry != null) {
        displayedCountry = uiState.selectedCountry
    }

    // Lock orientation to portrait for this screen
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // World Map
        WorldMapCanvas(
            selectedCountryId = uiState.selectedCountry?.id,
            onCountryTapped = { countryId ->
                if (countryId != null) {
                    viewModel.selectCountry(countryId)
                    showSheet = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Instructions overlay
        if (!showSheet) {
            Text(
                text = "Pinch to zoom • Drag to pan",
                style = MaterialTheme.typography.labelSmall,
                color = Primary.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }

        // Country Detail Bottom Sheet
        CountryDetailSheet(
            country = displayedCountry,
            visible = showSheet,
            onDismiss = {
                showSheet = false
                viewModel.clearSelection()
            }
        )
    }
}
