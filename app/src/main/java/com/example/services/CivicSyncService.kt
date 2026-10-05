package com.example.services

import com.example.model.*
import io.appwrite.ID
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
        createAudit(request.residentUid, "CREATE_REQUEST", "DocumentRequest", request.id.take(36), request.referenceNumber).getOrThrow()
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
        createAudit(report.residentUid, "EMERGENCY_SOS", "EmergencyReport", report.id.take(36), report.type.displayName).getOrThrow()
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
