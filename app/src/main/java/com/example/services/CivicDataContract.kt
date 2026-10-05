package com.example.services

/**
 * Single contract for resident-facing civic data.
 * UI repositories should map Appwrite records into these stable concepts.
 */
object CivicDataContract {
    const val SERVICES = "services"
    const val DOCUMENT_REQUESTS = "documentRequests"
    const val EMERGENCY_REPORTS = "emergencyReports"
    const val NOTIFICATIONS = "notifications"
    const val AUDIT_LOGS = "auditLogs"
    const val HOTLINES = "hotlines"
    const val FACILITIES = "facilities"
    const val REQUEST_STATUS_HISTORY = "requestStatusHistory"

    const val STATUS_PENDING = "pending"
    const val STATUS_RECEIVED = "received"
    const val STATUS_PROCESSING = "processing"
    const val STATUS_READY = "ready"
    const val STATUS_COMPLETED = "completed"
    const val STATUS_REJECTED = "rejected"
    const val STATUS_CANCELLED = "cancelled"
}
