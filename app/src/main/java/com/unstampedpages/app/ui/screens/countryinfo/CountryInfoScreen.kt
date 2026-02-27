package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unstampedpages.app.ui.theme.Primary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryInfoScreen(
    viewModel: CountryInfoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }

    // World Map
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
        if (uiState.selectedCountry == null) {
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

    // Country Detail Bottom Sheet with roll-up animation
    if (showSheet) {
        uiState.selectedCountry?.let { country ->
            CountryDetailSheet(
                country = country,
                sheetState = sheetState,
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showSheet = false
                            viewModel.clearSelection()
                        }
                    }
                }
            )
        }
    }
}
