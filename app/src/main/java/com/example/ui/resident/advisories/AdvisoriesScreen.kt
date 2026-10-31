package com.example.ui.resident.advisories

import androidx.compose.foundation.ListItem
import androidx.compose.foundation.layout.Arrangement.End
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingToken
import androidx.compose.foundation.layout.SizeModifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.R

/** Advisories Screen - Displays list of community announcements.
 * 
 *  Shows latest advisories from Appwrite database with priority badges.
 *  Follows constitution principles:
 *  - VII: Accessibility (contentDescription, contrast, touch targets)
 *  - VIII: Testing (unit tests required before merge)
 *  - IX: Compatibility (min SDK 24, works on entry-level devices)
 */
@Composable
fun AdvisoriesScreen(
    viewModel: AdvisoriesViewModel = AdvisoriesViewModel(),
    onAdvertSelected: ((Announcement) -> Unit)? = null
) {
    val announcements by viewModel.announcements.collectAsList()
    var error by remember { mutableStateOf<String?>(null) }

    when {
        isEmpty(announcements) -> {
            if (error != null) {
                Text(
                    text = "Error loading advisories: $error",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typrology.body1,
                    color = MaterialTheme.colorScheme.error,
                    contentDescription = "Error: failed to load advisories"
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.BaseIcons.info_outline,
                        contentDescription = "No advisories available",
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No advisories available",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Check back later for community announcements",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = "Information: check later for community announcements"
                    )
                    OutlinedButton(
                        onPressed = { viewModel.refreshAdvisories() },
                        toggleBox = {
                            Text(
                                text = "Refresh",
                                contentDescription = "Retry loading advisories"
                            )
                        }
                    )
                }
                }
            }
        }
        else -> {
            AdvisoryList(
                announcements = announcements,
                onSelect = { announcement ->
                    onAdvertSelected?.invoke(announcement)
                }
            )
        }
    }
}

/** Advisory List with pull-to-refresh and Material 3 typography. */
@Composable
fun AdvisoryList(
    announcements: List<Announcement>,
    onSelect: ((Announcement) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var refresh by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalPadding(16.dp),
        verticalArrangement = Arrangement.Start,
        horizontalAlignment = Alignment.Start
    ) {
        // Toolbar with title
        Row(
            modifier = Modifier.fillMaxWidth(),
            arrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Advisories",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            if (refresh) {
                CircularProgressIndicator(
                    modifier = Modifier.wrapContentSize(),
                    strokeWidth = 2.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Advisory items
        if (announcements.isEmpty()) {
            Text(
                text = "No advisories at this time",
                style = MaterialTheme.typrology.body1,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingToken.Zero,
                horizontalArrangement = Arrangement.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(announcements.size) { index ->
                    AdvisoryCard(
                        announcement = announcements[index],
                        onSelect = { onSelect?.invoke(announcements[index]) },
                        isLast = index == announcements.size - 1
                    )
                }
            }
        }

        // Pull-to-refresh indicator
        if (refresh) {
            Modifier.awaitVerticalScroll(visibleCount = 1)
            Text(
                text = "Pull down to refresh",
                modifier = Modifier.align(Alignment.End),
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Individual advisory card with priority badge and content. */
@Composable
fun AdvisoryCard(
    announcement: Announcement,
    onSelect: (() -> Unit)? = null,
    isLast: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        elevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ) {
            // Title and priority badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                arrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = announcement.title,
                        style = MaterialTheme.typography.titleMedium,
                        overflow = Overflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        text = announcement.createdAt?.takeIf { it.isNotEmpty() } ?? "",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Priority badge
                Badge(
                    value = announcement.priority ?: "LOW",
                    modifier = Modifier.padding(end = 4.dp)
                )
            }

            // Content preview
            Text(
                text = announcement.content?.take(100)?.takeIf { it.isNotEmpty() } else "",
                style = MaterialTheme.typography.body2,
                overflow = Overflow.Elipsis,
                maxLines = 2,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, horizontal = 4.dp)
            )

            // Select action
            if (onSelect != null) {
                Button(
                    onClick = onSelect,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    enabled = true
                ) {
                    Text("Read full advisory", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

/** ViewModel for Advisories Screen - handles data fetching. */
class AdvisoriesViewModel : ViewModel() {

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: Flow<List<Announcement>> = _announcements

    private val _isLoading = MutableStateFlow(true)
    val isLoading: Flow<bool> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: Flow<String?> = _error

    private val authProvider: AuthProvider = AuthProvider(AppwriteDataSource())

    /** Fetch announcements from Appwrite */
    init {
        viewModelScope.launch {
            fetchAnnouncements()
        }
    }

    private fun fetchAnnouncements() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val ads = withContext(Dispatchers.IO) {
                    authProvider.getAnnouncements().firstOrNull()
                        ?: emptyList()
                }

                // Transform from Appwrite data model to domain model
                val announcements = ads.map { ann ->
                    Announcement(
                        title = ann.title ?: "",
                        content = ann.content ?: "",
                        author = ann.author ?: "Barangay Official",
                        priority = ann.priority ?: "LOW",
                        expiresAt = ann.expiresAt,
                        isPinned = ann.isPinned ?: false,
                        createdAt = ann.createdAt ?: ""
                    )
                }

                _announcements.value = announcements
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = "Failed to load advisories. Please try again."
                _isLoading.value = false
                // Constitution VII: Log error without secrets
                ErrorHandler.logError("fetchAnnouncements", e, mapOf())
            }
        }
    }

    /** Refresh announcements */
    fun refresh() {
        viewModelScope.launch {
            fetchAnnouncements()
        }
    }
}