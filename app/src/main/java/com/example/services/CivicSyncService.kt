package com.example.services

import com.example.model.*
import io.appwrite.ID
import io.appwrite.Query
import java.time.Instant

object CivicSyncService {
    private val db get() = Appwrite.tablesDB()

    suspend fun createRequest(request: DocumentRequest): Result<Unit> = runCatching {
        db.createRow(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.REQUESTS_TABLE,
            rowId = request.id.take(36),
            data = mapOf(
                "userId" to request.residentUid,
                "serviceId" to request.serviceId,
                "referenceNumber" to request.referenceNumber,
                "status" to request.status.label,
                "details" to request.purpose + "|" + request.deliveryMethod + "|" + request.remarks,
                "submittedAt" to Instant.ofEpochMilli(request.createdAt).toString(),
                "updatedAt" to Instant.ofEpochMilli(request.updatedAt).toString()
            )
        )
        db.createRow(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.REQUEST_STATUS_HISTORY_TABLE,
            rowId = ID.unique(),
            data = mapOf(
                "requestId" to request.id.take(36),
                "status" to request.status.label,
                "remarks" to request.officialRemarks,
                "changedBy" to request.residentUid,
                "changedAt" to Instant.ofEpochMilli(request.createdAt).toString()
            )
        )
        createNotification(
            request.residentUid,
            BarangayNotification(
                id = ID.unique(),
                title = "Request Submitted",
                message = "Request ${request.referenceNumber} was received by the barangay.",
                timestamp = request.createdAt,
                category = "Service Request",
                priority = "Normal",
                referenceId = request.referenceNumber
            )
        ).getOrThrow()
        createAudit(request.residentUid, "CREATE_REQUEST", "DocumentRequest", request.id.take(36), request.referenceNumber).getOrThrow()
    }


    /** Fetch only the authenticated resident's requests. Appwrite permissions remain
     * the primary security boundary; the query also prevents loading unrelated rows. */
    suspend fun listRequestsForUser(userId: String): Result<List<Map<String, Any>>> = runCatching {
        db.listRows(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.REQUESTS_TABLE,
            queries = listOf(Query.equal("userId", userId), Query.orderDesc("submittedAt"), Query.limit(100))
        ).rows.map { row ->
            buildMap {
                put("id", row.id)
                putAll(row.data)
                put("createdAt", row.createdAt)
                put("updatedAt", row.updatedAt)
            }
        }
    }

    /** Server-side lifecycle update. The caller should also write the matching
     * status-history row so the audit timeline is durable. */
    suspend fun updateRequestStatus(
        requestId: String,
        status: String,
        remarks: String,
        actorUid: String
    ): Result<Unit> = runCatching {
        db.updateRow(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.REQUESTS_TABLE,
            rowId = requestId,
            data = mapOf(
                "status" to status,
                "updatedAt" to Instant.now().toString()
            )
        )
        db.createRow(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.REQUEST_STATUS_HISTORY_TABLE,
            rowId = ID.unique(),
            data = mapOf(
                "requestId" to requestId,
                "status" to status,
                "remarks" to remarks,
                "changedBy" to actorUid,
                "changedAt" to Instant.now().toString()
            )
        )
        val request = db.getRow(databaseId = Appwrite.DATABASE_ID, tableId = Appwrite.REQUESTS_TABLE, rowId = requestId)
        val residentUid = request.data["userId"]?.toString().orEmpty()
        if (residentUid.isNotBlank()) {
            createNotification(
                residentUid,
                BarangayNotification(
                    id = ID.unique(),
                    title = "Request Status Updated",
                    message = "Your request status is now ${status}." + if (remarks.isBlank()) "" else " ${remarks}",
                    timestamp = System.currentTimeMillis(),
                    category = "Service Request",
                    priority = if (status == RequestStatus.READY.label) "Important" else "Normal",
                    referenceId = request.data["referenceNumber"]?.toString().orEmpty()
                )
            ).getOrThrow()
        }
        createAudit(actorUid, "UPDATE_REQUEST_STATUS", "DocumentRequest", requestId, status).getOrThrow()
    }

    suspend fun createEmergency(report: EmergencyReport): Result<Unit> = runCatching {
        db.createRow(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.EMERGENCIES_TABLE,
            rowId = report.id.take(36),
            data = mapOf(
                "userId" to report.residentUid,
                "type" to when (report.type) {
                    EmergencyType.BARANGAY_EMERGENCY -> "Barangay Emergency"
                    EmergencyType.MEDICAL -> "Medical"
                    EmergencyType.FIRE -> "Fire"
                    EmergencyType.POLICE -> "Police"
                    EmergencyType.RESCUE_DISASTER -> "Rescue/Disaster"
                },
                "description" to report.description,
                "latitude" to report.latitude,
                "longitude" to report.longitude,
                "status" to "Reported",
                "createdAt" to Instant.ofEpochMilli(report.timestamp).toString()
            )
        )
        db.createRow(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.EMERGENCY_STATUS_HISTORY_TABLE,
            rowId = ID.unique(),
            data = mapOf(
                "reportId" to report.id.take(36),
                "status" to "Reported",
                "responder" to report.assignedResponder,
                "notes" to report.responseNotes,
                "changedBy" to report.residentUid,
                "changedAt" to Instant.ofEpochMilli(report.timestamp).toString()
            )
        )
        createNotification(
            report.residentUid,
            BarangayNotification(
                id = ID.unique(),
                title = "Emergency Report Accepted",
                message = "Emergency report ${report.id} was accepted by the barangay backend.",
                timestamp = report.timestamp,
                priority = "Emergency",
                category = "Emergency",
                referenceId = report.id
            )
        ).getOrThrow()
        createAudit(report.residentUid, "EMERGENCY_SOS", "EmergencyReport", report.id.take(36), report.type.displayName).getOrThrow()
    }

    suspend fun updateEmergencyStatus(
        reportId: String,
        status: String,
        responder: String,
        notes: String,
        actorUid: String
    ): Result<Unit> = runCatching {
        db.updateRow(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.EMERGENCIES_TABLE,
            rowId = reportId,
            data = mapOf("status" to status)
        )
        db.createRow(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.EMERGENCY_STATUS_HISTORY_TABLE,
            rowId = ID.unique(),
            data = mapOf(
                "reportId" to reportId,
                "status" to status,
                "responder" to responder,
                "notes" to notes,
                "changedBy" to actorUid,
                "changedAt" to Instant.now().toString()
            )
        )
        val report = db.getRow(databaseId = Appwrite.DATABASE_ID, tableId = Appwrite.EMERGENCIES_TABLE, rowId = reportId)
        val residentUid = report.data["userId"]?.toString().orEmpty()
        if (residentUid.isNotBlank()) {
            createNotification(
                residentUid,
                BarangayNotification(
                    id = ID.unique(),
                    title = "Emergency Status Updated",
                    message = "Your emergency report status is now $status.",
                    timestamp = System.currentTimeMillis(),
                    category = "Emergency",
                    priority = "Emergency",
                    referenceId = reportId
                )
            ).getOrThrow()
        }
        createAudit(actorUid, "UPDATE_EMERGENCY_STATUS", "EmergencyReport", reportId, status).getOrThrow()
    }

    suspend fun createNotification(userId: String, notification: BarangayNotification): Result<Unit> = runCatching {
        db.createRow(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.NOTIFICATIONS_TABLE,
            rowId = notification.id.take(36),
            data = mapOf(
                "userId" to userId,
                "title" to notification.title,
                "body" to notification.message,
                "type" to notification.category,
                "read" to notification.isRead,
                "createdAt" to Instant.ofEpochMilli(notification.timestamp).toString()
            )
        )
    }


    suspend fun createAnnouncement(
        title: String,
        description: String,
        category: AnnouncementCategory,
        priority: AnnouncementPriority,
        isPinned: Boolean,
        authorName: String,
        authorRole: String
    ): Result<Unit> = runCatching {
        val now = Instant.now().toString()
        db.createRow(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.ANNOUNCEMENTS_TABLE,
            rowId = ID.unique(),
            data = mapOf(
                "title" to title,
                "body" to description,
                "published" to true,
                "publishedAt" to now,
                "createdAt" to now,
                "category" to category.name,
                "priority" to priority.name,
                "authorName" to authorName,
                "authorRole" to authorRole,
                "isPinned" to isPinned
            )
        )
    }

    suspend fun createEvent(
        title: String,
        description: String,
        startsAt: String,
        endsAt: String,
        location: String,
        organizer: String,
        category: String
    ): Result<Unit> = runCatching {
        db.createRow(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.EVENTS_TABLE,
            rowId = ID.unique(),
            data = mapOf(
                "title" to title,
                "description" to description,
                "startsAt" to startsAt,
                "endsAt" to endsAt,
                "location" to location,
                "createdAt" to Instant.now().toString(),
                "organizer" to organizer,
                "category" to category,
                "rsvpCount" to 0
            )
        )
    }

    suspend fun markNotificationRead(userId: String, notificationId: String): Result<Unit> = runCatching {
        val row = db.getRow(databaseId = Appwrite.DATABASE_ID, tableId = Appwrite.NOTIFICATIONS_TABLE, rowId = notificationId)
        require(row.data["userId"]?.toString() == userId) { "Notification does not belong to the authenticated user." }
        db.updateRow(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.NOTIFICATIONS_TABLE,
            rowId = notificationId,
            data = mapOf("read" to true)
        )
    }

    suspend fun listNotificationsForUser(userId: String): Result<List<BarangayNotification>> = runCatching {
        db.listRows(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.NOTIFICATIONS_TABLE,
            queries = listOf(
                io.appwrite.Query.equal("userId", userId),
                io.appwrite.Query.orderDesc("createdAt"),
                io.appwrite.Query.limit(100)
            )
        ).rows.map { row ->
            val d = row.data
            BarangayNotification(
                id = row.id,
                title = d["title"]?.toString().orEmpty(),
                message = d["body"]?.toString().orEmpty(),
                timestamp = runCatching { Instant.parse(d["createdAt"]?.toString().orEmpty()).toEpochMilli() }.getOrDefault(System.currentTimeMillis()),
                isRead = d["read"]?.toString()?.toBooleanStrictOrNull() ?: false,
                category = d["type"]?.toString().orEmpty(),
                priority = d["priority"]?.toString().orEmpty(),
                referenceId = d["referenceId"]?.toString().orEmpty()
            )
        }
    }

    suspend fun createAudit(
        actorUid: String,
        action: String,
        resourceType: String,
        resourceId: String?,
        details: String
    ): Result<Unit> = runCatching {
        db.createRow(
            databaseId = Appwrite.DATABASE_ID,
            tableId = Appwrite.AUDIT_LOGS_TABLE,
            rowId = ID.unique(),
            data = mapOf(
                "actorUserId" to actorUid,
                "action" to action,
                "resourceType" to resourceType,
                "resourceId" to resourceId,
                "details" to details,
                "createdAt" to Instant.now().toString()
            )
        )
    }
}
