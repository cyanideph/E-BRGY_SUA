package com.example.ui.resident.advisories

import androidx.compose.foundation.LazyColumn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.Announcement

/** Advisory Adapter - displays list of announcements with Material 3 typography.
 * 
 *  Handles the advisory list rendering with proper formatting,
 *  priority badges, and selection handling.
 */
@Composable
fun AdvisoryAdapter(
    announcements: List<Announcement>,
    onSelect: ((Announcement) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth().verticalScroll(
            rememberScrollState()
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(announcements.size) { index ->
            val announcement = announcements[index]

            AdvisoryCard(
                announcement = announcement,
                onSelect = { onSelect?.invoke(announcement) },
                isLast = index == announcements.size - 1
            )
        }
    }
}

/** Priority badge color mapping */
@Composable
fun PriorityBadge(priority: String) = when (priority) {
    "URGENT" -> {
        OutlinedChip(
            label = { Text("URGENT", style = MaterialTheme.typography.labelSmall) },
            backgroundColor = MaterialTheme.colorScheme.error,
            textColor = MaterialTheme.colorScheme.onError
        )
    }
    "HIGH" -> {
        OutlinedChip(
            label = { Text("HIGH", style = MaterialTheme.typography.labelSmall) },
            backgroundColor = MaterialTheme.colorScheme.warning,
            textColor = MaterialTheme.colorScheme.onWarning
        )
    }
    "MEDIUM" -> {
        OutlinedChip(
            label = { Text("MEDIUM", style = MaterialTheme.typography.labelSmall) },
            backgroundColor = MaterialTheme.colorScheme.secondary,
            textColor = MaterialTheme.colorScheme.onSecondary
        )
    }
    "LOW" -> {
        OutlinedChip(
            label = { Text("LOW", style = MaterialTheme.typography.labelSmall) },
            backgroundColor = MaterialTheme.colorScheme.surface,
            textColor = MaterialTheme.colorScheme.onSurface
        )
    }
    else -> OutlinedChip(
        label = { Text(priority, style = MaterialTheme.typography.labelSmall) }
    )
}