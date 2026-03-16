package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

private const val ANIMATION_DURATION = 300

@Composable
fun CountryDetailSheet(
    country: Country?,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Scrim (dark overlay)
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(ANIMATION_DURATION)),
            exit = fadeOut(animationSpec = tween(ANIMATION_DURATION))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )
        }

        // Bottom Sheet Content
        AnimatedVisibility(
            visible = visible && country != null,
            enter = slideInVertically(
                animationSpec = tween(ANIMATION_DURATION),
                initialOffsetY = { it }
            ),
            exit = slideOutVertically(
                animationSpec = tween(ANIMATION_DURATION),
                targetOffsetY = { it }
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            country?.let {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Header with country image/gradient
                        CountryHeader(country = it, onClose = onDismiss)

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
                                value = formatPopulation(it.population)
                            )

                            Divider(color = Primary.copy(alpha = 0.1f))

                            // Safety Level
                            InfoRow(
                                icon = Icons.Default.Shield,
                                label = "Safety Level",
                                value = it.safetyLevel.displayName,
                                valueColor = it.safetyLevel.color
                            )

                            Divider(color = Primary.copy(alpha = 0.1f))

                            // Currency
                            InfoRow(
                                icon = Icons.Default.AttachMoney,
                                label = "Currency",
                                value = "${it.currency} (${it.currencyCode})"
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
                                    val foreignPerUsd = if (it.exchangeRateToUSD > 0) {
                                        1.0 / it.exchangeRateToUSD
                                    } else {
                                        0.0
                                    }
                                    Text(
                                        text = "${String.format(Locale.US, "%.2f", foreignPerUsd)} ${it.currencyCode}",
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
                                value = it.outletType
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
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
