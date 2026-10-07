package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BarangayRepository
import com.example.model.Announcement
import com.example.model.AnnouncementCategory
import com.example.model.AnnouncementPriority
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.PriorityBadge
import com.example.ui.components.SoftSkeuomorphicCard
import com.example.ui.components.SuaWaveHeader
import com.example.ui.theme.*

@Composable
fun AnnouncementsScreen(
    onNavigateToAnnouncementDetails: (String) -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val announcements by repository.announcements.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("All", "Disaster & Weather", "Health", "Community", "General", "Emergency")

    val filteredList = remember(announcements, selectedCategory, searchQuery) {
        announcements.filter { ann ->
            val matchCat = when (selectedCategory) {
                "All" -> true
                "Disaster & Weather" -> ann.category == AnnouncementCategory.DISASTER
                "Health" -> ann.category == AnnouncementCategory.HEALTH
                "Community" -> ann.category == AnnouncementCategory.COMMUNITY
                "General" -> ann.category == AnnouncementCategory.GENERAL
                "Emergency" -> ann.priority == AnnouncementPriority.EMERGENCY
                else -> true
            }
            val matchSearch = ann.title.contains(searchQuery, ignoreCase = true) ||
                    ann.description.contains(searchQuery, ignoreCase = true)
            matchCat && matchSearch
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SuaWaveHeader(
            greeting = "Public Advisories",
            name = "Announcements",
            location = "Barangay Sua Public Information Desk"
        )

        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search advisories & notices...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DeepOceanBlue) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

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
                        label = { Text(category, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepOceanBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.card_announcements),
                contentDescription = "Barangay Announcements and Bulletins",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateCard(
                    icon = Icons.Default.Campaign,
                    title = "No Announcements",
                    message = "There are no announcements currently in this category.",
                    buttonText = "View All Announcements",
                    onButtonClick = {
                        selectedCategory = "All"
                        searchQuery = ""
                    }
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { item ->
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
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Published by: ${item.authorName} (${item.authorRole})",
                                fontSize = 11.sp,
                                color = SouthernSeaTealDark,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
