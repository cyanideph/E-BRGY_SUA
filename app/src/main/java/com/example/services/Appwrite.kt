package com.example.services

import android.content.Context
import com.example.BuildConfig
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Realtime
import io.appwrite.services.Storage
import io.appwrite.services.TablesDB

/**
 * Single Appwrite client for the e-Barangay Sua Android application.
 *
 * No API key is stored in the mobile app. Resident/admin authorization is
 * enforced by Appwrite sessions, resource permissions, and server functions.
 *
 * Endpoint/project/database/bucket identifiers come from Gradle build
 * configuration so CI and local builds use the same configuration path.
 */
object Appwrite {
    val ENDPOINT: String get() = BuildConfig.APPWRITE_ENDPOINT.takeIf { it.isNotBlank() && it != "none" }.orEmpty()
    val PROJECT_ID: String get() = BuildConfig.APPWRITE_PROJECT_ID.takeIf { it.isNotBlank() && it != "none" }.orEmpty()
    val DATABASE_ID: String get() = BuildConfig.APPWRITE_DATABASE_ID.takeIf { it.isNotBlank() && it != "none" }.orEmpty()
    val RESIDENT_FILES_BUCKET_ID: String get() = BuildConfig.APPWRITE_RESIDENT_FILES_BUCKET_ID.takeIf { it.isNotBlank() && it != "none" }.orEmpty()

    const val USERS_TABLE = "users"
    const val RESIDENTS_TABLE = "residents"
    const val SERVICES_TABLE = "services"
    const val REQUESTS_TABLE = "documentRequests"
    const val ANNOUNCEMENTS_TABLE = "announcements"
    const val EVENTS_TABLE = "events"
    const val EMERGENCIES_TABLE = "emergencyReports"
    const val NOTIFICATIONS_TABLE = "notifications"
    const val AUDIT_LOGS_TABLE = "auditLogs"
    const val FACILITIES_TABLE = "facilities"
    const val OFFICIALS_TABLE = "officials"
    const val HOTLINES_TABLE = "hotlines"
    const val HOUSEHOLDS_TABLE = "households"
    const val REQUEST_STATUS_HISTORY_TABLE = "requestStatusHistory"
    const val EMERGENCY_STATUS_HISTORY_TABLE = "emergencyStatusHistory"

    private lateinit var client: Client

    fun init(context: Context) {
        if (::client.isInitialized) return
        val c = Client(context)
        runCatching {
            if (ENDPOINT.isNotBlank() && ENDPOINT.startsWith("http")) {
                c.setEndpoint(ENDPOINT)
            }
            if (PROJECT_ID.isNotBlank()) {
                c.setProject(PROJECT_ID)
            }
        }
        client = c
    }

    private fun requireClient(): Client {
        check(::client.isInitialized) { "Appwrite.init(context) must be called before using Appwrite." }
        return client
    }

    fun account(): Account = Account(requireClient())
    fun tablesDB(): TablesDB = TablesDB(requireClient())
    fun storage(): Storage = Storage(requireClient())
    fun realtime(): Realtime = Realtime(requireClient())

    /**
     * Verifies that the mobile client can reach this Appwrite project.
     * This uses the SDK's unauthenticated /ping endpoint and does not require
     * an API key or a signed-in user.
     */
    suspend fun ping(): String = requireClient().ping()
}
