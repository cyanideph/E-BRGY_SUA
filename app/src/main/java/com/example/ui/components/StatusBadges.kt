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
    val (bgColor, textColor, icon) = when (status) {
        RequestStatus.SUBMITTED -> Triple(
            DeepOceanContainer.copy(alpha = 0.6f),
            DeepOceanBlue,
            Icons.Default.Schedule
        )
        RequestStatus.UNDER_REVIEW -> Triple(
            SouthernSeaTealContainer.copy(alpha = 0.6f),
            SouthernSeaTealDark,
            Icons.Default.Search
        )
        RequestStatus.PROCESSING -> Triple(
            WarmAmberContainer.copy(alpha = 0.7f),
            WarmAmber,
            Icons.Default.HourglassTop
        )
        RequestStatus.READY -> Triple(
            NaturalGreenContainer,
            NaturalGreen,
            Icons.Default.CheckCircle
        )
        RequestStatus.COMPLETED -> Triple(
            Color(0xFFE2E8F0),
            DeepNavySecondary,
            Icons.Default.DoneAll
        )
        RequestStatus.REJECTED -> Triple(
            RestrainedCoralRedContainer,
            RestrainedCoralRed,
            Icons.Default.Cancel
        )
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.25f))
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
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PriorityBadge(
    priority: AnnouncementPriority,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (priority) {
        AnnouncementPriority.NORMAL -> Triple(
            SouthernSeaTealContainer.copy(alpha = 0.5f),
            SouthernSeaTealDark,
            "Normal"
        )
        AnnouncementPriority.IMPORTANT -> Triple(
            WarmAmberContainer,
            WarmAmber,
            "Important"
        )
        AnnouncementPriority.EMERGENCY -> Triple(
            RestrainedCoralRedContainer,
            RestrainedCoralRed,
            "Emergency"
        )
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun SyncStatusBadge(
    isSynced: Boolean,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label, icon) = if (isSynced) {
        Quadruple(
            NaturalGreenContainer.copy(alpha = 0.5f),
            NaturalGreen,
            "Successfully submitted",
            Icons.Default.CloudDone
        )
    } else {
        Quadruple(
            WarmAmberContainer.copy(alpha = 0.6f),
            WarmAmber,
            "Pending sync",
            Icons.Default.CloudQueue
        )
    }

    Surface(
        modifier = modifier,
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
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RoleBadge(
    role: UserRole,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (role) {
        UserRole.RESIDENT -> Pair(SouthernSeaTealContainer, SouthernSeaTealDark)
        UserRole.STAFF -> Pair(DeepOceanContainer, DeepOceanBlue)
        UserRole.OFFICIAL -> Pair(WarmSunGoldContainer, WarmSunGoldDark)
        UserRole.ADMIN -> Pair(RestrainedCoralRedContainer, RestrainedCoralRed)
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.3f))
    ) {
        Text(
            text = role.displayName,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
