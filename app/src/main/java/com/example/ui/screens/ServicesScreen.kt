package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BarangayRepository
import com.example.model.BarangayService
import com.example.model.RequestStatus
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.SuaPlainRow
import com.example.ui.components.SuaRowDivider
import com.example.ui.components.SuaWaveHeader
import com.example.ui.theme.*

@Composable
fun ServicesScreen(
    onNavigateToServiceDetails: (String) -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val services by repository.services.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = remember(services) {
        listOf("All") + services.map { it.category }.distinct()
    }

    val filteredServices = remember(services, searchQuery, selectedCategory) {
        services.filter { service ->
            val matchesCategory = selectedCategory == "All" || service.category == selectedCategory
            val matchesSearch = service.name.contains(searchQuery, ignoreCase = true) ||
                    service.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    // Check if First-Time Jobseeker Clearance is available
    val hasFirstTimeJobseeker = services.any { it.name.contains("First-Time Jobseeker", ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        SuaWaveHeader(
            greeting = "Official Services",
            name = "Barangay Sua",
            location = "San Juan, Southern Leyte Municipal District"
        )

        Column(modifier = Modifier.padding(16.dp)) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search barangay clearances & certificates...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                edgePadding = 0.dp,
                divider = {},
                containerColor = Color.Transparent
            ) {
                categories.forEach { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }
            }
        }

        // List of Services — plain rows with dividers, no per-row cards.
        if (filteredServices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateCard(
                    icon = Icons.Default.SearchOff,
                    title = "No services found",
                    message = "Try a different keyword or category.",
                    buttonText = "Clear search",
                    onButtonClick = {
                        searchQuery = ""
                        selectedCategory = "All"
                    }
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            ) {
                items(filteredServices.size) { index ->
                    val service = filteredServices[index]
                    ServiceListItem(
                        service = service,
                        onClick = { onNavigateToServiceDetails(service.id) }
                    )
                    if (index < filteredServices.lastIndex) SuaRowDivider()
                }
            }
        }

        // Quick access FAB for first-time jobseeker clearance if available
        if (hasFirstTimeJobseeker) {
            FloatingActionButton(
                onClick = { /* Navigate to jobseeker clearance form */ },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_person_add_24),
                    contentDescription = "First-Time Jobseeker Clearance",
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun ServiceListItem(
    service: BarangayService,
    onClick: () -> Unit
) {
    // Non-card row: thumbnail tile + text. Parent draws dividers.
    val cardThumb = when {
        service.name.contains("First-Time Jobseeker", ignoreCase = true) -> R.drawable.ic_baseline_person_add_24
        service.name.contains("Clearance", ignoreCase = true) -> R.drawable.card_clearance
        service.name.contains("Residency", ignoreCase = true) -> R.drawable.card_residency
        service.name.contains("Indigency", ignoreCase = true) -> R.drawable.card_indigency
        service.name.contains("Business", ignoreCase = true) -> R.drawable.card_facilities
        else -> R.drawable.card_clearance
    }

    SuaPlainRow(
        title = service.name,
        subtitle = service.category,
        meta = "${service.processingDays} • ${service.feeDescription}",
        onClick = onClick,
        testTag = "serviceRow_${service.id}",
        leading = {
            Surface(
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.size(52.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Image(
                    painter = painterResource(id = cardThumb),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    )
}
