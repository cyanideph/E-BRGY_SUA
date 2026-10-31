package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.local.migrations.Migration_1_2
import java.time.Instant
import kotlinx.datetime.LocalDate

@Entity(tableName = "residents")
data class ResidentEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "birth_date") val birthDate: String,
    @ColumnInfo(name = "age") val age: Int,
    @ColumnInfo(name = "profile_image_id") val profileImageId: String?,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "date_created") val dateCreated: String,
    @ColumnInfo(name = "last_login") val lastLogin: String?
) : AutoValue_ResidentEntity {
    // Room will generate an AutoValue subclass; this is the abstract builder
    abstract override fun getId(): String
    abstract override fun getFullName(): String
    abstract override fun getAddress(): String
    abstract override fun getBirthDate(): String
    abstract override fun getAge(): Int
    abstract override fun getProfileImageId(): String?
    abstract override fun getStatus(): String
    abstract override fun getDateCreated(): String
    abstract override fun getLastLogin(): String?
}

/** Room entity for ServiceRequest */
@Entity(tableName = "service_requests")
data class ServiceRequestEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "requested_by") val requestedBy: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "required_documents") val requiredDocuments: String, // JSON stored as String
    @ColumnInfo(name = "request_date") val requestDate: String,
    @ColumnInfo(name = "target_date") val targetDate: String?,
    @ColumnInfo(name = "completed_date") val completedDate: String?,
    @ColumnInfo(name = "assigned_official") val assignedOfficial: String?,
    @ColumnInfo(name = "resolution_notes") val resolutionNotes: String?
) : AutoValue_ServiceRequestEntity {
    abstract override fun getId(): String
    abstract override fun getType(): String
    abstract override fun getStatus(): String
    abstract override fun getRequestedBy(): String
    abstract override fun getDescription(): String
    abstract override fun getRequiredDocuments(): String
    abstract override fun getRequestDate(): String
    abstract override fun getTargetDate(): String?
    abstract override fun getCompletedDate(): String?
    abstract override fun getAssignedOfficial(): String?
    abstract override fun getResolutionNotes(): String?
}

/** Room entity for EmergencyReport */
@Entity(tableName = "emergency_reports")
data class EmergencyReportEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "severity") val severity: String,
    @ColumnInfo(name = "reported_by") val reportedBy: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "reported_at") val reportedAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String?,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "resolved_at") val resolvedAt: String?,
    @ColumnInfo(name = "resolved_by") val resolvedBy: String?,
    @ColumnInfo(name = "photos") val photos: String // JSON list of file IDs
) : AutoValue_EmergencyReportEntity {
    abstract override fun getId(): String
    abstract override fun getType(): String
    abstract override fun getSeverity(): String
    abstract override fun getReportedBy(): String
    abstract override fun getLocation(): String
    abstract override fun getDescription(): String
    abstract override fun getReportedAt(): String
    abstract override fun getUpdatedAt(): String?
    abstract override fun getStatus(): String
    abstract override fun getResolvedAt(): String?
    abstract override fun getResolvedBy(): String?
    abstract override fun getPhotos(): String
}

/** Room entity for Announcement */
@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "priority") val priority: String,
    @ColumnInfo(name = "expires_at") val expiresAt: String?,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "is_pinned") val isPinned: Boolean
) : AutoValue_AnnouncementEntity {
    abstract override fun getId(): String
    abstract override fun getTitle(): String
    abstract override fun getContent(): String
    abstract override fun getAuthor(): String
    abstract override fun getPriority(): String
    abstract override fun getExpiresAt(): String?
    abstract override fun getCreatedAt(): String
    abstract override fun getIsPinned(): Boolean
}

/** Room entity for BarangayOfficial */
@Entity(tableName = "officials")
data class BarangayOfficialEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "position") val position: String,
    @ColumnInfo(name = "contact_phone") val contactPhone: String,
    @ColumnInfo(name = "contact_email") val contactEmail: String?,
    @ColumnInfo(name = "is_on_duty") val isOnDuty: Boolean,
    @ColumnInfo(name = "date_appointed") val dateAppointed: String?,
    @ColumnInfo(name = "profile_image_id") val profileImageId: String?
) : AutoValue_BarangayOfficialEntity {
    abstract override fun getId(): String
    abstract override fun getFullName(): String
    abstract override fun getPosition(): String
    abstract override fun getContactPhone(): String
    abstract override fun getContactEmail(): String?
    abstract override fun getIsOnDuty(): Boolean
    abstract override fun getDateAppointed(): String?
    abstract override fun getProfileImageId(): String?
}