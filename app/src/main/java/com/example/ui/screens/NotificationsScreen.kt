package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BarangayRepository
import com.example.model.BarangayNotification
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.SoftSkeuomorphicCard
import com.example.ui.components.SuaWaveHeader
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationsScreen(
    onNavigateToRequestDetails: ((String) -> Unit)? = null
) {
    val repository = remember { BarangayRepository.instance }
    val notifications by repository.notifications.collectAsState()
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.US) }

    Column(modifier = Modifier.fillMaxSize()) {
        SuaWaveHeader(
            greeting = "In-App Alerts",
            name = "Notifications",
            location = "Barangay Sua Dispatch",
            trailingAction = {
                if (notifications.any { !it.isRead }) {
                    TextButton(onClick = { repository.markAllNotificationsAsRead() }) {
                        Text(
                            text = "Mark all read",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        )

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateCard(
                    icon = Icons.Default.NotificationsNone,
                    title = "No Notifications",
                    message = "You have no new notifications at this time."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications) { notif ->
                    val (icon, tint) = when (notif.priority) {
                        "Emergency" -> Pair(Icons.Default.Emergency, RestrainedCoralRed)
                        "Important" -> Pair(Icons.Default.PriorityHigh, WarmAmber)
                        else -> Pair(Icons.Default.Notifications, DeepOceanBlue)
                    }

                    SoftSkeuomorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = if (notif.isRead) MaterialTheme.colorScheme.surface else DeepOceanContainer.copy(alpha = 0.35f),
                        onClick = { repository.markNotificationAsRead(notif.id) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = tint.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = if (notif.isRead) FontWeight.SemiBold else FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (!notif.isRead) {
                                        Surface(
                                            shape = CircleShape,
                                            color = SouthernSeaTeal,
                                            modifier = Modifier.size(8.dp)
                                        ) {}
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = SouthernSeaTealContainer
                                    ) {
                                        Text(
                                            text = notif.category,
                                            fontSize = 10.sp,
                                            color = SouthernSeaTealDark,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = dateFormat.format(Date(notif.timestamp)),
                                        fontSize = 10.sp,
                                        color = DeepNavySecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
