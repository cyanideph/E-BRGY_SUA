package com.example.data.remote

import com.example.domain.model.Resident
import com.example.domain.model.ServiceRequest
import com.example.domain.model.EmergencyReport
import com.example.domain.model.Announcement
import com.example.domain.model.BarangayOfficial
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine

/** Appwrite data source interface - authoritative backend for all domain data */
interface AppwriteDataSource {

    // =========================================================================
    // Resident
    // =========================================================================

    fun getResident(id: String): Flow<Resident?>
    fun observeAuthState(): Flow<Boolean>

    // =========================================================================
    // Service Requests
    // =========================================================================

    fun getServiceRequests(userId: String): Flow<List<ServiceRequest>>
    fun createServiceRequest(request: ServiceRequest): Flow<String>
    fun updateServiceRequestStatus(requestId: String, status: String): Flow<Unit>

    // =========================================================================
    // Emergency Reports
    // =========================================================================

    fun getEmergencyReports(userId: String): Flow<List<EmergencyReport>>
    fun reportEmergency(report: EmergencyReport): Flow<String>
    fun subscribeEmergencyUpdates(userId: String): Flow<List<EmergencyReport>>

    // =========================================================================
    // Announcements
    // =========================================================================

    fun getAnnouncements(): Flow<List<Announcement>>
    fun createAnnouncement(announcement: Announcement): Flow<String>
    fun deleteAnnouncement(id: String): Flow<Unit>

    // =========================================================================
    // Barangay Official
    // =========================================================================

    fun getOfficials(): Flow<List<BarangayOfficial>>
    fun getOnDutyOfficial(): Flow<BarangayOfficial?>

    // =========================================================================
    // Authentication
    // =========================================================================

    fun signInWithEmail(email: String, password: String): Flow<String>
    fun signOut(): Flow<Unit>
    fun registerWithEmail(email: String, password: String): Flow<String>
}

/** Appwrite Resident data source implementation */
class AppwriteResidentDataSource @Inject constructor() : AppwriteDataSource {

    private val appwrite = Appwrite.getInstance()
    private val service = appwrite.services.users()

    override fun getResident(id: String): Flow<Resident?> =
        flow {
            suspendCancellableCoroutine { cotc ->
                service.get(id).execute { response ->
                    cotc.resume(if (response.isOk) {
                        Resident(
                            id = response.documentId,
                            fullName = response.data["full_name"]?.toString() ?: "",
                            address = response.data["address"]?.toString() ?: "",
                            birthDate = response.data["birthdate"]?.toString() ?: "",
                            age = response.data["age"]?.toInt() ?: 0,
                            profileImageId = response.data["profile_image_id"]?.toString(),
                            status = response.data["status"]?.toString() ?: "ACTIVE",
                            dateCreated = response.data["date_created"]?.toString() ?: "",
                            lastLogin = response.data["last_login"]?.toString()
                        )
                    } else {
                        null
                    })
                }
            }
        }.catch { /* log error */ }

    override fun observeAuthState(): Flow<Boolean> =
        flow {
            // Implementation for auth state observation
            emit(true)
        }
}

/** Appwrite ServiceRequest data source implementation */
class AppwriteServiceRequestDataSource @Inject constructor() : AppwriteDataSource {

    private val appwrite = Appwrite.getInstance()
    private val database = appwrite.services.databases()

    override fun getServiceRequests(userId: String): Flow<List<ServiceRequest>> =
        flow {
            suspendCancellableCoroutine { cotc ->
                database.listDocuments(
                    databaseId = "ebarangay-sua-db",
                    collectionId = "service_requests",
                    queries = ["user_id" = userId]
                ).execute { response ->
                    if (response.isOk) {
                        val docs = response.documents
                        val requests = docs.map { doc ->
                            ServiceRequest(
                                type = doc.data["type"]?.toString() ?: "",
                                status = doc.data["status"]?.toString() ?: "PENDING",
                                requestedBy = doc.data["requested_by"]?.toString() ?: "",
                                description = doc.data["description"]?.toString() ?: "",
                                requiredDocuments = (doc.data["required_documents"] as? List<??>) ?: emptyList(),
                                requestDate = doc.data["created_at"]?.toString() ?: ""
                            )
                        }
                        cotc.resume(requests)
                    } else {
                        cotc.resume(emptyList())
                    }
                }
            }.catch { emit(emptyList()) }
        }

    override fun createServiceRequest(request: ServiceRequest): Flow<String> =
        flow {
            suspendCancellableCoroutine { cotc ->
                database.createDocument(
                    databaseId = "ebarangay-sua-db",
                    collectionId = "service_requests",
                    documentId = java.util.UUID.randomUUID().toString(),
                    data = mapOf(
                        "type" to request.type,
                        "status" to request.status,
                        "requested_by" to request.requestedBy,
                        "description" to request.description,
                        "required_documents" to request.requiredDocuments,
                        "created_at" to request.requestDate
                    )
                ).execute { response ->
                    if (response.isOk) {
                        cotc.resume(response.documentId)
                    } else {
                        cotc.resume("")
                    }
                }
            }.catch { emit("") }
        }
}

/** Appwrite EmergencyReport data source implementation */
class AppwriteEmergencyReportDataSource @Inject constructor() : AppwriteDataSource {

    private val appwrite = Appwrite.getInstance()
    private val database = appwrite.services.databases()

    override fun getEmergencyReports(userId: String): Flow<List<EmergencyReport>> =
        flow {
            suspendCancellableCoroutine { cotc ->
                database.listDocuments(
                    databaseId = "ebarangay-sua-db",
                    collectionId = "emergency_reports",
                    queries = ["reported_by" = userId]
                ).execute { response ->
                    if (response.isOk) {
                        val docs = response.documents
                        val reports = docs.map { doc ->
                            EmergencyReport(
                                type = doc.data["type"]?.toString() ?: "",
                                severity = doc.data["severity"]?.toString() ?: "LOW",
                                reportedBy = doc.data["reported_by"]?.toString() ?: "",
                                location = doc.data["location"]?.toString() ?: "",
                                description = doc.data["description"]?.toString() ?: "",
                                reportedAt = doc.data["reported_at"]?.toString() ?: "",
                                status = doc.data["status"]?.toString() ?: "ACTIVE"
                            )
                        }
                        cotc.resume(reports)
                    } else {
                        cotc.resume(emptyList())
                    }
                }
            }.catch { emit(emptyList()) }
        }

    override fun reportEmergency(report: EmergencyReport): Flow<String> =
        flow {
            suspendCancellableCoroutine { cotc ->
                database.createDocument(
                    databaseId = "ebarangay-sua-db",
                    collectionId = "emergency_reports",
                    documentId = java.util.UUID.randomUUID().toString(),
                    data = mapOf(
                        "type" to report.type,
                        "severity" to report.severity,
                        "reported_by" to report.reportedBy,
                        "location" to report.location,
                        "description" to report.description,
                        "reported_at" to report.reportedAt,
                        "status" to "ACTIVE"
                    )
                ).execute { response ->
                    if (response.isOk) {
                        cotc.resume(response.documentId)
                    } else {
                        cotc.resume("")
                    }
                }
            }.catch { emit("") }
        }

    override fun subscribeEmergencyUpdates(userId: String): Flow<List<EmergencyReport>> =
        flow {
            // Real-time subscription for emergency reports
            emit(listOf())
        }
}

/** Appwrite Announcement data source implementation */
class AppwriteAnnouncementDataSource @Inject constructor() : AppwriteDataSource {

    private val appwrite = Appwrite.getInstance()
    private val database = appwrite.services.databases()

    override fun getAnnouncements(): Flow<List<Announcement>> =
        flow {
            suspendCancellableCoroutine { cotc ->
                database.listDocuments(
                    databaseId = "ebarangay-sua-db",
                    collectionId = "announcements"
                ).execute { response ->
                    if (response.isOk) {
                        val docs = response.documents
                        val announcements = docs.map { doc ->
                            Announcement(
                                title = doc.data["title"]?.toString() ?: "",
                                content = doc.data["content"]?.toString() ?: "",
                                author = doc.data["author"]?.toString() ?: "",
                                priority = doc.data["priority"]?.toString() ?: "LOW",
                                expiresAt = doc.data["expires_at"]?.toString(),
                                isPinned = doc.data["is_pinned"]?.toBoolean() ?: false,
                                createdAt = doc.data["created_at"]?.toString() ?: ""
                            )
                        }
                        cotc.resume(announcements)
                    } else {
                        cotc.resume(emptyList())
                    }
                }
            }.catch { emit(emptyList()) }
        }

    override fun createAnnouncement(announcement: Announcement): Flow<String> =
        flow {
            suspendCancellableCoroutine { cotc ->
                database.createDocument(
                    databaseId = "ebarangay-sua-db",
                    collectionId = "announcements",
                    documentId = java.util.UUID.randomUUID().toString(),
                    data = mapOf(
                        "title" to announcement.title,
                        "content" to announcement.content,
                        "author" to announcement.author,
                        "priority" to announcement.priority,
                        "expires_at" to announcement.expiresAt,
                        "is_pinned" to announcement.isPinned,
                        "created_at" to announcement.createdAt
                    )
                ).execute { response ->
                    if (response.isOk) {
                        cotc.resume(response.documentId)
                    } else {
                        cotc.resume("")
                    }
                }
            }.catch { emit("") }
        }

    override fun deleteAnnouncement(id: String): Flow<Unit> =
        flow {
            suspendCancellableCoroutine { cotc ->
                database.deleteDocument(
                    databaseId = "ebarangay-sua-db",
                    collectionId = "announcements",
                    documentId = id
                ).execute { response ->
                    if (response.isOk) {
                        cotc.resume(Unit)
                    } else {
                        cotc.resume(Unit)
                    }
                }
            }.catch { emit(Unit) }
        }
}

/** Appwrite Barangay Official data source implementation */
class AppwriteBarangayOfficialDataSource @Inject constructor() : AppwriteDataSource {

    private val appwrite = Appwrite.getInstance()
    private val database = appwrite.services.databases()

    override fun getOfficials(): Flow<List<BarangayOfficial>> =
        flow {
            suspendCancellableCoroutine { cotc ->
                database.listDocuments(
                    databaseId = "ebarangay-sua-db",
                    collectionId = "officials"
                ).execute { response ->
                    if (response.isOk) {
                        val docs = response.documents
                        val officials = docs.map { doc ->
                            BarangayOfficial(
                                fullName = doc.data["full_name"]?.toString() ?: "",
                                position = doc.data["position"]?.toString() ?: "OTHER",
                                contactPhone = doc.data["contact_phone"]?.toString() ?: "",
                                contactEmail = doc.data["contact_email"]?.toString(),
                                isOnDuty = doc.data["is_on_duty"]?.toBoolean() ?: false,
                                dateAppointed = doc.data["date_appointed"]?.toString(),
                                profileImageId = doc.data["profile_image_id"]?.toString()
                            )
                        }
                        cotc.resume(officials)
                    } else {
                        cotc.resume(emptyList())
                    }
                }
            }.catch { emit(emptyList()) }
        }

    override fun getOnDutyOfficial(): Flow<BarangayOfficial?> =
        flow {
            suspendCancellableCoroutine { cotc ->
                database.listDocuments(
                    databaseId = "ebarangay-sua-db",
                    collectionId = "officials",
                    queries = ["is_on_duty" to true]
                ).execute { response ->
                    if (response.isOk && response.documents.isNotEmpty()) {
                        val doc = response.documents.first()
                        cotc.resume(BarangayOfficial(
                            fullName = doc.data["full_name"]?.toString() ?: "",
                            position = doc.data["position"]?.toString() ?: "OTHER",
                            contactPhone = doc.data["contact_phone"]?.toString() ?: "",
                            contactEmail = doc.data["contact_email"]?.toString(),
                            isOnDuty = true,
                            dateAppointed = doc.data["date_appointed"]?.toString(),
                            profileImageId = doc.data["profile_image_id"]?.toString()
                        ))
                    } else {
                        cotc.resume(null)
                    }
                }
            }.catch { emit(null) }
        }
}