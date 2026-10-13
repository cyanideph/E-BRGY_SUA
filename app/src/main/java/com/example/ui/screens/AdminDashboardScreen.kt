package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BarangayRepository
import com.example.model.RequestStatus
import com.example.ui.components.RoleBadge
import com.example.ui.components.SoftSkeuomorphicCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRequests: () -> Unit,
    onNavigateToResidents: () -> Unit,
    onNavigateToHouseholds: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToEmergencies: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToAuditLogs: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val userSession by repository.currentUser.collectAsState()
    val requests by repository.requests.collectAsState()
    val residents by repository.residents.collectAsState()
    val households by repository.households.collectAsState()
    val emergencies by repository.emergencyReports.collectAsState()
    val events by repository.events.collectAsState()

    val pendingCount = requests.count { it.status == RequestStatus.SUBMITTED || it.status == RequestStatus.UNDER_REVIEW }
    val processingCount = requests.count { it.status == RequestStatus.PROCESSING }
    val readyCount = requests.count { it.status == RequestStatus.READY }
    val activeEmergencies = emergencies.count { it.status != com.example.model.EmergencyStatus.RESOLVED }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Barangay Admin Console", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            "${userSession.role.displayName} • San Juan, Southern Leyte",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    RoleBadge(role = userSession.role, modifier = Modifier.padding(end = 12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Needs attention",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            SoftSkeuomorphicCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToRequests
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$pendingCount requests",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DeepOceanBlue
                        )
                        Text(
                            text = if (activeEmergencies == 0) "No active emergencies" else "$activeEmergencies active emergency calls",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = onNavigateToRequests) {
                        Text("Review requests")
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Today",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminMetricCard(
                    title = "Processing",
                    value = "$processingCount",
                    subtitle = "In progress",
                    color = SouthernSeaTeal,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToRequests
                )
                AdminMetricCard(
                    title = "Ready",
                    value = "$readyCount",
                    subtitle = "For release",
                    color = NaturalGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToRequests
                )
                AdminMetricCard(
                    title = "Emergencies",
                    value = "$activeEmergencies",
                    subtitle = "Active",
                    color = RestrainedCoralRed,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToEmergencies
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Management",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AdminNavTile(
                    title = "Requests",
                    desc = "Review and process document requests",
                    icon = Icons.Default.AssignmentTurnedIn,
                    onClick = onNavigateToRequests
                )
                AdminNavTile(
                    title = "Residents",
                    desc = "Manage the resident registry",
                    icon = Icons.Default.People,
                    onClick = onNavigateToResidents
                )
                AdminNavTile(
                    title = "Announcements",
                    desc = "Publish official notices and advisories",
                    icon = Icons.Default.Campaign,
                    onClick = onNavigateToAnnouncements
                )
                AdminNavTile(
                    title = "Reports",
                    desc = "Open reports and analytics",
                    icon = Icons.Default.BarChart,
                    onClick = onNavigateToReports
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CHARTS SECTION
            Text(
                text = "Service Demand & Analytics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Chart: Request Status Distribution
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Request Status Distribution",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Bar Visualization
                val statusCounts = listOf(
                    Triple("Submitted", requests.count { it.status == RequestStatus.SUBMITTED }, DeepOceanBlue),
                    Triple("Under Review", requests.count { it.status == RequestStatus.UNDER_REVIEW }, SouthernSeaTeal),
                    Triple("Processing", requests.count { it.status == RequestStatus.PROCESSING }, WarmAmber),
                    Triple("Ready", requests.count { it.status == RequestStatus.READY }, NaturalGreen),
                    Triple("Completed", requests.count { it.status == RequestStatus.COMPLETED }, DeepNavySecondary)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    statusCounts.forEach { (label, count, color) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.width(90.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            // P0-9: a zero count renders as a track-only bar — no fake 8% fill.
                            val fraction = if (requests.isEmpty() || count == 0) {
                                0f
                            } else {
                                (count.toFloat() / requests.size).coerceIn(0.02f, 1f)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(16.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                if (fraction > 0f) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(fraction)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(color)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$count",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart: Weekly Requests Volume Trend
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Weekly request volume (sample)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Sample visualization — live counts coming soon.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Custom Line / Points Chart Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    val points = listOf(14f, 22f, 18f, 29f, 35f, 28f, 42f)
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val maxVal = 50f
                    val stepX = size.width / (points.size - 1)

                    // Draw baseline
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2f
                    )

                    // Draw path
                    val path = androidx.compose.ui.graphics.Path()
                    points.forEachIndexed { i, p ->
                        val x = i * stepX
                        val y = size.height - ((p / maxVal) * size.height)
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }

                    drawPath(
                        path = path,
                        color = DeepOceanBlue,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                    )

                    // Draw dots
                    points.forEachIndexed { i, p ->
                        val x = i * stepX
                        val y = size.height - ((p / maxVal) * size.height)
                        drawCircle(color = WarmSunGold, radius = 5f, center = Offset(x, y))
                        drawCircle(color = DeepOceanBlue, radius = 2.5f, center = Offset(x, y))
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach {
                        Text(it, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "More administration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AdminNavTile(
                    title = "Households",
                    desc = "Purok groups, family heads and emergency tags",
                    icon = Icons.Default.HomeWork,
                    onClick = onNavigateToHouseholds
                )
                AdminNavTile(
                    title = "Events",
                    desc = "Schedule assemblies and community programs",
                    icon = Icons.Default.Event,
                    onClick = onNavigateToEvents
                )
                AdminNavTile(
                    title = "Emergency Dispatch",
                    desc = "Manage incoming SOS calls and responder actions",
                    icon = Icons.Default.Emergency,
                    onClick = onNavigateToEmergencies
                )
                AdminNavTile(
                    title = "System Audit Logs",
                    desc = "Review administrative actions and status history",
                    icon = Icons.Default.ReceiptLong,
                    onClick = onNavigateToAuditLogs
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    SoftSkeuomorphicCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = color
        )
        Text(
            text = subtitle,
            fontSize = 10.sp,
            color = DeepNavySecondary
        )
    }
}

@Composable
private fun AdminNavTile(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    SoftSkeuomorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DeepOceanContainer,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = DeepOceanBlue, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
