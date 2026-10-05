package com.example.services

import android.content.Context
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
 */
object Appwrite {
    const val ENDPOINT = "https://sgp.cloud.appwrite.io/v1"
    const val PROJECT_ID = "6ac31e4000390af0f850"
    const val DATABASE_ID = "ebarangay-sua-db"
    const val RESIDENT_FILES_BUCKET_ID = "resident-files"

    const val USERS_TABLE = "users"
    const val RESIDENTS_TABLE = "residents"
    const val SERVICES_TABLE = "services"
    const val REQUESTS_TABLE = "documentRequests"
    const val ANNOUNCEMENTS_TABLE = "announcements"
    const val EVENTS_TABLE = "events"
    const val EMERGENCIES_TABLE = "emergencyReports"
    const val NOTIFICATIONS_TABLE = "notifications"
    const val AUDIT_LOGS_TABLE = "auditLogs"

    private lateinit var client: Client

    fun init(context: Context) {
        if (::client.isInitialized) return
        client = Client(context)
            .setEndpoint(ENDPOINT)
            .setProject(PROJECT_ID)
    }

    private fun requireClient(): Client {
        check(::client.isInitialized) { "Appwrite.init(context) must be called before using Appwrite." }
        return client
    }

    fun account(): Account = Account(requireClient())
    fun tablesDB(): TablesDB = TablesDB(requireClient())
    fun storage(): Storage = Storage(requireClient())
    fun realtime(): Realtime = Realtime(requireClient())
}
