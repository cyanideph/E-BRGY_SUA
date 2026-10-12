package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun SuaWaveHeader(
    greeting: String,
    name: String,
    location: String = "Barangay Sua • San Juan, Southern Leyte",
    modifier: Modifier = Modifier,
    trailingAction: (@Composable () -> Unit)? = null
) {
    // Brand header is intentionally always deep-ocean gradient (both modes)
    // so white text stays AA. Shadow must wrap the clipped shape — clip first
    // via Surface shape rather than shadow-then-clip.
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        color = Color.Transparent,
        shadowElevation = 6.dp
    ) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DeepOceanBlueDark,
                        DeepOceanBlue,
                        SouthernSeaTeal
                    )
                )
            )
    ) {
        // Decorative Coastal Wave lines in background
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(135.dp)
        ) {
            val width = size.width
            val height = size.height

            val wavePath1 = Path().apply {
                moveTo(0f, height * 0.65f)
                cubicTo(
                    width * 0.25f, height * 0.45f,
                    width * 0.70f, height * 0.85f,
                    width, height * 0.55f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = wavePath1,
                color = Color.White.copy(alpha = 0.08f)
            )

            val wavePath2 = Path().apply {
                moveTo(0f, height * 0.80f)
                cubicTo(
                    width * 0.35f, height * 0.95f,
                    width * 0.65f, height * 0.65f,
                    width, height * 0.85f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = wavePath2,
                color = WarmSunGold.copy(alpha = 0.12f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Tagline badge
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_sua_logo),
                                    contentDescription = "Barangay Sua Emblem",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "e-Barangay Sua Portal",
                                color = Color.White.copy(alpha = 0.95f),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "$greeting, $name",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = WarmSunGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = location,
                            color = Color.White.copy(alpha = 0.88f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                    }
                }

                if (trailingAction != null) {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        trailingAction()
                    }
                }
            }
        }
        }
    }
}

@Composable
fun SuaWaveDivider(
    modifier: Modifier = Modifier,
    color: Color = SouthernSeaTeal.copy(alpha = 0.25f)
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
    ) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(0f, height * 0.5f)
            cubicTo(
                width * 0.25f, 0f,
                width * 0.75f, height,
                width, height * 0.5f
            )
        }
        drawPath(
            path = path,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
        )
    }
}
