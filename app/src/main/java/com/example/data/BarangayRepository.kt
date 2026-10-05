package com.example.data

import android.content.Context
import com.example.data.local.*
import com.example.model.*
import com.example.services.Appwrite
import com.example.services.CivicSyncService
import com.example.services.NotificationHelper
import io.appwrite.ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.time.ZoneId
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class AppThemeMode(val displayName: String, val subtitle: String) {
    LIGHT("Light Mode (Default)", "Clean warm coastal daylight theme"),
    DARK("Dark Mode", "Deep midnight ocean navy palette"),
    SYSTEM("Follow System", "Adapts automatically to device settings")
}

class BarangayRepository {

    // App Theme State: Default is Light Mode
    private val _themeMode = MutableStateFlow(AppThemeMode.LIGHT)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun toggleTheme() {
        _themeMode.value = if (_themeMode.value == AppThemeMode.DARK) AppThemeMode.LIGHT else AppThemeMode.DARK
    }

    // Current User Session
    private val _currentUser = MutableStateFlow(UserSession())
    val currentUser: StateFlow<UserSession> = _currentUser.asStateFlow()

    fun switchRole(newRole: UserRole) {
        _currentUser.value = _currentUser.value.copy(role = newRole)
    }

    // Offline / Online state
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    // Services
    private val _services = MutableStateFlow<List<BarangayService>>(emptyList())
    val services: StateFlow<List<BarangayService>> = _services.asStateFlow()

    // Document Requests
    private val _requests = MutableStateFlow<List<DocumentRequest>>(emptyList())
    val requests: StateFlow<List<DocumentRequest>> = _requests.asStateFlow()

    // Public civic data — Appwrite is authoritative; Room is cache only.
    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    private val _events = MutableStateFlow<List<BarangayEvent>>(emptyList())
    val events: StateFlow<List<BarangayEvent>> = _events.asStateFlow()

    private val _officials = MutableStateFlow<List<BarangayOfficial>>(emptyList())
    val officials: StateFlow<List<BarangayOfficial>> = _officials.asStateFlow()

    private val _hotlines = MutableStateFlow<List<OfficialHotline>>(emptyList())
    val hotlines: StateFlow<List<OfficialHotline>> = _hotlines.asStateFlow()

    private val _facilities = MutableStateFlow<List<Facility>>(emptyList())
    val facilities: StateFlow<List<Facility>> = _facilities.asStateFlow()

    // Announcements — persisted in Appwrite before local/cache refresh.
    suspend fun publishAnnouncement(
        title: String,
        description: String,
        category: AnnouncementCategory,
        priority: AnnouncementPriority,
        isPinned: Boolean
    ) {
        val user = _currentUser.value
        require(user.uid.isNotBlank()) { "You must be signed in." }
        CivicSyncService.createAnnouncement(
            title = title,
            description = description,
            category = category,
            priority = priority,
            isPinned = isPinned,
            authorName = user.profile.fullName,
            authorRole = user.role.displayName
        ).getOrThrow()
        refreshPublicData()
    }

    // Events
    suspend fun createEvent(
        title: String,
        description: String,
        date: String,
        time: String,
        location: String,
        organizer: String,
        category: String
    ) {
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a", Locale.US)
        val start = LocalDateTime.parse("$date $time", formatter).atZone(ZoneId.of("Asia/Manila")).toInstant()
        val end = start.plusSeconds(7200)
        CivicSyncService.createEvent(
            title = title,
            description = description,
            startsAt = start.toString(),
            endsAt = end.toString(),
            location = location,
            organizer = organizer,
            category = category
        ).getOrThrow()
        refreshPublicData()
    }

    fun toggleEventRsvp(eventId: String) {
        ioScope.launch {
            runCatching {
                val row = Appwrite.tablesDB().getRow(Appwrite.DATABASE_ID, Appwrite.EVENTS_TABLE, eventId)
                val current = row.data["rsvpCount"]?.toString()?.toIntOrNull() ?: 0
                Appwrite.tablesDB().updateRow(Appwrite.DATABASE_ID, Appwrite.EVENTS_TABLE, eventId, mapOf("rsvpCount" to current + 1))
                refreshPublicData()
            }.onFailure { _isOnline.value = false }
        }
    }

    // Notifications — Appwrite is authoritative; Room is cache only.
    fun markNotificationAsRead(id: String) {
        val userId = _currentUser.value.uid
        if (userId.isBlank()) return
        ioScope.launch {
            CivicSyncService.markNotificationRead(userId, id)
                .onSuccess { refreshAuthenticatedNotifications(userId) }
                .onFailure { _isOnline.value = false }
        }
    }

    fun markAllNotificationsAsRead() {
        val userId = _currentUser.value.uid
        if (userId.isBlank()) return
        ioScope.launch {
            val notifications = _notifications.value
            var failed = false
            for (notification in notifications.filter { !it.isRead }) {
                if (CivicSyncService.markNotificationRead(userId, notification.id).isFailure) {
                    failed = true
                    break
                }
            }
            if (failed) {
                _isOnline.value = false
            } else {
                refreshAuthenticatedNotifications(userId)
            }
        }
    }

    private fun addAuditLog(
        action: String,
        targetType: String,
        targetId: String,
        previousState: String?,
        newState: String?
    ) {
        val user = _currentUser.value
        val log = AuditLog(
            id = "log_${System.currentTimeMillis()}",
            actorUid = user.uid,
            actorName = user.profile.fullName.ifEmpty { "Barangay Official" },
            actorRole = user.role.displayName,
            action = action,
            targetType = targetType,
            targetId = targetId,
            previousState = previousState,
            newState = newState,
            timestamp = System.currentTimeMillis()
        )
        _auditLogs.value = listOf(log) + _auditLogs.value
        ioScope.launch {
            database?.auditLogDao()?.insertLog(AuditLogEntity.fromDomain(log))
        }
    }

    companion object {
        val instance by lazy { BarangayRepository() }
    }
}
