package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AnnouncementPriority
import com.example.model.EmergencyStatus
import com.example.model.RequestStatus
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun RequestStatusBadge(
    status: RequestStatus,
    modifier: Modifier = Modifier
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val (bgColor, textColor, icon) = when (status) {
        RequestStatus.SUBMITTED -> Triple(
            if (isDark) MaterialTheme.colorScheme.primaryContainer else DeepOceanContainer.copy(alpha = 0.6f),
            if (isDark) MaterialTheme.colorScheme.onPrimaryContainer else DeepOceanBlue,
            Icons.Default.Schedule
        )
        RequestStatus.UNDER_REVIEW -> Triple(
            if (isDark) MaterialTheme.colorScheme.secondaryContainer else SouthernSeaTealContainer.copy(alpha = 0.6f),
            if (isDark) MaterialTheme.colorScheme.onSecondaryContainer else SouthernSeaTealDark,
            Icons.Default.Search
        )
        RequestStatus.PROCESSING -> Triple(
            if (isDark) WarmAmberContainerDark else WarmAmberContainer.copy(alpha = 0.7f),
            if (isDark) OnWarmAmberContainerDark else OnWarmAmberContainer,
            Icons.Default.HourglassTop
        )
        RequestStatus.READY -> Triple(
            if (isDark) NaturalGreenContainerDark else NaturalGreenContainer,
            if (isDark) OnNaturalGreenContainerDark else OnNaturalGreenContainer,
            Icons.Default.CheckCircle
        )
        RequestStatus.COMPLETED -> Triple(
            MaterialTheme.colorScheme.surfaceContainerHigh,
            MaterialTheme.colorScheme.onSurfaceVariant,
            Icons.Default.DoneAll
        )
        RequestStatus.REJECTED -> Triple(
            if (isDark) CoralContainerDark else RestrainedCoralRedContainer,
            if (isDark) OnCoralContainerDark else OnRestrainedCoralContainer,
            Icons.Default.Cancel
        )
    }

    Surface(
        modifier = modifier.semantics { contentDescription = "Request status: ${status.label}" },
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.label,
                color = textColor,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun PriorityBadge(
    priority: AnnouncementPriority,
    modifier: Modifier = Modifier
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val (bgColor, textColor, label) = when (priority) {
        AnnouncementPriority.NORMAL -> Triple(
            if (isDark) MaterialTheme.colorScheme.secondaryContainer else SouthernSeaTealContainer.copy(alpha = 0.5f),
            if (isDark) MaterialTheme.colorScheme.onSecondaryContainer else SouthernSeaTealDark,
            "Normal"
        )
        AnnouncementPriority.IMPORTANT -> Triple(
            if (isDark) WarmAmberContainerDark else WarmAmberContainer,
            if (isDark) OnWarmAmberContainerDark else OnWarmAmberContainer,
            "Important"
        )
        AnnouncementPriority.EMERGENCY -> Triple(
            if (isDark) CoralContainerDark else RestrainedCoralRedContainer,
            if (isDark) OnCoralContainerDark else OnRestrainedCoralContainer,
            "Emergency"
        )
    }

    Surface(
        modifier = modifier.semantics { contentDescription = "Priority: $label" },
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.35f))
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun SyncStatusBadge(
    isSynced: Boolean,
    modifier: Modifier = Modifier
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val (bgColor, textColor, label, icon) = if (isSynced) {
        Quadruple(
            if (isDark) NaturalGreenContainerDark else NaturalGreenContainer.copy(alpha = 0.5f),
            if (isDark) OnNaturalGreenContainerDark else OnNaturalGreenContainer,
            "Successfully submitted",
            Icons.Default.CloudDone
        )
    } else {
        Quadruple(
            if (isDark) WarmAmberContainerDark else WarmAmberContainer.copy(alpha = 0.6f),
            if (isDark) OnWarmAmberContainerDark else OnWarmAmberContainer,
            "Pending sync",
            Icons.Default.CloudQueue
        )
    }

    Surface(
        modifier = modifier.semantics { contentDescription = label },
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

@Composable
fun RoleBadge(
    role: UserRole,
    modifier: Modifier = Modifier
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val (bgColor, textColor) = when (role) {
        UserRole.RESIDENT -> Pair(
            if (isDark) MaterialTheme.colorScheme.secondaryContainer else SouthernSeaTealContainer,
            if (isDark) MaterialTheme.colorScheme.onSecondaryContainer else SouthernSeaTealDark
        )
        UserRole.STAFF -> Pair(
            if (isDark) MaterialTheme.colorScheme.primaryContainer else DeepOceanContainer,
            if (isDark) MaterialTheme.colorScheme.onPrimaryContainer else DeepOceanBlue
        )
        UserRole.OFFICIAL -> Pair(WarmSunGoldContainer, OnWarmSunGoldContainer)
        UserRole.ADMIN -> Pair(
            if (isDark) CoralContainerDark else RestrainedCoralRedContainer,
            if (isDark) OnCoralContainerDark else OnRestrainedCoralContainer
        )
    }

    Surface(
        modifier = modifier.semantics { contentDescription = "Role: ${role.displayName}" },
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.35f))
    ) {
        Text(
            text = role.displayName,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmergencyStatusBadge(
    status: EmergencyStatus,
    modifier: Modifier = Modifier
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val (bgColor, textColor, icon) = when (status) {
        EmergencyStatus.RECEIVED -> Triple(
            if (isDark) WarmAmberContainerDark else WarmAmberContainer,
            if (isDark) OnWarmAmberContainerDark else OnWarmAmberContainer,
            Icons.Default.HourglassTop
        )
        EmergencyStatus.RESPONDING -> Triple(
            if (isDark) CoralContainerDark else RestrainedCoralRedContainer,
            if (isDark) OnCoralContainerDark else OnRestrainedCoralContainer,
            Icons.Default.DirectionsRun
        )
        EmergencyStatus.RESOLVED -> Triple(
            if (isDark) NaturalGreenContainerDark else NaturalGreenContainer,
            if (isDark) OnNaturalGreenContainerDark else OnNaturalGreenContainer,
            Icons.Default.CheckCircle
        )
    }

    Surface(
        modifier = modifier.semantics { contentDescription = "Emergency: ${status.label}" },
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.label,
                color = textColor,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
