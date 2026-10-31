package com.example.domain

import com.example.data.local.AppDatabase
import com.example.data.local.migrations.Migration_1_2
import com.example.data.local.repositories.DocumentRequestRepository
import com.example.data.local.repositories.EmergencyReportRepository
import com.example.data.local.repositories.NotificationRepository
import com.example.data.local.repositories.AuditLogRepository
import com.example.data.remote.AppwriteDataSource
import com.example.domain.model.Resident
import com.example.domain.model.ServiceRequest
import com.example.domain.model.EmergencyReport
import com.example.domain.model.Announcement
import com.example.domain.model.BarangayOfficial
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flowMutableStateFlow

/** 
 * BarangayRepository - Clean Architecture Repository Pattern
 * 
 * Encapsulates all data sources: Appwrite (authoritative backend) + Room (local cache).
 * Follows constitution principle II: clear separation between UI, domain, and data layers.
 * Repository pattern handles offline-first: Room as source of truth locally, Appwrite as
 * authoritative backend on reconnection, conflicts resolved by timestamp (latest wins).
 */
interface BarangayRepository {

    // =========================================================================
    // Resident Management
    // =========================================================================

    /** Get resident by ID - flows from Room cache, syncs with Appwrite on change */
    fun getResident(id: String): Flow<Resident?>

    /** Get current logged-in resident - from Appwrite auth state */
    val currentResident: Resident?

    /** Update resident status (ACTIVE, INACTIVE, PENDING_VERIFICATION) */
    suspend fun updateResidentStatus(id: String, status: String)

    // =========================================================================
    // Service Requests
    // =========================================================================

    /** Get all service requests for a resident */
    fun getServiceRequestsByResident(residentId: String): Flow<List<ServiceRequest>>

    /** Get pending service requests */
    fun getPendingServiceRequests(): Flow<List<ServiceRequest>>

    /** Create new service request - writes to both Appwrite and Room */
    suspend fun createServiceRequest(
        type: String,
        description: String,
        requiredDocuments: List<String>,
        resident: Resident
    ): ServiceRequest

    /** Update service request status */
    suspend fun updateServiceRequestStatus(
        requestId: String,
        status: String,
        officialNotes: String?
    )

    // =========================================================================
    // Emergency Reports
    // =========================================================================

    /** Get active emergency reports */
    fun getActiveEmergencyReports(): Flow<List<EmergencyReport>>

    /** Get emergency reports by resident */
    fun getEmergencyReportsByResident(residentId: String): Flow<List<EmergencyReport>>

    /** Create emergency report - writes to both Appwrite and Room with realtime */
    suspend fun reportEmergency(
        type: String,
        severity: String,
        location: String,
        description: String,
        resident: Resident
    ): EmergencyReport

    // =========================================================================
    // Announcements
    // =========================================================================

    /** Get all announcements */
    fun getAnnouncements(): Flow<List<Announcement>>

    /** Get pinned announcements first */
    fun getPinnedAnnouncements(): Flow<List<Announcement>>

    // =========================================================================
    // Barangay Official
    // =========================================================================

    /** Get on-duty official */
    fun getOnDutyOfficial(): Flow<BarangayOfficial?>

    // =========================================================================
    // Authentication State
    // =========================================================================

    /** Observe auth state changes */
    val authState: kotlinx.coroutines.flow.StateFlow<AuthState>

    /** Auth state enum */
    enum class AuthState {
        UNAUTHENTICATED,
        AUTHENTICATED,
        AUTHENTICATING,
        OFFLINE
    }

    /** Sign in with email and password */
    suspend fun signIn(email: String, password: String)

    /** Sign out / log out */
    suspend fun signOut()

    /** Refresh auth token */
    suspend fun refreshAuth()
}

/** Data class for resident model in domain layer */
data class Resident(
    val id: String,
    val fullName: String,
    val address: String,
    val birthDate: String,
    val age: Int,
    val profileImageId: String?,
    val status: String,
    val dateCreated: String,
    val lastLogin: String?
) : Serializable

/** Data class for auth state */
data class AuthStateData(
    val state: BarangayRepository.AuthState,
    val resident: BarangayRepository.Resident?,
    val timestamp: String = java.time Instant.now().toString()
) : Serializable