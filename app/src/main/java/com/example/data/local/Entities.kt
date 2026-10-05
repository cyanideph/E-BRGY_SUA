package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.*

@Entity(tableName = "document_requests")
data class DocumentRequestEntity(
    @PrimaryKey val id: String,
    val referenceNumber: String,
    val serviceId: String,
    val serviceName: String,
    val residentUid: String,
    val residentName: String,
    val residentAddress: String,
    val residentContact: String,
    val purpose: String,
    val remarks: String,
    val deliveryMethod: String,
    val status: RequestStatus,
    val officialRemarks: String,
    val attachmentNames: List<String>,
    val createdAt: Long,
    val updatedAt: Long,
    val timeline: List<RequestTimelineEvent>,
    val isSyncedToServer: Boolean
) {
    fun toDomain(): DocumentRequest = DocumentRequest(
        id = id,
        referenceNumber = referenceNumber,
        serviceId = serviceId,
        serviceName = serviceName,
        residentUid = residentUid,
        residentName = residentName,
        residentAddress = residentAddress,
        residentContact = residentContact,
        purpose = purpose,
        remarks = remarks,
        deliveryMethod = deliveryMethod,
        status = status,
        officialRemarks = officialRemarks,
        attachmentNames = attachmentNames,
        createdAt = createdAt,
        updatedAt = updatedAt,
        timeline = timeline,
        isSyncedToServer = isSyncedToServer
    )

    companion object {
        fun fromDomain(d: DocumentRequest): DocumentRequestEntity = DocumentRequestEntity(
            id = d.id,
            referenceNumber = d.referenceNumber,
            serviceId = d.serviceId,
            serviceName = d.serviceName,
            residentUid = d.residentUid,
            residentName = d.residentName,
            residentAddress = d.residentAddress,
            residentContact = d.residentContact,
            purpose = d.purpose,
            remarks = d.remarks,
            deliveryMethod = d.deliveryMethod,
            status = d.status,
            officialRemarks = d.officialRemarks,
            attachmentNames = d.attachmentNames,
            createdAt = d.createdAt,
            updatedAt = d.updatedAt,
            timeline = d.timeline,
            isSyncedToServer = d.isSyncedToServer
        )
    }
}

@Entity(tableName = "emergency_reports")
data class EmergencyReportEntity(
    @PrimaryKey val id: String,
    val type: EmergencyType,
    val description: String,
    val residentName: String,
    val residentContact: String,
    val residentUid: String,
    val latitude: Double?,
    val longitude: Double?,
    val locationDescription: String,
    val timestamp: Long,
    val status: EmergencyStatus,
    val assignedResponder: String,
    val responseNotes: String
) {
    fun toDomain(): EmergencyReport = EmergencyReport(
        id = id,
        type = type,
        description = description,
        residentName = residentName,
        residentContact = residentContact,
        residentUid = residentUid,
        latitude = latitude,
        longitude = longitude,
        locationDescription = locationDescription,
        timestamp = timestamp,
        status = status,
        assignedResponder = assignedResponder,
        responseNotes = responseNotes
    )

    companion object {
        fun fromDomain(e: EmergencyReport): EmergencyReportEntity = EmergencyReportEntity(
            id = e.id,
            type = e.type,
            description = e.description,
            residentName = e.residentName,
            residentContact = e.residentContact,
            residentUid = e.residentUid,
            latitude = e.latitude,
            longitude = e.longitude,
            locationDescription = e.locationDescription,
            timestamp = e.timestamp,
            status = e.status,
            assignedResponder = e.assignedResponder,
            responseNotes = e.responseNotes
        )
    }
}

@Entity(tableName = "barangay_notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean,
    val category: String,
    val priority: String,
    val referenceId: String?
) {
    fun toDomain(): BarangayNotification = BarangayNotification(
        id = id,
        title = title,
        message = message,
        timestamp = timestamp,
        isRead = isRead,
        category = category,
        priority = priority,
        referenceId = referenceId
    )

    companion object {
        fun fromDomain(n: BarangayNotification): NotificationEntity = NotificationEntity(
            id = n.id,
            title = n.title,
            message = n.message,
            timestamp = n.timestamp,
            isRead = n.isRead,
            category = n.category,
            priority = n.priority,
            referenceId = n.referenceId
        )
    }
}

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val actorUid: String,
    val actorName: String,
    val actorRole: String,
    val action: String,
    val targetType: String,
    val targetId: String,
    val previousState: String?,
    val newState: String?,
    val timestamp: Long
) {
    fun toDomain(): AuditLog = AuditLog(
        id = id,
        actorUid = actorUid,
        actorName = actorName,
        actorRole = actorRole,
        action = action,
        targetType = targetType,
        targetId = targetId,
        previousState = previousState,
        newState = newState,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(a: AuditLog): AuditLogEntity = AuditLogEntity(
            id = a.id,
            actorUid = a.actorUid,
            actorName = a.actorName,
            actorRole = a.actorRole,
            action = a.action,
            targetType = a.targetType,
            targetId = a.targetId,
            previousState = a.previousState,
            newState = a.newState,
            timestamp = a.timestamp
        )
    }
}
