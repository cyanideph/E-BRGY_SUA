package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.services.CoastalTelemetry
import com.example.services.OpenMeteoService
import com.example.services.SeaSafety
import com.example.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Beautiful Coastal Weather, Swell & Marine Advisory Widget
 * Powered by Open-Meteo Live API telemetry for Barangay Sua, San Juan, Southern Leyte.
 */
@Composable
fun CoastalWeatherWidget(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var telemetry by remember { mutableStateOf(CoastalTelemetry()) }
    var isRefreshing by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    // Fetch initial live telemetry from Open-Meteo
    LaunchedEffect(Unit) {
        isRefreshing = true
        telemetry = OpenMeteoService.fetchCoastalTelemetry()
        isRefreshing = false
    }

    // Rotating animation for refresh button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        SouthernSeaTeal.copy(alpha = 0.35f),
                        DeepOceanBlue.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            ),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SouthernSeaTealContainer.copy(alpha = 0.22f),
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Header Row: Coastal Station, Live Status, Sea Safety & Refresh
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(DeepOceanBlue, SouthernSeaTeal)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Waves,
                            contentDescription = "Marine Waters",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Sua Coastal Waters",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Live indicator pill
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (telemetry.isLive) NaturalGreenContainer else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (telemetry.isLive) NaturalGreen.copy(alpha = pulseAlpha)
                                                else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (telemetry.isLive) "LIVE" else "EST.",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (telemetry.isLive) NaturalGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Cabalian Bay • ${telemetry.conditionDescription}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Safety Badge Pill
                    val (badgeBg, badgeFg) = when (telemetry.safetyStatus) {
                        SeaSafety.SAFE_SEAS -> NaturalGreenContainer to NaturalGreen
                        SeaSafety.MODERATE_SWELL -> WarmAmberContainer to WarmAmber
                        SeaSafety.GALE_WARNING -> RestrainedCoralRedContainer to RestrainedCoralRed
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = badgeBg,
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, badgeFg.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = telemetry.safetyStatus.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = badgeFg,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Refresh Button
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                isRefreshing = true
                                telemetry = OpenMeteoService.fetchCoastalTelemetry()
                                isRefreshing = false
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = SouthernSeaTeal
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Marine Telemetry",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary 4-Metric Grid (Wave Height, Rain Probability, Wind Speed, Temperature)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Wave / Swell Height Tile
                MetricTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Water,
                    iconTint = DeepOceanBlue,
                    title = "Wave Height",
                    value = "${String.format(java.util.Locale.US, "%.1f", telemetry.waveHeightMeters)} m",
                    subtitle = "${String.format(java.util.Locale.US, "%.1f", telemetry.swellHeightMeters)}m Swell"
                )

                // 2. Rain Probability Tile
                MetricTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.WaterDrop,
                    iconTint = SouthernSeaTealDark,
                    title = "Rain Risk",
                    value = "${telemetry.rainProbability}%",
                    subtitle = if (telemetry.rainProbability > 60) "High Risk" else if (telemetry.rainProbability > 30) "Scattered" else "Low Risk",
                    progress = telemetry.rainProbability / 100f
                )

                // 3. Wind Speed Tile
                MetricTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Air,
                    iconTint = WarmAmber,
                    title = "Wind",
                    value = "${String.format(java.util.Locale.US, "%.0f", telemetry.windSpeedKmH)} km/h",
                    subtitle = telemetry.windDirection.substringBefore(" (")
                )

                // 4. Air Temp Tile
                MetricTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Thermostat,
                    iconTint = WarmSunGold,
                    title = "Temp",
                    value = "${String.format(java.util.Locale.US, "%.0f", telemetry.temperature)}°C",
                    subtitle = "${telemetry.humidity}% RH"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expandable Fisherfolk Advisory & Marine Protected Sanctuary Section
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Advisory Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SouthernSeaTealContainer.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SouthernSeaTealLight.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sailing,
                                contentDescription = null,
                                tint = SouthernSeaTeal,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Fisherfolk & Sea Travel Advisory",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSouthernSeaTealContainer
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = telemetry.safetyStatus.advisory,
                                    fontSize = 11.sp,
                                    color = DeepNavy,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // Sua Marine Sanctuary Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DeepOceanContainer.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DeepOceanBlueLight.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = DeepOceanBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Sua Marine Fish Sanctuary Boundary",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepOceanBlue
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Motorized commercial fishing and compressor diving are prohibited within the 200m marked sanctuary buffer zone to protect coral gardens and giant clams.",
                                    fontSize = 11.sp,
                                    color = DeepNavy,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // Tides Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = DeepOceanBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("High Tide", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(telemetry.highTideEstimate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = SouthernSeaTeal, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("Low Tide", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(telemetry.lowTideEstimate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Source and coordinates footer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Telemetry: Open-Meteo WMO • ECMWF Wave Model",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Updated: ${telemetry.lastUpdated}",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Expand / Collapse Action Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isExpanded = !isExpanded },
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isExpanded) "Hide marine advisory" else "Tap for fisherfolk advisory & sanctuary details",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricTile(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    subtitle: String,
    progress: Float? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(0.6.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 9.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = title,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = subtitle,
                fontSize = 8.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            if (progress != null) {
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (progress > 0.6f) RestrainedCoralRed else SouthernSeaTeal,
                    trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                )
            }
        }
    }
}
