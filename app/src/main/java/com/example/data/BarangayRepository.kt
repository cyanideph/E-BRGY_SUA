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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    private val _emergencyReports = MutableStateFlow<List<EmergencyReport>>(emptyList())
    val emergencyReports: StateFlow<List<EmergencyReport>> = _emergencyReports.asStateFlow()

    private val _residents = MutableStateFlow<List<ResidentProfile>>(emptyList())
    val residents: StateFlow<List<ResidentProfile>> = _residents.asStateFlow()

    private val _households = MutableStateFlow<List<Household>>(emptyList())
    val households: StateFlow<List<Household>> = _households.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<BarangayNotification>>(emptyList())
    val notifications: StateFlow<List<BarangayNotification>> = _notifications.asStateFlow()

    // Audit Logs
    private val _auditLogs = MutableStateFlow(emptyList<AuditLog>())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    private var database: AppDatabase? = null
    private var appContext: Context? = null

    fun initLocalDb(context: Context) {
        if (database != null) return
        appContext = context.applicationContext
        val db = AppDatabase.getInstance(context)
        database = db

        ioScope.launch {
            runCatching {
                val account = Appwrite.account().get()
                val session = loadAuthenticatedSession()
                _currentUser.value = session
                _isOnline.value = true
                refreshAuthenticatedRequests(account.id)
                refreshAuthenticatedNotifications(account.id)
            }.onFailure { _isOnline.value = Appwrite.ENDPOINT.isNotBlank() }
        }

        ioScope.launch {
            db.documentRequestDao().getAllRequests().collect { entities ->
                if (entities.isNotEmpty()) {
                    _requests.value = entities.map { it.toDomain() }
                }
            }
        }

        ioScope.launch {
            db.emergencyReportDao().getAllEmergencies().collect { entities ->
                if (entities.isNotEmpty()) {
                    _emergencyReports.value = entities.map { it.toDomain() }
                }
            }
        }

        ioScope.launch {
            db.notificationDao().getAllNotifications().collect { entities ->
                if (entities.isNotEmpty()) {
                    _notifications.value = entities.map { it.toDomain() }
                }
            }
        }

        ioScope.launch {
            db.auditLogDao().getAllLogs().collect { entities ->
                if (entities.isNotEmpty()) {
                    _auditLogs.value = entities.map { it.toDomain() }
                }
            }
        }
    }

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val authMutex = Mutex()

    init { refreshPublicData() }

    private fun rowData(row: io.appwrite.models.Row<Map<String, Any>>): Map<String, Any> = row.data
    private fun str(data: Map<String, Any>, key: String) = data[key]?.toString().orEmpty()
    private fun bool(data: Map<String, Any>, key: String) = data[key]?.toString()?.toBooleanStrictOrNull() ?: false
    private fun int(data: Map<String, Any>, key: String) = data[key]?.toString()?.toIntOrNull() ?: 0

    private fun refreshPublicData() {
        ioScope.launch {
            if (Appwrite.ENDPOINT.isBlank() || Appwrite.DATABASE_ID.isBlank()) {
                _isOnline.value = false
                return@launch
            }
            try {
                val db = Appwrite.tablesDB()
                val servicesRes = db.listRows(Appwrite.DATABASE_ID, Appwrite.SERVICES_TABLE)
                if (servicesRes.rows.isNotEmpty()) {
                    _services.value = servicesRes.rows.map { row ->
                        val d = rowData(row)
                        BarangayService(row.id, str(d,"name"), str(d,"category"), str(d,"description"),
                            parseJsonArray(str(d,"purposeExamples")),
                            parseJsonArray(str(d,"requirements")).map { ServiceRequirement(it, it, true) },
                            str(d,"processingDays"), str(d,"feeDescription"), str(d,"iconKey"))
                    }
                }
                val announcementsRes = db.listRows(Appwrite.DATABASE_ID, Appwrite.ANNOUNCEMENTS_TABLE)
                if (announcementsRes.rows.isNotEmpty()) {
                    _announcements.value = announcementsRes.rows.map { row ->
                        val d = rowData(row)
                        Announcement(row.id, str(d,"title"), str(d,"body"),
                            runCatching { AnnouncementCategory.valueOf(str(d,"category").uppercase().replace(" & ","_").replace(" ","_")) }.getOrDefault(AnnouncementCategory.GENERAL),
                            runCatching { AnnouncementPriority.valueOf(str(d,"priority").uppercase()) }.getOrDefault(AnnouncementPriority.NORMAL),
                            str(d,"publishedAt").substringBefore("T"), str(d,"authorName"), str(d,"authorRole"), bool(d,"isPinned"))
                    }
                }
                val eventsRes = db.listRows(Appwrite.DATABASE_ID, Appwrite.EVENTS_TABLE)
                _events.value = eventsRes.rows.map { row ->
                    val d = rowData(row)
                    BarangayEvent(row.id, str(d,"title"), str(d,"description"), str(d,"startsAt").substringBefore("T"),
                        str(d,"startsAt").substringAfter("T").take(5), str(d,"location"), str(d,"organizer"), str(d,"category"), int(d,"rsvpCount"), false)
                }

                val officialsRes = db.listRows(Appwrite.DATABASE_ID, Appwrite.OFFICIALS_TABLE)
                _officials.value = officialsRes.rows.map { row ->
                    val d = rowData(row)
                    BarangayOfficial(
                        id = row.id,
                        name = str(d,"name"),
                        position = str(d,"position"),
                        roleCategory = str(d,"department"),
                        contactNumber = str(d,"phone"),
                        officeHours = str(d,"officeHours"),
                        committee = str(d,"committee"),
                        isDemoRecord = false,
                        email = str(d,"email")
                    )
                }

                val hotlinesRes = db.listRows(Appwrite.DATABASE_ID, Appwrite.HOTLINES_TABLE)
                _hotlines.value = hotlinesRes.rows.map { row ->
                    val d = rowData(row)
                    OfficialHotline(str(d,"name"), str(d,"number"), str(d,"agency"), str(d,"description"))
                }

                val householdsRes = db.listRows(Appwrite.DATABASE_ID, Appwrite.HOUSEHOLDS_TABLE)
                _households.value = householdsRes.rows.map { row ->
                    val d = rowData(row)
                    Household(
                        id = row.id,
                        householdNumber = str(d, "householdNumber"),
                        headName = str(d, "headName"),
                        address = str(d, "address"),
                        memberCount = int(d, "memberCount"),
                        memberNames = parseJsonArray(str(d, "memberNames")),
                        emergencyNotes = str(d, "emergencyNotes")
                    )
                }

                val facilitiesRes = db.listRows(Appwrite.DATABASE_ID, Appwrite.FACILITIES_TABLE)
                _facilities.value = facilitiesRes.rows.map { row ->
                    val d = rowData(row)
                    Facility(
                        id = row.id,
                        name = str(d,"name"),
                        type = str(d,"type"),
                        description = str(d,"description"),
                        address = str(d,"address"),
                        latitude = d["latitude"]?.toString()?.toDoubleOrNull(),
                        longitude = d["longitude"]?.toString()?.toDoubleOrNull(),
                        phone = d["phone"]?.toString(),
                        hours = d["hours"]?.toString(),
                        emergencyAvailable = bool(d,"emergencyAvailable"),
                        active = bool(d,"active")
                    )
                }

                _isOnline.value = true
            } catch (_: Exception) { _isOnline.value = false }
        }
    }

    private suspend fun refreshAuthenticatedRequests(userId: String) {
        val remote = CivicSyncService.listRequestsForUser(userId).getOrElse { return }
        val current = _requests.value.toMutableList()
        val knownRefs = current.map { it.referenceNumber }.toMutableSet()
        remote.forEach { row ->
            val ref = row["referenceNumber"]?.toString().orEmpty()
            val idx = current.indexOfFirst { it.referenceNumber == ref }
            if (ref.isBlank()) return@forEach
            if (idx >= 0) {
                val old = current[idx]
                val mapped = RequestStatus.values().firstOrNull { it.label.equals(row["status"]?.toString().orEmpty(), true) } ?: old.status
                current[idx] = old.copy(status = mapped, officialRemarks = row["officialRemarks"]?.toString() ?: old.officialRemarks)
                return@forEach
            }
            if (knownRefs.contains(ref)) return@forEach
            val serviceId = row["serviceId"]?.toString().orEmpty()
            val service = _services.value.firstOrNull { it.id == serviceId }
            val details = row["details"]?.toString().orEmpty().split("|", limit = 3)
            val submittedAt = runCatching { java.time.Instant.parse(row["submittedAt"]?.toString().orEmpty()).toEpochMilli() }.getOrDefault(System.currentTimeMillis())
            val status = RequestStatus.values().firstOrNull { it.label.equals(row["status"]?.toString().orEmpty(), true) } ?: RequestStatus.SUBMITTED
            val request = DocumentRequest(
                id = row["id"]?.toString() ?: ref, referenceNumber = ref, serviceId = serviceId,
                serviceName = service?.name ?: "Barangay Service", residentUid = userId,
                residentName = _currentUser.value.profile.fullName, residentAddress = _currentUser.value.profile.address,
                residentContact = _currentUser.value.profile.mobileNumber, purpose = details.getOrNull(0).orEmpty(),
                deliveryMethod = details.getOrNull(1).orEmpty(), remarks = details.getOrNull(2).orEmpty(), status = status,
                officialRemarks = row["officialRemarks"]?.toString().orEmpty(), attachmentNames = emptyList(),
                createdAt = submittedAt, updatedAt = submittedAt,
                timeline = listOf(RequestTimelineEvent("Application Submitted", "Request synchronized from Barangay Sua backend", submittedAt, _currentUser.value.profile.fullName)),
                isSyncedToServer = true
            )
            current.add(0, request)
            knownRefs.add(ref)
            database?.documentRequestDao()?.insertRequest(DocumentRequestEntity.fromDomain(request))
        }
        _requests.value = current
    }

    private suspend fun refreshAuthenticatedNotifications(userId: String) {
        val remote = CivicSyncService.listNotificationsForUser(userId).getOrElse { return }
        _notifications.value = remote
        remote.forEach { notification ->
            database?.notificationDao()?.insertNotification(NotificationEntity.fromDomain(notification))
        }
    }

    private fun parseJsonArray(value: String): List<String> = runCatching {
        val a = org.json.JSONArray(value)
        List(a.length()) { i -> a.optString(i) }.filter { it.isNotBlank() }
    }.getOrDefault(emptyList())

    private fun roleFromBackend(value: String?): UserRole = when (value?.trim()?.lowercase()) {
        "admin" -> UserRole.ADMIN
        "official" -> UserRole.OFFICIAL
        "staff" -> UserRole.STAFF
        else -> UserRole.RESIDENT
    }

    private suspend fun loadAuthenticatedSession(): UserSession {
        val account = Appwrite.account().get()
        val fallbackName = account.name.ifBlank { account.email.substringBefore("@") }
        val userRow = runCatching {
            Appwrite.tablesDB()
                .listRows(Appwrite.DATABASE_ID, Appwrite.USERS_TABLE)
                .rows.firstOrNull { it.data["userId"]?.toString() == account.id }
        }.getOrNull()
        val userData = userRow?.data ?: emptyMap()
        val residentRow = runCatching {
            Appwrite.tablesDB()
                .listRows(Appwrite.DATABASE_ID, Appwrite.RESIDENTS_TABLE)
                .rows.firstOrNull { it.data["userId"]?.toString() == account.id }
        }.getOrNull()
        val residentData = residentRow?.data ?: emptyMap()
        val role = roleFromBackend(userData["role"]?.toString())
        val profile = ResidentProfile(
            id = residentRow?.id ?: account.id,
            residentId = residentData["residentId"]?.toString()?.ifBlank { account.id } ?: account.id,
            fullName = residentData["fullName"]?.toString()?.ifBlank { userData["name"]?.toString() ?: fallbackName } ?: fallbackName,
            address = residentData["address"]?.toString() ?: userData["address"]?.toString().orEmpty(),
            mobileNumber = residentData["mobileNumber"]?.toString() ?: userData["phone"]?.toString().orEmpty(),
            dateOfBirth = residentData["birthDate"]?.toString().orEmpty(),
            civilStatus = residentData["civilStatus"]?.toString().orEmpty(),
            sex = residentData["sex"]?.toString().orEmpty(),
            occupation = residentData["occupation"]?.toString().orEmpty(),
            householdId = residentData["householdId"]?.toString().orEmpty(),
            registrationStatus = residentData["registrationStatus"]?.toString()?.ifBlank { "Account Registered" } ?: "Account Registered",
            emergencyContactName = residentData["emergencyContactName"]?.toString().orEmpty(),
            emergencyContactRelationship = residentData["emergencyContactRelationship"]?.toString().orEmpty(),
            emergencyContactPhone = residentData["emergencyContactPhone"]?.toString().orEmpty(),
            latitude = residentData["latitude"]?.toString()?.toDoubleOrNull(),
            longitude = residentData["longitude"]?.toString()?.toDoubleOrNull()
        )
        return UserSession(account.id, account.email, role, profile)
    }

    suspend fun login(email: String, password: String): Result<UserSession> = authMutex.withLock {
        runCatching {
            require(Appwrite.ENDPOINT.isNotBlank()) { "Appwrite is not configured for this build." }

            // Appwrite persists the mobile session. Reuse an existing authenticated
            // session instead of attempting to create a second one.
            val account = Appwrite.account()
            val existingSession = runCatching { account.get() }.getOrNull()
            if (existingSession == null) {
                account.createEmailPasswordSession(
                    email = email.trim(),
                    password = password
                )
            }

            val session = loadAuthenticatedSession()
            _currentUser.value = session
            _isOnline.value = true
            refreshAuthenticatedRequests(session.uid)
            refreshAuthenticatedNotifications(session.uid)
            session
        }
    }

    suspend fun register(
        fullName: String, email: String, password: String, mobile: String, address: String,
        dateOfBirth: String, civilStatus: String, occupation: String,
        emergencyContactName: String, emergencyContactRelationship: String, emergencyContactPhone: String,
        latitude: Double?, longitude: Double?
    ): Result<UserSession> = authMutex.withLock { runCatching {
        require(Appwrite.ENDPOINT.isNotBlank()) { "Appwrite is not configured for this build." }
        val created = Appwrite.account().create(userId = ID.unique(), email = email, password = password, name = fullName)
        Appwrite.account().createEmailPasswordSession(email = email, password = password)
        val now = java.time.Instant.now().toString()
        val normalizedBirthDate = dateOfBirth.takeIf { it.isNotBlank() }?.let { java.time.LocalDate.parse(it).atStartOfDay(java.time.ZoneId.of("Asia/Manila")).toInstant().toString() }
        val userPermissions = listOf("read(\"user:${created.id}\")", "update(\"user:${created.id}\")")
        Appwrite.tablesDB().createRow(databaseId = Appwrite.DATABASE_ID, tableId = Appwrite.USERS_TABLE, rowId = created.id,
            data = mapOf("userId" to created.id, "name" to fullName, "email" to email, "role" to "resident", "address" to address, "phone" to mobile, "createdAt" to now), permissions = userPermissions)
        Appwrite.tablesDB().createRow(databaseId = Appwrite.DATABASE_ID, tableId = Appwrite.RESIDENTS_TABLE, rowId = created.id,
            data = mapOf(
                "userId" to created.id, "fullName" to fullName, "address" to address, "mobileNumber" to mobile,
                "birthDate" to normalizedBirthDate, "civilStatus" to civilStatus, "occupation" to occupation,
                "emergencyContactName" to emergencyContactName, "emergencyContactRelationship" to emergencyContactRelationship,
                "emergencyContactPhone" to emergencyContactPhone, "latitude" to latitude, "longitude" to longitude,
                "residentId" to created.id, "verified" to false, "registrationStatus" to "Pending Verification", "createdAt" to now
            ), permissions = userPermissions)
        val session = loadAuthenticatedSession()
        _currentUser.value = session
        _isOnline.value = true
        session
    }
    }

    suspend fun logout() {
        runCatching {
            if (Appwrite.ENDPOINT.isNotBlank()) {
                Appwrite.account().deleteSession("current")
            }
        }
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

    // Submit Document Request. The backend is authoritative; local Room is only a cache.
    suspend fun submitRequest(
        service: BarangayService,
        purpose: String,
        deliveryMethod: String,
        remarks: String,
        attachmentNames: List<String>
    ): DocumentRequest {
        val user = _currentUser.value
        require(user.uid.isNotBlank()) { "You must be signed in before submitting a request." }
        require(Appwrite.ENDPOINT.isNotBlank()) { "Barangay services are unavailable because the backend is not configured." }

        val requestId = ID.unique()
        val refNum = "BRG-SUA-${requestId.take(12).uppercase()}"
        val now = System.currentTimeMillis()
        val newRequest = DocumentRequest(
            id = requestId,
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
            officialRemarks = "",
            attachmentNames = attachmentNames,
            createdAt = now,
            updatedAt = now,
            timeline = listOf(
                RequestTimelineEvent(
                    title = "Application Submitted",
                    description = "Request accepted by the barangay backend",
                    timestamp = now,
                    actorName = user.profile.fullName
                )
            ),
            isSyncedToServer = true
        )

        CivicSyncService.createRequest(newRequest).getOrThrow()
        _isOnline.value = true

        val updatedList = listOf(newRequest) + _requests.value.filter { it.id != newRequest.id }
        _requests.value = updatedList
        database?.documentRequestDao()?.insertRequest(DocumentRequestEntity.fromDomain(newRequest))
        refreshAuthenticatedNotifications(user.uid)

        appContext?.let { ctx ->
            NotificationHelper.showNotification(
                context = ctx,
                id = newRequest.hashCode(),
                title = "Request Submitted: ${service.name}",
                message = "Reference number ${refNum} was accepted by the barangay backend."
            )
        }

        return newRequest
    }

    // Update Request Status (Staff / Admin)
    fun updateRequestStatus(
        requestId: String,
        newStatus: RequestStatus,
        officialRemarks: String
    ) {
        val user = _currentUser.value
        ioScope.launch {
            val index = _requests.value.indexOfFirst { it.id == requestId || it.referenceNumber == requestId }
            if (index < 0) return@launch
            val old = _requests.value[index]
            val result = CivicSyncService.updateRequestStatus(old.id, newStatus.label, officialRemarks, user.uid)
            if (result.isFailure) {
                _isOnline.value = false
                return@launch
            }

            _isOnline.value = true
            val now = System.currentTimeMillis()
            val updated = old.copy(
                status = newStatus,
                officialRemarks = officialRemarks,
                updatedAt = now,
                timeline = old.timeline + RequestTimelineEvent(
                    title = "Status: ${newStatus.label}",
                    description = officialRemarks,
                    timestamp = now,
                    actorName = user.profile.fullName
                ),
                isSyncedToServer = true
            )
            val list = _requests.value.toMutableList()
            val currentIndex = list.indexOfFirst { it.id == old.id }
            if (currentIndex >= 0) {
                list[currentIndex] = updated
                _requests.value = list
            }
            database?.documentRequestDao()?.insertRequest(DocumentRequestEntity.fromDomain(updated))
            refreshAuthenticatedNotifications(user.uid)
        }
    }

    // Emergency / SOS. The backend must accept the report before it is shown as dispatched.
    suspend fun submitEmergency(
        type: EmergencyType,
        description: String,
        latitude: Double?,
        longitude: Double?,
        locationDescription: String
    ): EmergencyReport {
        val user = _currentUser.value
        require(user.uid.isNotBlank()) { "You must be signed in before sending an SOS." }
        require(description.isNotBlank()) { "Please describe the emergency." }
        val now = System.currentTimeMillis()
        val report = EmergencyReport(
            id = ID.unique(),
            type = type,
            description = description,
            residentName = user.profile.fullName,
            residentContact = user.profile.mobileNumber,
            residentUid = user.uid,
            latitude = latitude,
            longitude = longitude,
            locationDescription = locationDescription,
            timestamp = now,
            status = EmergencyStatus.RECEIVED,
            assignedResponder = ""
        )

        CivicSyncService.createEmergency(report).getOrThrow()
        _isOnline.value = true
        _emergencyReports.value = listOf(report) + _emergencyReports.value
        database?.emergencyReportDao()?.insertEmergency(EmergencyReportEntity.fromDomain(report))
        refreshAuthenticatedNotifications(user.uid)

        appContext?.let { ctx ->
            NotificationHelper.showNotification(
                context = ctx,
                id = report.id.hashCode(),
                title = "Emergency Report Accepted",
                message = "Your emergency report was accepted by the barangay backend.",
                isEmergency = true
            )
        }
        return report
    }

    suspend fun updateEmergencyStatus(
        reportId: String,
        newStatus: EmergencyStatus,
        assignedResponder: String,
        notes: String
    ) {
        val user = _currentUser.value
        val status = when (newStatus) {
            EmergencyStatus.RECEIVED -> "Reported"
            EmergencyStatus.RESPONDING -> "Responding"
            EmergencyStatus.RESOLVED -> "Resolved"
        }
        CivicSyncService.updateEmergencyStatus(
            reportId = reportId,
            status = status,
            responder = assignedResponder,
            notes = notes,
            actorUid = user.uid
        ).getOrThrow()

        val list = _emergencyReports.value.toMutableList()
        val index = list.indexOfFirst { it.id == reportId }
        if (index >= 0) {
            val old = list[index]
            list[index] = old.copy(
                status = newStatus,
                assignedResponder = assignedResponder,
                responseNotes = notes
            )
            _emergencyReports.value = list
            database?.emergencyReportDao()?.insertEmergency(EmergencyReportEntity.fromDomain(list[index]))
        }
        refreshAuthenticatedNotifications(user.uid)
    }

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
