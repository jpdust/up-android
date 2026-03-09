package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unstampedpages.app.data.model.Country
import com.unstampedpages.app.ui.theme.Primary
import com.unstampedpages.app.ui.theme.PrimaryDark
import com.unstampedpages.app.ui.theme.Secondary
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailSheet(
    country: Country,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header with country image/gradient
            CountryHeader(country = country, onClose = onDismiss)

            // Country details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Population
                InfoRow(
                    icon = Icons.Default.Groups,
                    label = "Population",
                    value = formatPopulation(country.population)
                )

                Divider(color = Primary.copy(alpha = 0.1f))

                // Safety Level
                InfoRow(
                    icon = Icons.Default.Shield,
                    label = "Safety Level",
                    value = country.safetyLevel.displayName,
                    valueColor = country.safetyLevel.color
                )

                Divider(color = Primary.copy(alpha = 0.1f))

                // Currency
                InfoRow(
                    icon = Icons.Default.AttachMoney,
                    label = "Currency",
                    value = "${country.currency} (${country.currencyCode})"
                )

                // Exchange rate (1 USD = X foreign currency)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Secondary.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1 USD =",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Primary
                        )
                        val foreignPerUsd = if (country.exchangeRateToUSD > 0) {
                            1.0 / country.exchangeRateToUSD
                        } else {
                            0.0
                        }
                        Text(
                            text = "${String.format(Locale.US, "%.2f", foreignPerUsd)} ${country.currencyCode}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                }

                Divider(color = Primary.copy(alpha = 0.1f))

                // Outlet Type
                InfoRow(
                    icon = Icons.Default.ElectricalServices,
                    label = "Power Outlet",
                    value = country.outletType
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CountryHeader(
    country: Country,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryDark,
                        Primary
                    )
                )
            )
    ) {
        // Close button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }

        // Country info
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Flag emoji
            Text(
                text = country.flagEmoji,
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Country name
            Text(
                text = country.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            // Continent
            Text(
                text = country.continent.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = Secondary
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Secondary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Primary.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = valueColor
            )
        }
    }
}

private fun formatPopulation(population: Long): String {
    return when {
        population >= 1_000_000_000 -> String.format(Locale.US, "%.2f billion", population / 1_000_000_000.0)
        population >= 1_000_000 -> String.format(Locale.US, "%.1f million", population / 1_000_000.0)
        else -> NumberFormat.getNumberInstance(Locale.US).format(population)
    }
}
