package com.example.services

import com.example.model.*
import io.appwrite.ID
import io.appwrite.Query
import java.time.Instant

object CivicSyncService {
    private val db get() = Appwrite.tablesDB()

    private suspend fun executeAuthority(path: String, payload: Map<String, Any?> = emptyMap()) {
        val response = Appwrite.functions().createExecution(
            functionId = Appwrite.BACKEND_AUTHORITY_FUNCTION_ID,
            body = org.json.JSONObject(payload).toString(),
            async = false,
            path = path,
            method = io.appwrite.enums.ExecutionMethod.POST
        )
        if (response.responseStatusCode !in 200..299) {
            error("Appwrite backend authority failed: HTTP ${response.responseStatusCode}: ${response.responseBody}")
        }
        val body = response.responseBody.orEmpty()
        if (body.isNotBlank() && !runCatching { org.json.JSONObject(body).optBoolean("ok", true) }.getOrDefault(true)) {
            error("Appwrite backend authority rejected operation: $body")
        }
    }

    suspend fun createRequest(request: DocumentRequest): Result<Unit> = runCatching {
        executeAuthority(
            path = "/request",
            payload = mapOf(
                "requestId" to request.id.take(36),
                "serviceId" to request.serviceId,
                "referenceNumber" to request.referenceNumber,
                "details" to (request.purpose + "|" + request.deliveryMethod + "|" + request.remarks),
                "status" to request.status.label,
                "submittedAt" to Instant.ofEpochMilli(request.createdAt).toString(),
                "updatedAt" to Instant.ofEpochMilli(request.updatedAt).toString()
            )
        )
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
        require(actorUid.isNotBlank()) { "Authenticated staff user required." }
        executeAuthority(
            path = "/request-status",
            payload = mapOf("requestId" to requestId, "status" to status, "remarks" to remarks)
        )
    }

    suspend fun createEmergency(report: EmergencyReport): Result<Unit> = runCatching {
        executeAuthority(
            path = "/emergency",
            payload = mapOf(
                "reportId" to report.id.take(36),
                "type" to report.type.displayName,
                "description" to report.description,
                "latitude" to report.latitude,
                "longitude" to report.longitude,
                "status" to "Reported",
                "createdAt" to Instant.ofEpochMilli(report.timestamp).toString()
            )
        )
    }

    suspend fun updateEmergencyStatus(
        reportId: String,
        status: String,
        responder: String,
        notes: String,
        actorUid: String
    ): Result<Unit> = runCatching {
        require(actorUid.isNotBlank()) { "Authenticated responder user required." }
        executeAuthority(
            path = "/emergency-status",
            payload = mapOf("reportId" to reportId, "status" to status, "responder" to responder, "notes" to notes)
        )
    }

    suspend fun createNotification(userId: String, notification: BarangayNotification): Result<Unit> = runCatching {
        require(userId.isNotBlank()) { "Authenticated resident required." }
        Unit
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
