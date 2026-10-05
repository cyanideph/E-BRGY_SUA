package com.example.data

import com.example.model.*
import com.example.services.Appwrite
import com.example.services.CivicSyncService
import io.appwrite.ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
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
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    // Services
    private val _services = MutableStateFlow(emptyList<BarangayService>())
    val services: StateFlow<List<BarangayService>> = _services.asStateFlow()

    // Document Requests
    private val _requests = MutableStateFlow(emptyList<DocumentRequest>())
    val requests: StateFlow<List<DocumentRequest>> = _requests.asStateFlow()

    // Announcements
    private val _announcements = MutableStateFlow(emptyList<Announcement>())
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    // Events
    private val _events = MutableStateFlow(emptyList<BarangayEvent>())
    val events: StateFlow<List<BarangayEvent>> = _events.asStateFlow()

    // Emergency Reports
    private val _emergencyReports = MutableStateFlow(emptyList<EmergencyReport>())
    val emergencyReports: StateFlow<List<EmergencyReport>> = _emergencyReports.asStateFlow()

    // Residents (Admin / Staff only)
    private val _residents = MutableStateFlow(emptyList<ResidentProfile>())
    val residents: StateFlow<List<ResidentProfile>> = _residents.asStateFlow()

    // Households
    private val _households = MutableStateFlow(emptyList<Household>())
    val households: StateFlow<List<Household>> = _households.asStateFlow()

    // Officials
    private val _officials = MutableStateFlow(emptyList<BarangayOfficial>())
    val officials: StateFlow<List<BarangayOfficial>> = _officials.asStateFlow()

    // Hotlines
    val hotlines: List<OfficialHotline> = emptyList()

    // Notifications
    private val _notifications = MutableStateFlow(emptyList<BarangayNotification>())
    val notifications: StateFlow<List<BarangayNotification>> = _notifications.asStateFlow()

    // Audit Logs
    private val _auditLogs = MutableStateFlow(emptyList<AuditLog>())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    private var requestCounter = 124

    private val ioScope = CoroutineScope(Dispatchers.IO)

    init { refreshPublicData() }

    private fun rowData(row: io.appwrite.models.Row<Map<String, Any>>): Map<String, Any> = row.data
    private fun str(data: Map<String, Any>, key: String) = data[key]?.toString().orEmpty()
    private fun bool(data: Map<String, Any>, key: String) = data[key]?.toString()?.toBooleanStrictOrNull() ?: false
    private fun int(data: Map<String, Any>, key: String) = data[key]?.toString()?.toIntOrNull() ?: 0

    private fun refreshPublicData() {
        ioScope.launch {
            try {
                val db = Appwrite.tablesDB()
                _services.value = db.listRows(Appwrite.DATABASE_ID, Appwrite.SERVICES_TABLE).rows.map { row ->
                    val d = rowData(row)
                    BarangayService(row.id, str(d,"name"), str(d,"category"), str(d,"description"),
                        parseJsonArray(str(d,"purposeExamples")),
                        parseJsonArray(str(d,"requirements")).map { ServiceRequirement(it, it, true) },
                        str(d,"processingDays"), str(d,"feeDescription"), str(d,"iconKey"))
                }
                _announcements.value = db.listRows(Appwrite.DATABASE_ID, Appwrite.ANNOUNCEMENTS_TABLE).rows.map { row ->
                    val d = rowData(row)
                    Announcement(row.id, str(d,"title"), str(d,"body"),
                        runCatching { AnnouncementCategory.valueOf(str(d,"category").uppercase().replace(" & ","_").replace(" ","_")) }.getOrDefault(AnnouncementCategory.GENERAL),
                        runCatching { AnnouncementPriority.valueOf(str(d,"priority").uppercase()) }.getOrDefault(AnnouncementPriority.NORMAL),
                        str(d,"publishedAt").substringBefore("T"), str(d,"authorName"), str(d,"authorRole"), bool(d,"isPinned"))
                }
                _events.value = db.listRows(Appwrite.DATABASE_ID, Appwrite.EVENTS_TABLE).rows.map { row ->
                    val d = rowData(row)
                    BarangayEvent(row.id, str(d,"title"), str(d,"description"), str(d,"startsAt").substringBefore("T"),
                        str(d,"startsAt").substringAfter("T").take(5), str(d,"location"), str(d,"organizer"), str(d,"category"), int(d,"rsvpCount"), false)
                }
                _isOnline.value = true
            } catch (_: Exception) { _isOnline.value = false }
        }
    }

    private fun parseJsonArray(value: String): List<String> = runCatching {
        val a = org.json.JSONArray(value)
        List(a.length()) { i -> a.optString(i) }.filter { it.isNotBlank() }
    }.getOrDefault(emptyList())

    suspend fun login(email: String, password: String): Result<UserSession> = runCatching {
        Appwrite.account().createEmailPasswordSession(email=email, password=password)
        val a = Appwrite.account().get()
        val s = UserSession(a.id, a.email, UserRole.RESIDENT,
            ResidentProfile(id=a.id, residentId=a.id, fullName=a.name.ifBlank { a.email.substringBefore("@") },
                registrationStatus="Account Registered"))
        _currentUser.value=s; _isOnline.value=true; s
    }

    suspend fun register(fullName: String, email: String, password: String, mobile: String, address: String): Result<UserSession> = runCatching {
        val created = Appwrite.account().create(userId=ID.unique(), email=email, password=password, name=fullName)
        Appwrite.account().createEmailPasswordSession(email=email, password=password)
        val now = java.time.Instant.now().toString()
        val userPermissions = listOf("read(\"user:${created.id}\")", "update(\"user:${created.id}\")")
        Appwrite.tablesDB().createRow(
            databaseId=Appwrite.DATABASE_ID, tableId=Appwrite.USERS_TABLE, rowId=created.id,
            data=mapOf("userId" to created.id, "name" to fullName, "email" to email, "role" to "resident", "address" to address, "createdAt" to now),
            permissions=userPermissions
        )
        Appwrite.tablesDB().createRow(
            databaseId=Appwrite.DATABASE_ID, tableId=Appwrite.RESIDENTS_TABLE, rowId=created.id,
            data=mapOf("userId" to created.id, "fullName" to fullName, "address" to address, "mobileNumber" to mobile, "residentId" to created.id, "verified" to false, "registrationStatus" to "Pending Verification", "createdAt" to now),
            permissions=userPermissions
        )
        val s = UserSession(created.id, created.email, UserRole.RESIDENT,
            ResidentProfile(id=created.id, residentId=created.id, fullName=fullName, address=address, mobileNumber=mobile,
                registrationStatus="Pending Verification"))
        _currentUser.value=s; _isOnline.value=true; s
    }

    suspend fun logout() {
        runCatching { Appwrite.account().deleteSession("current") }
        _currentUser.value=UserSession()
    }


    fun updateProfile(updated: ResidentProfile) {
        val current = _currentUser.value
        _currentUser.value = current.copy(profile = updated)
        val list = _residents.value.toMutableList()
        val index = list.indexOfFirst { it.id == updated.id || it.residentId == updated.residentId }
        if (index >= 0) {
            list[index] = updated
            _residents.value = list
        }
    }

    fun toggleOnline(online: Boolean) {
        _isOnline.value = online
    }

    // Submit Document Request
    fun submitRequest(
        service: BarangayService,
        purpose: String,
        deliveryMethod: String,
        remarks: String,
        attachmentNames: List<String>
    ): DocumentRequest {
        val user = _currentUser.value
        val refNum = "BRG-SUA-2026-${String.format(Locale.US, "%06d", requestCounter++)}"
        val now = System.currentTimeMillis()

        val newRequest = DocumentRequest(
            id = "req_${System.currentTimeMillis()}",
            referenceNumber = refNum,
            serviceId = service.id,
            serviceName = service.name,
            residentUid = user.uid,
            residentName = user.profile.fullName,
            residentAddress = user.profile.address,
            residentContact = user.profile.mobileNumber,
            purpose = purpose,
            deliveryMethod = deliveryMethod,
            remarks = remarks,
            status = RequestStatus.SUBMITTED,
            officialRemarks = "Application received. Queue position logged.",
            attachmentNames = attachmentNames,
            createdAt = now,
            updatedAt = now,
            timeline = listOf(
                RequestTimelineEvent(
                    title = "Application Submitted",
                    description = "Request submitted online via e-Barangay Sua",
                    timestamp = now,
                    actorName = user.profile.fullName
                )
            ),
            isSyncedToServer = _isOnline.value
        )

        val updatedList = listOf(newRequest) + _requests.value
        _requests.value = updatedList

        // Persist the same mutation server-side without blocking the UI.
        ioScope.launch {
            val sync = CivicSyncService.createRequest(newRequest)
            if (sync.isFailure) _isOnline.value = false
        }

        // In-app notification
        val notif = BarangayNotification(
            id = "notif_${System.currentTimeMillis()}",
            title = "Request Submitted: ${service.name}",
            message = "Your request with reference number $refNum has been received.",
            timestamp = now,
            isRead = false,
            category = "Service Request",
            priority = "Normal",
            referenceId = refNum
        )
        _notifications.value = listOf(notif) + _notifications.value
        ioScope.launch { CivicSyncService.createNotification(user.uid, notif) }

        addAuditLog(
            action = "CREATE_REQUEST",
            targetType = "DocumentRequest",
            targetId = refNum,
            previousState = null,
            newState = RequestStatus.SUBMITTED.label
        )

        return newRequest
    }

    // Update Request Status (Staff / Admin)
    fun updateRequestStatus(
        requestId: String,
        newStatus: RequestStatus,
        officialRemarks: String
    ) {
        val user = _currentUser.value
        val list = _requests.value.toMutableList()
        val index = list.indexOfFirst { it.id == requestId || it.referenceNumber == requestId }
        if (index >= 0) {
            val old = list[index]
            val now = System.currentTimeMillis()
            val newTimeline = old.timeline + RequestTimelineEvent(
                title = "Status: ${newStatus.label}",
                description = officialRemarks.ifEmpty { "Status updated by ${user.role.displayName}" },
                timestamp = now,
                actorName = user.profile.fullName
            )
            val updated = old.copy(
                status = newStatus,
                officialRemarks = officialRemarks,
                updatedAt = now,
                timeline = newTimeline
            )
            list[index] = updated
            _requests.value = list

            // Notification for resident
            val notif = BarangayNotification(
                id = "notif_${System.currentTimeMillis()}",
                title = "Request Update: ${old.serviceName}",
                message = "Status changed to ${newStatus.label}. $officialRemarks",
                timestamp = now,
                isRead = false,
                category = "Service Request",
                priority = if (newStatus == RequestStatus.READY) "Important" else "Normal",
                referenceId = old.referenceNumber
            )
            _notifications.value = listOf(notif) + _notifications.value

            addAuditLog(
                action = "UPDATE_REQUEST_STATUS",
                targetType = "DocumentRequest",
                targetId = old.referenceNumber,
                previousState = old.status.label,
                newState = newStatus.label
            )
        }
    }

    // Emergency / SOS
    fun submitEmergency(
        type: EmergencyType,
        description: String,
        latitude: Double?,
        longitude: Double?,
        locationDescription: String
    ): EmergencyReport {
        val user = _currentUser.value
        val now = System.currentTimeMillis()
        val report = EmergencyReport(
            id = "emg_${System.currentTimeMillis()}",
            type = type,
            description = description,
            residentName = user.profile.fullName,
            residentContact = user.profile.mobileNumber,
            residentUid = user.uid,
            latitude = latitude,
            longitude = longitude,
            locationDescription = locationDescription.ifEmpty { "Barangay Sua, San Juan, Southern Leyte" },
            timestamp = now,
            status = EmergencyStatus.RECEIVED,
            assignedResponder = "Barangay Tanod Immediate Dispatch"
        )
        _emergencyReports.value = listOf(report) + _emergencyReports.value

        // Persist SOS + lifecycle history + audit trail.
        ioScope.launch {
            val sync = CivicSyncService.createEmergency(report)
            if (sync.isFailure) _isOnline.value = false
        }

        val notif = BarangayNotification(
            id = "notif_${System.currentTimeMillis()}",
            title = "SOS Emergency Logged",
            message = "Your ${type.displayName} alert was broadcast to Barangay Tanod & San Juan MDRRMO.",
            timestamp = now,
            isRead = false,
            category = "Emergency",
            priority = "Emergency",
            referenceId = report.id
        )
        _notifications.value = listOf(notif) + _notifications.value

        addAuditLog(
            action = "EMERGENCY_SOS",
            targetType = "EmergencyReport",
            targetId = report.id,
            previousState = null,
            newState = type.displayName
        )
        return report
    }

    fun updateEmergencyStatus(
        reportId: String,
        newStatus: EmergencyStatus,
        assignedResponder: String,
        notes: String
    ) {
        val list = _emergencyReports.value.toMutableList()
        val index = list.indexOfFirst { it.id == reportId }
        if (index >= 0) {
            val old = list[index]
            val updated = old.copy(
                status = newStatus,
                assignedResponder = assignedResponder.ifEmpty { old.assignedResponder },
                responseNotes = notes
            )
            list[index] = updated
            _emergencyReports.value = list

            addAuditLog(
                action = "UPDATE_EMERGENCY_STATUS",
                targetType = "EmergencyReport",
                targetId = reportId,
                previousState = old.status.label,
                newState = newStatus.label
            )
        }
    }

    // Announcements
    fun publishAnnouncement(
        title: String,
        description: String,
        category: AnnouncementCategory,
        priority: AnnouncementPriority,
        isPinned: Boolean
    ) {
        val user = _currentUser.value
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)
        val now = System.currentTimeMillis()
        val announcement = Announcement(
            id = "ann_${now}",
            title = title,
            description = description,
            category = category,
            priority = priority,
            publishedDate = dateFormat.format(Date(now)),
            authorName = user.profile.fullName,
            authorRole = user.role.displayName,
            isPinned = isPinned
        )
        _announcements.value = listOf(announcement) + _announcements.value

        val notif = BarangayNotification(
            id = "notif_$now",
            title = "New Official Announcement",
            message = title,
            timestamp = now,
            isRead = false,
            category = "Announcement",
            priority = if (priority == AnnouncementPriority.EMERGENCY) "Emergency" else "Normal",
            referenceId = announcement.id
        )
        _notifications.value = listOf(notif) + _notifications.value

        addAuditLog(
            action = "PUBLISH_ANNOUNCEMENT",
            targetType = "Announcement",
            targetId = announcement.id,
            previousState = null,
            newState = title
        )
    }

    // Events
    fun toggleEventRsvp(eventId: String) {
        val list = _events.value.toMutableList()
        val index = list.indexOfFirst { it.id == eventId }
        if (index >= 0) {
            val old = list[index]
            val newRsvp = !old.isUserRsvpd
            val newCount = if (newRsvp) old.rsvpCount + 1 else maxOf(0, old.rsvpCount - 1)
            list[index] = old.copy(isUserRsvpd = newRsvp, rsvpCount = newCount)
            _events.value = list
        }
    }

    fun createEvent(
        title: String,
        description: String,
        date: String,
        time: String,
        location: String,
        organizer: String,
        category: String
    ) {
        val event = BarangayEvent(
            id = "evt_${System.currentTimeMillis()}",
            title = title,
            description = description,
            date = date,
            time = time,
            location = location,
            organizer = organizer,
            category = category
        )
        _events.value = listOf(event) + _events.value
        addAuditLog(
            action = "CREATE_EVENT",
            targetType = "BarangayEvent",
            targetId = event.id,
            previousState = null,
            newState = title
        )
    }

    // Notifications
    fun markNotificationAsRead(id: String) {
        val list = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
        _notifications.value = list
    }

    fun markAllNotificationsAsRead() {
        val list = _notifications.value.map { it.copy(isRead = true) }
        _notifications.value = list
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
    }

    companion object {
        val instance by lazy { BarangayRepository() }
    }
}
