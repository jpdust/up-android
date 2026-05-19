package com.unstampedpages.app.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unstampedpages.app.R
import com.unstampedpages.app.ui.theme.Primary
import com.unstampedpages.app.ui.theme.PrimaryDark
import com.unstampedpages.app.ui.theme.Secondary
import com.unstampedpages.app.ui.theme.SecondaryLight
import com.unstampedpages.app.ui.theme.StencilFontFamily
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    onNavigateToCountries: () -> Unit = {},
    onNavigateToChecklist: () -> Unit = {},
    onNavigateToTripLog: () -> Unit = {},
    onNavigateToMyStamps: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image — fills the full screen and crops to fit any size/orientation
        Image(
            painter = painterResource(R.drawable.up),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark scrim overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Black.copy(alpha = 0.55f),
                            0.4f to Color.Black.copy(alpha = 0.45f),
                            1.0f to Color.Black.copy(alpha = 0.65f)
                        )
                    )
                )
        )

        // Scrollable content on top
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Compass Icon
            CompassIcon(modifier = Modifier.size(120.dp))

            // App Title - Stencil Font (auto-sized to fit width)
            AutoSizeTitle(
                text = stringResource(R.string.app_title),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.titleLarge,
                color = SecondaryLight,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Welcome Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.home_welcome),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.home_description),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Feature Cards
            FeatureCard(
                title = stringResource(R.string.feature_explore_title),
                description = stringResource(R.string.feature_explore_description),
                iconContent = { Icon(Icons.Filled.Explore, contentDescription = null, tint = Secondary) },
                onClick = onNavigateToCountries
            )

            FeatureCard(
                title = stringResource(R.string.feature_checklist_title),
                description = stringResource(R.string.feature_checklist_description),
                iconContent = { ChecklistIcon() },
                onClick = onNavigateToChecklist
            )

            FeatureCard(
                title = stringResource(R.string.feature_journal_title),
                description = stringResource(R.string.feature_journal_description),
                iconContent = { JournalIcon() },
                onClick = onNavigateToTripLog
            )

            FeatureCard(
                title = stringResource(R.string.feature_stamps_title),
                description = stringResource(R.string.feature_stamps_description),
                iconContent = { StampIcon() },
                onClick = onNavigateToMyStamps
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Inspirational Quote
            Text(
                text = stringResource(R.string.home_quote),
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.85f)
            )

            Text(
                text = stringResource(R.string.home_quote_author),
                style = MaterialTheme.typography.labelMedium,
                color = SecondaryLight.copy(alpha = 0.80f)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CompassIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(SecondaryLight, Secondary, Primary)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 8.dp.toPx()

            // Outer circle
            drawCircle(
                color = PrimaryDark,
                radius = radius,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // Compass points
            val directions = listOf(0f, 90f, 180f, 270f)
            directions.forEach { angle ->
                val rad = Math.toRadians(angle.toDouble() - 90)
                val startRadius = radius - 15.dp.toPx()
                val endRadius = radius - 5.dp.toPx()

                drawLine(
                    color = PrimaryDark,
                    start = Offset(
                        center.x + (startRadius * cos(rad)).toFloat(),
                        center.y + (startRadius * sin(rad)).toFloat()
                    ),
                    end = Offset(
                        center.x + (endRadius * cos(rad)).toFloat(),
                        center.y + (endRadius * sin(rad)).toFloat()
                    ),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // North arrow (red/accent)
            val northPath = Path().apply {
                moveTo(center.x, center.y - radius + 20.dp.toPx())
                lineTo(center.x - 8.dp.toPx(), center.y)
                lineTo(center.x, center.y - 10.dp.toPx())
                close()
            }
            drawPath(northPath, color = Color(0xFF8B2500))

            // South arrow
            val southPath = Path().apply {
                moveTo(center.x, center.y + radius - 20.dp.toPx())
                lineTo(center.x + 8.dp.toPx(), center.y)
                lineTo(center.x, center.y + 10.dp.toPx())
                close()
            }
            drawPath(southPath, color = PrimaryDark)

            // Center dot
            drawCircle(
                color = PrimaryDark,
                radius = 4.dp.toPx(),
                center = center
            )
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    iconContent: @Composable () -> Unit,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(modifier = Modifier.size(32.dp)) {
                iconContent()
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ChecklistIcon() {
    Canvas(modifier = Modifier.size(32.dp)) {
        val lineSpacing = size.height / 4

        for (i in 0..2) {
            val y = lineSpacing * (i + 0.5f)
            // Checkbox
            drawRect(
                color = Secondary,
                topLeft = Offset(0f, y - 4.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(8.dp.toPx(), 8.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )
            // Line
            drawLine(
                color = Primary,
                start = Offset(12.dp.toPx(), y),
                end = Offset(size.width, y),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
private fun JournalIcon() {
    Canvas(modifier = Modifier.size(32.dp)) {
        // Book cover
        drawRoundRect(
            color = Primary,
            topLeft = Offset(2.dp.toPx(), 0f),
            size = androidx.compose.ui.geometry.Size(size.width - 4.dp.toPx(), size.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
        )
        // Book spine
        drawLine(
            color = PrimaryDark,
            start = Offset(6.dp.toPx(), 2.dp.toPx()),
            end = Offset(6.dp.toPx(), size.height - 2.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )
        // Pages
        drawLine(
            color = Secondary,
            start = Offset(12.dp.toPx(), 8.dp.toPx()),
            end = Offset(size.width - 6.dp.toPx(), 8.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Secondary,
            start = Offset(12.dp.toPx(), 14.dp.toPx()),
            end = Offset(size.width - 6.dp.toPx(), 14.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Secondary,
            start = Offset(12.dp.toPx(), 20.dp.toPx()),
            end = Offset(size.width - 10.dp.toPx(), 20.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
    }
}

@Composable
private fun StampIcon() {
    Canvas(modifier = Modifier.size(32.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 - 2.dp.toPx()

        // Outer circle (stamp border)
        drawCircle(
            color = Secondary,
            radius = radius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        // Inner circle
        drawCircle(
            color = Secondary,
            radius = radius - 4.dp.toPx(),
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )

        // Stamp serrated edge effect (small lines around the circle)
        val numLines = 16
        for (i in 0 until numLines) {
            val angle = Math.toRadians((i * 360.0 / numLines))
            val innerRadius = radius - 1.dp.toPx()
            val outerRadius = radius + 1.dp.toPx()

            drawLine(
                color = Secondary,
                start = Offset(
                    center.x + (innerRadius * cos(angle)).toFloat(),
                    center.y + (innerRadius * sin(angle)).toFloat()
                ),
                end = Offset(
                    center.x + (outerRadius * cos(angle)).toFloat(),
                    center.y + (outerRadius * sin(angle)).toFloat()
                ),
                strokeWidth = 1.5.dp.toPx()
            )
        }

        // Star in center
        val starPath = Path().apply {
            val starRadius = 6.dp.toPx()
            val innerStarRadius = 3.dp.toPx()
            for (i in 0 until 5) {
                val outerAngle = Math.toRadians((i * 72.0) - 90)
                val innerAngle = Math.toRadians((i * 72.0) + 36 - 90)

                val outerX = center.x + (starRadius * cos(outerAngle)).toFloat()
                val outerY = center.y + (starRadius * sin(outerAngle)).toFloat()
                val innerX = center.x + (innerStarRadius * cos(innerAngle)).toFloat()
                val innerY = center.y + (innerStarRadius * sin(innerAngle)).toFloat()

                if (i == 0) {
                    moveTo(outerX, outerY)
                } else {
                    lineTo(outerX, outerY)
                }
                lineTo(innerX, innerY)
            }
            close()
        }
        drawPath(starPath, color = Primary)
    }
}

@Composable
private fun AutoSizeTitle(
    text: String,
    modifier: Modifier = Modifier,
    maxFontSize: TextUnit = 36.sp,
    minFontSize: TextUnit = 16.sp
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val maxWidthPx = constraints.maxWidth

        val optimalFontSize = remember(text, maxWidthPx, maxFontSize, minFontSize) {
            var fontSize = maxFontSize
            while (fontSize > minFontSize) {
                val textStyle = TextStyle(
                    fontFamily = StencilFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize,
                    letterSpacing = with(density) { (fontSize.value * 0.06f).sp }
                )
                val result = textMeasurer.measure(text = text, style = textStyle)
                if (result.size.width <= maxWidthPx) {
                    break
                }
                fontSize = (fontSize.value - 1f).sp
            }
            fontSize
        }

        Text(
            text = text,
            fontFamily = StencilFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = optimalFontSize,
            letterSpacing = (optimalFontSize.value * 0.06f).sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

