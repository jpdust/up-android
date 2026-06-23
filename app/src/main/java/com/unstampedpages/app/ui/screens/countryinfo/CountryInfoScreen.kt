package com.unstampedpages.app.ui.screens.countryinfo

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unstampedpages.app.R
import com.unstampedpages.app.data.model.Country
import com.unstampedpages.app.data.repository.CountryGeometryData
import com.unstampedpages.app.ui.theme.Primary
import androidx.compose.ui.focus.onFocusChanged
import com.unstampedpages.app.analytics.AppAnalytics
import com.unstampedpages.app.ui.theme.Secondary

@Composable
fun CountryInfoScreen(
    viewModel: CountryInfoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentLocale = LocalConfiguration.current.locales[0]
    LaunchedEffect(currentLocale) {
        viewModel.updateLocale(currentLocale)
    }
    val isMapLoaded by CountryGeometryData.isLoaded
    var showSheet by remember { mutableStateOf(false) }
    var showLegend by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    var selectedColorMode by remember { mutableStateOf(MapColorMode.DEFAULT) }
    var isSearchFocused by remember { mutableStateOf(false) }

    // Pre-compute state-derived values to reduce complexity
    val hasSearchResults = uiState.searchResults.isNotEmpty()
    val showInstructions = !showSheet && !hasSearchResults && !showLegend

    // Create a map of country ID to Country for efficient lookup
    val countriesMap = remember(uiState.countries) {
        uiState.countries.associateBy { it.id }
    }

    // Keep local copies for the exit animation — prevents content from disappearing
    // before the slide-out animation completes.
    var displayedCountry by remember { mutableStateOf(uiState.selectedCountry) }
    var displayedDisplayName by remember { mutableStateOf(uiState.selectedDisplayName) }

    // Update displayed values when a new country is selected (but not when cleared)
    uiState.selectedCountry?.let {
        displayedCountry = it
        displayedDisplayName = uiState.selectedDisplayName
    }

    BackHandler(enabled = showSheet) {
        viewModel.clearSelection()
        showSheet = false
    }

    BackHandler(enabled = isSearchFocused && !showSheet) {
        viewModel.clearSearch()
        focusManager.clearFocus()
    }

    // Lock orientation to portrait for this screen
    LockOrientationPortrait()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("countries_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search Bar Section
            CountrySearchBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onClearSearch = {
                    viewModel.clearSearch()
                    focusManager.clearFocus()
                },
                onFocusChanged = { focused ->
                    isSearchFocused = focused
                    if (focused) AppAnalytics.trackCountrySearchFocused()
                }
            )

            // World Map
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("world_map_container")
            ) {
                WorldMapCanvas(
                    selectedCountryId = uiState.selectedCountry?.id,
                    onCountryTapped = { countryId, displayName ->
                        countryId?.let {
                            viewModel.selectCountry(it, displayName)
                            focusManager.clearFocus()
                            viewModel.clearSearch()
                            showSheet = true
                            val countryName = countriesMap[it]?.name ?: it
                            AppAnalytics.trackCountrySelected(it, countryName, AppAnalytics.SOURCE_MAP)
                        }
                    },
                    colorMode = selectedColorMode,
                    countries = countriesMap,
                    legendConfig = MapLegendConfig(
                        showLegend = showLegend,
                        onCompassTapped = {
                            showLegend = true
                            AppAnalytics.trackMapLegendOpened()
                        },
                        onLegendClose = {
                            showLegend = false
                            AppAnalytics.trackMapLegendClosed()
                        }
                    ),
                    gestureCallbacks = MapGestureCallbacks(
                        onZoomGestureEnd = { zoomedIn, zoomLevel ->
                            AppAnalytics.trackMapZoomed(zoomedIn, zoomLevel)
                        },
                        onPanGestureEnd = { direction ->
                            AppAnalytics.trackMapPanned(direction)
                        }
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("world_map")
                )

                // Instructions overlay
                if (showInstructions) {
                    Text(
                        text = stringResource(R.string.country_map_instructions),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    )
                }

                if (!isMapLoaded) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                            .testTag("world_map_loading"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = Secondary)
                        Text(
                            text = stringResource(R.string.country_map_loading),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        )
                    }
                }
            }

            // Map Color Mode Selection
            MapColorModeSelector(
                selectedMode = selectedColorMode,
                onModeSelected = { mode ->
                    selectedColorMode = mode
                    trackMapColorModeSelected(mode)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("map_color_mode_selector")
            )
        }

        // Autocomplete Dropdown - overlays on top of map
        SearchResultsDropdown(
            searchResults = uiState.searchResults,
            onCountrySelected = { suggestion ->
                val displayName = if (suggestion.parentCountryName != null) suggestion.displayName else null
                viewModel.selectCountry(suggestion.country.id, displayName)
                viewModel.clearSearch()
                focusManager.clearFocus()
                showSheet = true
                AppAnalytics.trackCountrySelected(
                    suggestion.country.id,
                    suggestion.country.name,
                    AppAnalytics.SOURCE_SEARCH
                )
            }
        )

        // Country Detail Bottom Sheet - overlays everything
        CountryDetailSheet(
            country = displayedCountry,
            displayName = displayedDisplayName,
            visible = showSheet,
            onDismiss = {
                showSheet = false
                viewModel.clearSelection()
            },
            analytics = CountrySheetAnalytics(
                onDismissed = {
                    displayedCountry?.let { c ->
                        AppAnalytics.trackCountryDetailDismissed(c.id, c.name)
                    }
                },
                onUsAdvisoryTapped = {
                    displayedCountry?.let { c -> AppAnalytics.trackUsAdvisoryOpened(c.id, c.name) }
                },
                onUkAdvisoryTapped = {
                    displayedCountry?.let { c -> AppAnalytics.trackUkAdvisoryOpened(c.id, c.name) }
                },
                onCaAdvisoryTapped = {
                    displayedCountry?.let { c -> AppAnalytics.trackCaAdvisoryOpened(c.id, c.name) }
                },
                onAuAdvisoryTapped = {
                    displayedCountry?.let { c -> AppAnalytics.trackAuAdvisoryOpened(c.id, c.name) }
                },
                onUsdFocused = {
                    displayedCountry?.let { c ->
                        AppAnalytics.trackUsdChanged(c.id, c.name, c.currencyCode)
                    }
                },
                onForeignFocused = {
                    displayedCountry?.let { c ->
                        AppAnalytics.trackForeignChanged(c.id, c.name, c.currencyCode)
                    }
                }
            )
        )
    }
}

internal fun trackMapColorModeSelected(mode: MapColorMode) {
    when (mode) {
        MapColorMode.DEFAULT           -> AppAnalytics.trackDefaultFilterSelected()
        MapColorMode.SECURITY_RISK     -> AppAnalytics.trackSecurityRiskFilterSelected()
        MapColorMode.VISA_REQUIREMENTS -> AppAnalytics.trackVisaRequirementsFilterSelected()
        MapColorMode.PASSPORT_VALIDITY -> AppAnalytics.trackPassportValidityFilterSelected()
        MapColorMode.YELLOW_FEVER      -> AppAnalytics.trackYellowFeverFilterSelected()
        MapColorMode.MALARIA           -> AppAnalytics.trackMalariaFilterSelected()
        MapColorMode.TRAFFIC_SIDE      -> AppAnalytics.trackTrafficSideFilterSelected()
    }
}

@Composable
private fun LockOrientationPortrait() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}

@Composable
private fun CountrySearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = {
            Text(
                text = stringResource(R.string.country_search_placeholder),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.cd_search),
                tint = Secondary
            )
        },
        trailingIcon = if (searchQuery.isNotEmpty()) {
            {
                IconButton(
                    onClick = onClearSearch,
                    modifier = Modifier.testTag("search_clear_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_clear),
                        tint = Primary.copy(alpha = 0.6f)
                    )
                }
            }
        } else null,
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
            .testTag("search_bar")
            .onFocusChanged { onFocusChanged(it.isFocused) }
    )
}

@Composable
private fun SearchResultsDropdown(
    searchResults: List<SearchSuggestion>,
    onCountrySelected: (SearchSuggestion) -> Unit
) {
    if (searchResults.isEmpty()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 72.dp)
            .heightIn(max = 200.dp)
            .zIndex(1f)
            .testTag("search_results_dropdown"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier.testTag("search_results_list")
        ) {
            items(searchResults) { suggestion ->
                CountrySearchItem(
                    suggestion = suggestion,
                    onClick = { onCountrySelected(suggestion) },
                    // Direct country matches keep the stable "search_result_{id}" tag used by
                    // existing robot helpers. Territory matches use their display name so that
                    // multiple territories of the same parent (e.g. two US territories) never
                    // share a tag and never collide with the parent's direct-match tag.
                    testTag = if (suggestion.parentCountryName == null)
                        "search_result_${suggestion.country.id}"
                    else
                        "search_result_territory_${suggestion.displayName.filter { it.isLetterOrDigit() }}"
                )
            }
        }
    }
}

@Composable
private fun CountrySearchItem(
    suggestion: SearchSuggestion,
    onClick: () -> Unit,
    testTag: String = ""
) {
    val subtitleText = suggestion.parentCountryName
        ?: stringResource(suggestion.country.continent.displayNameResId)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag(testTag)
    ) {
        Text(
            text = "${suggestion.country.flagEmoji} ${suggestion.displayName}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitleText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun MapColorModeSelector(
    selectedMode: MapColorMode,
    onModeSelected: (MapColorMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val column1Modes = listOf(
        MapColorMode.DEFAULT,
        MapColorMode.SECURITY_RISK,
        MapColorMode.VISA_REQUIREMENTS,
        MapColorMode.PASSPORT_VALIDITY
    )
    val column2Modes = listOf(
        MapColorMode.YELLOW_FEVER,
        MapColorMode.MALARIA,
        MapColorMode.TRAFFIC_SIDE
    )

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
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.country_map_view),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .testTag("map_view_label")
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    column1Modes.forEach { mode ->
                        MapModeRow(mode, selectedMode, onModeSelected)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    column2Modes.forEach { mode ->
                        MapModeRow(mode, selectedMode, onModeSelected)
                    }
                }
            }
        }
    }
}

@Composable
private fun MapModeRow(
    mode: MapColorMode,
    selectedMode: MapColorMode,
    onModeSelected: (MapColorMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onModeSelected(mode) }
            .padding(vertical = 1.dp)
            .testTag("map_mode_${mode.name.lowercase()}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        RadioButton(
            selected = selectedMode == mode,
            onClick = { onModeSelected(mode) },
            colors = RadioButtonDefaults.colors(
                selectedColor = Secondary,
                unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
            modifier = Modifier.testTag("map_mode_radio_${mode.name.lowercase()}")
        )
        Text(
            text = stringResource(mode.displayNameResId),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
