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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricalServices
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unstampedpages.app.data.model.Country
import com.unstampedpages.app.ui.theme.Primary
import com.unstampedpages.app.ui.theme.PrimaryDark
import com.unstampedpages.app.ui.theme.Secondary
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
                    .testTag("bottom_sheet_scrim")
            )
        }

        // Bottom Sheet Content
        AnimatedVisibility(
            visible = visible,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("country_detail_sheet"),
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
                                .padding(24.dp)
                                .testTag("country_details_content"),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Safety Level
                            InfoRow(
                                icon = Icons.Default.Shield,
                                label = "Safety Level",
                                value = it.safetyLevel.displayName,
                                valueColor = it.safetyLevel.color,
                                testTag = "info_safety_level"
                            )

                            Divider(color = Primary.copy(alpha = 0.1f))

                            // Entry Requirement
                            InfoRow(
                                icon = Icons.Default.Badge,
                                label = "Entry Requirement",
                                value = it.visaRequirement.displayName,
                                testTag = "info_entry_requirement"
                            )

                            Divider(color = Primary.copy(alpha = 0.1f))

                            // Currency
                            InfoRow(
                                icon = Icons.Default.AttachMoney,
                                label = "Currency",
                                value = "${it.currency} (${it.currencyCode})",
                                testTag = "info_currency"
                            )

                            // Currency converter - hide for USD countries
                            if (it.currencyCode != "USD") {
                                CurrencyConverter(
                                    exchangeRateToUSD = it.exchangeRateToUSD,
                                    currencyCode = it.currencyCode
                                )

                                Divider(color = Primary.copy(alpha = 0.1f))
                            }

                            // Outlet Type
                            InfoRow(
                                icon = Icons.Default.ElectricalServices,
                                label = "Power Outlet",
                                value = it.outletType,
                                testTag = "info_power_outlet"
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
            .testTag("country_header")
    ) {
        // Close button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .testTag("bottom_sheet_close_button")
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
                fontSize = 64.sp,
                modifier = Modifier.testTag("country_flag")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Country name
            Text(
                text = country.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("country_name")
            )

            // Continent
            Text(
                text = country.continent.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = Secondary,
                modifier = Modifier.testTag("country_continent")
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
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
                color = valueColor,
                modifier = Modifier.testTag("${testTag}_value")
            )
        }
    }
}

@Composable
private fun CurrencyConverter(
    exchangeRateToUSD: Double,
    currencyCode: String
) {
    // Calculate foreign currency per USD
    val foreignPerUsd = if (exchangeRateToUSD > 0) 1.0 / exchangeRateToUSD else 0.0

    // State for the input amounts
    var usdAmount by remember { mutableStateOf("1") }
    var foreignAmount by remember { mutableStateOf(String.format(Locale.US, "%.2f", foreignPerUsd)) }

    // Reset amounts when country changes (exchange rate changes)
    LaunchedEffect(exchangeRateToUSD) {
        usdAmount = "1"
        foreignAmount = String.format(Locale.US, "%.2f", foreignPerUsd)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("currency_converter"),
        colors = CardDefaults.cardColors(
            containerColor = Secondary.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // USD Input
            CurrencyInputField(
                value = usdAmount,
                onValueChange = { newValue ->
                    usdAmount = newValue
                    val usdValue = newValue.toDoubleOrNull() ?: 0.0
                    foreignAmount = if (usdValue > 0 && foreignPerUsd > 0) {
                        String.format(Locale.US, "%.2f", usdValue * foreignPerUsd)
                    } else {
                        "0.00"
                    }
                },
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "USD",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Primary
            )

            Text(
                text = "=",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Secondary
            )

            // Foreign Currency Input
            CurrencyInputField(
                value = foreignAmount,
                onValueChange = { newValue ->
                    foreignAmount = newValue
                    val foreignValue = newValue.toDoubleOrNull() ?: 0.0
                    usdAmount = if (foreignValue > 0 && exchangeRateToUSD > 0) {
                        String.format(Locale.US, "%.2f", foreignValue * exchangeRateToUSD)
                    } else {
                        "0.00"
                    }
                },
                modifier = Modifier.weight(1f)
            )

            Text(
                text = currencyCode,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Primary
            )
        }
    }
}

@Composable
private fun CurrencyInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Use TextFieldValue to control cursor position - always at the end
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }

    // Update textFieldValue when external value changes, cursor always at end
    LaunchedEffect(value) {
        if (textFieldValue.text != value) {
            textFieldValue = TextFieldValue(text = value, selection = TextRange(value.length))
        }
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            val newText = newValue.text
            // Only allow valid decimal input
            if (newText.isEmpty() || newText.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                // Always force cursor to the end
                textFieldValue = TextFieldValue(
                    text = newText,
                    selection = TextRange(newText.length)
                )
                onValueChange(newText)
            } else {
                // Invalid input - keep current text but force cursor to end
                textFieldValue = textFieldValue.copy(selection = TextRange(textFieldValue.text.length))
            }
        },
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Primary,
            textAlign = TextAlign.End
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        singleLine = true,
        cursorBrush = SolidColor(Secondary),
        modifier = modifier
            .background(
                color = Color.White.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    // Place cursor at the end when focused
                    textFieldValue = textFieldValue.copy(selection = TextRange(textFieldValue.text.length))
                }
            }
    )
}

