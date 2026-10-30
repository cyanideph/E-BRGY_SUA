package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.isSystemInReducedMotion
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.infiniteLoopableMemoryKey
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    message: String,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var useReducedMotion = isSystemInReducedMotion(LocalContext.current)
    val alpha = remember { if (useReducedMotion) 0.8f else shimmer.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emptyStateAlpha"
    ) }

    // Respect system reduced-motion preference
    val effectiveAlpha = if (useReducedMotion) 0.8f else alpha

    // Coastal gradient background that adapts to light/dark mode
    val bgColor = when {
        MaterialTheme.colorScheme.brightness == androidx.compose.ui.platform.isSystemInDarkTheme(LocalContext.current) ->
            DeepOceanContainer.copy(alpha = 0.6f)
        else -> WarmCoastalWhite.copy(alpha = 0.8f)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .defaultMinSize(minHeight = 200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
) {
        // Coastal gradient card foundation
        Surface(
            modifier = Modifier.size(88.dp).background(bgColor),
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 2.dp,
            shadowElevation = 1.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            // Animated coastal accent stripe
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                DeepOceanBlue.copy(alpha = 0.3f),
                                SouthernSeaTeal.copy(alpha = 0.3f)
                            )
                        )
                    )
            )
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = when (icon) {
                        is Icons.Default.AssignmentLate -> DeepNavy
                        is Icons.Default.CloudOff -> WarmAmber
                        is Icons.Default.SmartToy -> DeepOceanBlue
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // WCAG-AA contrast message with active voice copy
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Prominent single CTA — design principle: one memorable element
        if (buttonText != null && onButtonClick != null) {
            Button(
                onClick = onButtonClick,
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                // Active-voice CTA per design guidelines
                Text(
                    text = buttonText,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

}
