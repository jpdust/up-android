package com.unstampedpages.app.ui.screens.countryinfo

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unstampedpages.app.data.model.Country
import com.unstampedpages.app.ui.theme.Primary
import com.unstampedpages.app.ui.theme.Secondary

@Composable
fun CountryInfoScreen(
    viewModel: CountryInfoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    var selectedColorMode by remember { mutableStateOf(MapColorMode.DEFAULT) }

    // Create a map of country ID to Country for efficient lookup
    val countriesMap = remember(uiState.countries) {
        uiState.countries.associateBy { it.id }
    }

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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search Bar Section
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = {
                    Text(
                        text = "Search for a country/territory...",
                        color = Primary.copy(alpha = 0.5f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Secondary
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.clearSearch()
                            focusManager.clearFocus()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Primary.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Secondary,
                    unfocusedBorderColor = Primary.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // World Map
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                WorldMapCanvas(
                    selectedCountryId = uiState.selectedCountry?.id,
                    onCountryTapped = { countryId ->
                        if (countryId != null) {
                            viewModel.selectCountry(countryId)
                            focusManager.clearFocus()
                            viewModel.clearSearch()
                            showSheet = true
                        }
                    },
                    colorMode = selectedColorMode,
                    countries = countriesMap,
                    modifier = Modifier.fillMaxSize()
                )

                // Instructions overlay
                if (!showSheet && uiState.searchResults.isEmpty()) {
                    Text(
                        text = "Pinch to zoom • Drag to pan",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    )
                }
            }

            // Map Color Mode Selection
            MapColorModeSelector(
                selectedMode = selectedColorMode,
                onModeSelected = { selectedColorMode = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Autocomplete Dropdown - overlays on top of map
        if (uiState.searchResults.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 72.dp)
                    .heightIn(max = 200.dp)
                    .zIndex(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                LazyColumn {
                    items(uiState.searchResults) { country ->
                        CountrySearchItem(
                            country = country,
                            onClick = {
                                viewModel.selectCountry(country.id)
                                viewModel.clearSearch()
                                focusManager.clearFocus()
                                showSheet = true
                            }
                        )
                    }
                }
            }
        }

        // Country Detail Bottom Sheet - overlays everything
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

@Composable
private fun CountrySearchItem(
    country: Country,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "${country.flagEmoji} ${country.name}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Primary
        )
        Text(
            text = country.continent.displayName,
            style = MaterialTheme.typography.bodySmall,
            color = Primary.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun MapColorModeSelector(
    selectedMode: MapColorMode,
    onModeSelected: (MapColorMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Map View",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            MapColorMode.entries.forEach { mode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onModeSelected(mode) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = selectedMode == mode,
                        onClick = { onModeSelected(mode) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Secondary,
                            unselectedColor = Primary.copy(alpha = 0.5f)
                        )
                    )
                    Text(
                        text = mode.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Primary
                    )
                }
            }
        }
    }
}
