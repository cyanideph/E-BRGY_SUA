package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BarangayRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToServices: () -> Unit,
    onNavigateToServiceDetails: (String) -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToMyRequests: () -> Unit,
    onNavigateToRequestDetails: (String) -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToAnnouncementDetails: (String) -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToEventDetails: (String) -> Unit,
    onNavigateToAssistant: () -> Unit,
    onNavigateToAdminDashboard: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val userSession by repository.currentUser.collectAsState()
    val isOnline by repository.isOnline.collectAsState()
    val requests by repository.requests.collectAsState()
    val announcements by repository.announcements.collectAsState()
    val events by repository.events.collectAsState()
    val services by repository.services.collectAsState()

    val greeting = remember {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    var showQuickEmergencySheet by remember { mutableStateOf(false) }

    val myActiveRequests = remember(requests) {
        requests.filter { it.status != RequestStatus.COMPLETED && it.status != RequestStatus.REJECTED }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Coastal Wave Header
        SuaWaveHeader(
            greeting = greeting,
            name = userSession.profile.fullName.ifEmpty { "Resident" },
            location = "Barangay Sua • San Juan, Southern Leyte",
            trailingAction = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlobalOfflineSyncPill()
                    Spacer(modifier = Modifier.width(8.dp))
                    // Quick AI Barangay Assistant trigger
                    Surface(
                        onClick = onNavigateToAssistant,
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = "Barangay Assistant",
                                tint = WarmSunGoldLight,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        )

        // Clear page title for wayfinding and screen-reader comprehension
        Text(
            text = "Barangay Sua",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // Offline Banner
        if (!isOnline) {
            Surface(
                color = WarmAmberContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = WarmAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "You are currently offline. Viewing cached barangay data.",
                        fontSize = 12.sp,
                        color = DeepNavy,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // EMERGENCY / SOS SECTION
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SoftSkeuomorphicCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = RestrainedCoralRedContainer.copy(alpha = 0.45f),
                borderColor = RestrainedCoralRed.copy(alpha = 0.4f),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = RestrainedCoralRed,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Emergency,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Emergency Assistance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = RestrainedCoralRed
                            )
                            Text(
                                text = "Get help from Tanod or medical responders",
                                style = MaterialTheme.typography.bodySmall,
                                color = DeepNavySecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = { showQuickEmergencySheet = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RestrainedCoralRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "SOS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // QUICK SERVICES SECTION
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Services for you",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Keep the home screen focused; the full service catalog remains in Services.
            val primaryServices = services.take(4)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (chunk in primaryServices.chunked(2)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (service in chunk) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToServiceDetails(service.id) },
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 1.dp,
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val icon = when (service.iconKey) {
                                        "verified" -> Icons.Default.VerifiedUser
                                        "home" -> Icons.Default.Home
                                        "handshake" -> Icons.Default.VolunteerActivism
                                        "description" -> Icons.Default.Description
                                        "shield" -> Icons.Default.Security
                                        "store" -> Icons.Default.Storefront
                                        else -> Icons.Default.Article
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = DeepOceanContainer.copy(alpha = 0.6f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = DeepOceanBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = service.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = service.processingDays,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // MY REQUESTS (ACTIVE) SECTION
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Requests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = onNavigateToMyRequests) {
                    Text(
                        text = "View all (${requests.size})",
                        fontWeight = FontWeight.SemiBold,
                        color = SouthernSeaTealDark,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (myActiveRequests.isEmpty()) {
                SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                    EmptyStateCard(
                        icon = Icons.Default.AssignmentLate,
                        title = "No Active Requests",
                        message = "You don't have any ongoing requests. Apply for clearances and certifications anytime.",
                        buttonText = "Apply for a Service",
                        onButtonClick = onNavigateToServices
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    myActiveRequests.take(2).forEach { request ->
                        SoftSkeuomorphicCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigateToRequestDetails(request.id) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = request.serviceName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = request.referenceNumber,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = SouthernSeaTealDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = request.officialRemarks.ifEmpty { "Application in progress" },
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.End) {
                                    RequestStatusBadge(status = request.status)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    SyncStatusBadge(isSynced = request.isSyncedToServer)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // COASTAL TIDE & WEATHER WIDGET
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            CoastalWeatherWidget()
        }

        Spacer(modifier = Modifier.height(20.dp))

        // LATEST ANNOUNCEMENTS SECTION
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Latest Announcements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                announcements.take(2).forEach { item ->
                    SoftSkeuomorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onNavigateToAnnouncementDetails(item.id) }
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PriorityBadge(priority = item.priority)
                                Text(
                                    text = item.publishedDate,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // UPCOMING EVENTS SECTION
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Events",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            events.take(2).forEach { event ->
                SoftSkeuomorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    onClick = { onNavigateToEventDetails(event.id) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Date badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DeepOceanContainer,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = DeepOceanBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = event.date.take(6),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepOceanBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = SouthernSeaTealDark,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${event.date} • ${event.time}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }

    // 1-Tap Emergency Bottom Sheet
    if (showQuickEmergencySheet) {
        QuickEmergencyBottomSheet(
            onDismiss = { showQuickEmergencySheet = false },
            onEmergencyDispatched = { code ->
                // Emergency report created in repository
            }
        )
    }
}
