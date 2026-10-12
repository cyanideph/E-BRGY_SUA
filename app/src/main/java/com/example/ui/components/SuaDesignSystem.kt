package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

// ============================================================================
// SUA DESIGN SYSTEM V2 — viewport primitives
// Single source for spacing, gradients, section headers, banners, skeletons.
// ============================================================================

object SuaSpacing {
    val xxs: Dp = 4.dp
    val xs: Dp = 8.dp
    val sm: Dp = 12.dp
    val md: Dp = 16.dp
    val lg: Dp = 20.dp
    val xl: Dp = 24.dp
    val screenHorizontal: Dp = 16.dp
    val sectionGap: Dp = 20.dp
}

@Composable
fun suaHeaderGradient(): Brush = Brush.verticalGradient(
    colors = listOf(DeepOceanBlueDark, DeepOceanBlue, SouthernSeaTeal)
)

@Composable
fun suaCardGradient(): Brush? = null // cards stay flat M3; gradient reserved for header + emergency

/**
 * Standard section header: title + optional action. Enforces hierarchy:
 * titleLarge for sections, bodyMedium for supporting copy.
 */
@Composable
fun SuaSectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (actionLabel != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Offline banner — single canonical pattern. Replaces ad-hoc amber Surfaces.
 * Shows freshness when provided ("Updated 2h ago").
 */
@Composable
fun SuaOfflineBanner(
    isOnline: Boolean,
    lastSyncedLabel: String? = null,
    modifier: Modifier = Modifier
) {
    if (isOnline) return
    Surface(
        color = WarmAmberContainer,
        contentColor = OnWarmAmberContainer,
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Offline. Showing cached data." }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SuaSpacing.md, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = OnWarmAmberContainer,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "You're offline — showing cached barangay data.",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = OnWarmAmberContainer
                )
                if (lastSyncedLabel != null) {
                    Text(
                        text = "Last synced: $lastSyncedLabel",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnWarmAmberContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

/**
 * Shimmer skeleton block for loading states. Use in LazyColumn while
 * repository flows are still empty on first load.
 */
@Composable
fun SuaSkeletonBlock(
    modifier: Modifier = Modifier,
    height: Dp = 76.dp,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp)
) {
    val transition = rememberInfiniteTransition(label = "suaShimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = alpha))
    )
}

@Composable
fun SuaSkeletonList(
    count: Int = 3,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(count) { SuaSkeletonBlock() }
    }
}

fun Modifier.suaTestTag(tag: String): Modifier = this.testTag(tag)

/**
 * Canonical icon-tile used in list rows: 52dp rounded tile with
 * theme container color. Keeps Services / Requests / Announcements rows consistent.
 */
@Composable
fun SuaIconTile(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        modifier = modifier.size(52.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
