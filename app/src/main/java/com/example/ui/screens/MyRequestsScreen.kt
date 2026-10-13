package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentLate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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

    val tabs = listOf("Active", "Ready", "Rejected", "All (${requests.size})")

    val filteredRequests = remember(requests, selectedTab, searchQuery) {
        val tabFiltered = when (selectedTab) {
            0 -> requests.filter { it.status == RequestStatus.SUBMITTED || it.status == RequestStatus.UNDER_REVIEW || it.status == RequestStatus.PROCESSING }
            1 -> requests.filter { it.status == RequestStatus.READY || it.status == RequestStatus.COMPLETED }
            2 -> requests.filter { it.status == RequestStatus.REJECTED }
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
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Inline search (single filter — chips removed; Rejected is surfaced as its own tab)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by tracking number, clearance, purpose...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                items(filteredRequests.size) { index ->
                    val request = filteredRequests[index]
                    SuaPlainRow(
                        title = request.serviceName,
                        subtitle = "Purpose: ${request.purpose}",
                        meta = request.referenceNumber,
                        onClick = { onNavigateToRequestDetails(request.id) },
                        testTag = "requestRow_${request.id}",
                        badge = { RequestStatusBadge(status = request.status) },
                        trailing = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View timeline",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                    if (index < filteredRequests.lastIndex) SuaRowDivider()
                }
            }
        }
    }
}
