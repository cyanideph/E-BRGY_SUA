package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentRequestDao {
    @Query("SELECT * FROM document_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<DocumentRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: DocumentRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<DocumentRequestEntity>)

    @Query("SELECT * FROM document_requests WHERE id = :id OR referenceNumber = :id LIMIT 1")
    suspend fun getRequestById(id: String): DocumentRequestEntity?

    @Query("UPDATE document_requests SET status = :status, officialRemarks = :remarks, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, remarks: String, updatedAt: Long)

    @Query("DELETE FROM document_requests")
    suspend fun clear()
}

@Dao
interface EmergencyReportDao {
    @Query("SELECT * FROM emergency_reports ORDER BY timestamp DESC")
    fun getAllEmergencies(): Flow<List<EmergencyReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergency(report: EmergencyReportEntity)

    @Query("SELECT * FROM emergency_reports WHERE id = :id LIMIT 1")
    suspend fun getEmergencyById(id: String): EmergencyReportEntity?

    @Query("DELETE FROM emergency_reports")
    suspend fun clear()
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM barangay_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE barangay_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE barangay_notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM barangay_notifications")
    suspend fun clear()
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)

    @Query("DELETE FROM audit_logs")
    suspend fun clear()
}
