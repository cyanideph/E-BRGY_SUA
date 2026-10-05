package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentLate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BarangayRepository
import com.example.model.RequestStatus
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun MyRequestsScreen(
    onNavigateToRequestDetails: (String) -> Unit,
    onNavigateToServices: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val requests by repository.requests.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val tabs = listOf("Active", "Ready / Completed", "All (${requests.size})")

    val filteredRequests = remember(requests, selectedTab, searchQuery) {
        val tabFiltered = when (selectedTab) {
            0 -> requests.filter { it.status == RequestStatus.SUBMITTED || it.status == RequestStatus.UNDER_REVIEW || it.status == RequestStatus.PROCESSING }
            1 -> requests.filter { it.status == RequestStatus.READY || it.status == RequestStatus.COMPLETED }
            else -> requests
        }

        if (searchQuery.isBlank()) {
            tabFiltered
        } else {
            tabFiltered.filter {
                it.serviceName.contains(searchQuery, ignoreCase = true) ||
                it.referenceNumber.contains(searchQuery, ignoreCase = true) ||
                it.purpose.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SuaWaveHeader(
            greeting = "Document Tracking",
            name = "My Requests",
            location = "Barangay Sua Secretariat Desk"
        )

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = DeepOceanBlue
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = (selectedTab == index),
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Search Filter Bar
        CivicSearchFilterBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            filterChips = listOf("All", "Active", "Ready", "Completed"),
            selectedFilter = when (selectedTab) {
                0 -> "Active"
                1 -> "Ready"
                else -> "All"
            },
            onFilterSelected = { chip ->
                selectedTab = when (chip) {
                    "Active" -> 0
                    "Ready", "Completed" -> 1
                    else -> 2
                }
            },
            placeholderText = "Search by tracking number, clearance, purpose...",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )

        if (filteredRequests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateCard(
                    icon = Icons.Default.AssignmentLate,
                    title = "No Requests Found",
                    message = "You don't have any requests in this category. Submit an application anytime.",
                    buttonText = "View Available Services",
                    onButtonClick = onNavigateToServices
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredRequests) { request ->
                    SoftSkeuomorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onNavigateToRequestDetails(request.id) }
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = request.referenceNumber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SouthernSeaTealDark
                                )
                                RequestStatusBadge(status = request.status)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = request.serviceName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Purpose: ${request.purpose}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SyncStatusBadge(isSynced = request.isSyncedToServer)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "View Timeline",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepOceanBlue
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = DeepOceanBlue,
                                        modifier = Modifier.size(16.dp)
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
