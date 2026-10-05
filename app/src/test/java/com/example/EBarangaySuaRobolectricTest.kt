package com.example

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.example.services.Appwrite
import com.example.services.CivicDataContract
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class EBarangaySuaRobolectricTest {

    @Test
    fun android16ContextBoots() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals(36, Build.VERSION.SDK_INT)
        assertEquals("com.aistudio.ebarangaysua.sjl", context.packageName)
    }

    @Test
    fun civicBackendContractIsStable() {
        assertEquals("documentRequests", CivicDataContract.DOCUMENT_REQUESTS)
        assertEquals("emergencyReports", CivicDataContract.EMERGENCY_REPORTS)
        assertEquals("facilities", Appwrite.FACILITIES_TABLE)
        assertEquals("requestStatusHistory", Appwrite.REQUEST_STATUS_HISTORY_TABLE)
        assertEquals("emergencyStatusHistory", Appwrite.EMERGENCY_STATUS_HISTORY_TABLE)
        assertTrue(CivicDataContract.STATUS_PENDING.isNotBlank())
    }

    @Test
    fun repositoryInitializesWithCivicDataAndHotlines() {
        val repo = com.example.data.BarangayRepository.instance
        assertTrue("Services should not be empty", repo.services.value.isNotEmpty())
        assertTrue("Officials directory should not be empty", repo.officials.value.isNotEmpty())
        assertTrue("Emergency hotlines should not be empty", repo.hotlines.isNotEmpty())
        assertTrue("Announcements should not be empty", repo.announcements.value.isNotEmpty())
        assertTrue("Initial requests should be available", repo.requests.value.isNotEmpty())
    }

    @Test
    fun roomDatabaseAndDaosWork() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = com.example.data.local.AppDatabase.getInstance(context)
        val dao = db.documentRequestDao()
        org.junit.Assert.assertNotNull(dao)
    }

    @Test
    fun auditLogTriggersOnRequestSubmissionAndStatusUpdate() {
        val repo = com.example.data.BarangayRepository.instance
        val service = repo.services.value.first()
        val initialAuditCount = repo.auditLogs.value.size

        // 1. Submit request -> verifies CREATE_REQUEST audit
        val req = repo.submitRequest(
            service = service,
            purpose = "Employment Application Test",
            deliveryMethod = "Pick-up at Barangay Hall",
            remarks = "Frontend Audit Verification",
            attachmentNames = listOf("cedula.jpg")
        )

        assertEquals("Audit log count must increase by 1", initialAuditCount + 1, repo.auditLogs.value.size)
        val createLog = repo.auditLogs.value.first()
        assertEquals("CREATE_REQUEST", createLog.action)
        assertEquals("DocumentRequest", createLog.targetType)
        assertEquals(req.referenceNumber, createLog.targetId)
        assertEquals("Submitted", createLog.newState)

        // 2. Update request status -> verifies UPDATE_REQUEST_STATUS audit
        repo.updateRequestStatus(
            requestId = req.id,
            newStatus = com.example.model.RequestStatus.READY,
            officialRemarks = "Signed by Punong Barangay"
        )

        val updateLog = repo.auditLogs.value.first()
        assertEquals("UPDATE_REQUEST_STATUS", updateLog.action)
        assertEquals("DocumentRequest", updateLog.targetType)
        assertEquals(req.referenceNumber, updateLog.targetId)
        assertEquals("Submitted", updateLog.previousState)
        assertEquals(com.example.model.RequestStatus.READY.label, updateLog.newState)
    }

    @Test
    fun auditLogTriggersOnEmergencySosAndResolution() {
        val repo = com.example.data.BarangayRepository.instance
        val initialAuditCount = repo.auditLogs.value.size

        // 1. Submit SOS report -> verifies EMERGENCY_SOS audit
        val report = repo.submitEmergency(
            type = com.example.model.EmergencyType.MEDICAL,
            description = "High fever in Purok 2",
            latitude = 10.3340,
            longitude = 124.9810,
            locationDescription = "Near Sea Wall"
        )

        assertEquals(initialAuditCount + 1, repo.auditLogs.value.size)
        val sosLog = repo.auditLogs.value.first()
        assertEquals("EMERGENCY_SOS", sosLog.action)
        assertEquals("EmergencyReport", sosLog.targetType)

        // 2. Update emergency status -> verifies UPDATE_EMERGENCY_STATUS audit
        repo.updateEmergencyStatus(
            reportId = report.id,
            newStatus = com.example.model.EmergencyStatus.RESOLVED,
            assignedResponder = "Barangay Health Worker Desk",
            notes = "Patient attended and stabilized"
        )

        val resolveLog = repo.auditLogs.value.first()
        assertEquals("UPDATE_EMERGENCY_STATUS", resolveLog.action)
        assertEquals("EmergencyReport", resolveLog.targetType)
        assertEquals(com.example.model.EmergencyStatus.RESOLVED.label, resolveLog.newState)
    }

    @Test
    fun auditLogTriggersOnPublishAnnouncementAndEvent() {
        val repo = com.example.data.BarangayRepository.instance

        // 1. Publish announcement
        repo.publishAnnouncement(
            title = "Test Coastal Storm Warning",
            description = "Gale warning issued for Cabalian Bay",
            category = com.example.model.AnnouncementCategory.DISASTER,
            priority = com.example.model.AnnouncementPriority.EMERGENCY,
            isPinned = true
        )
        val annLog = repo.auditLogs.value.first()
        assertEquals("PUBLISH_ANNOUNCEMENT", annLog.action)
        assertEquals("Announcement", annLog.targetType)
        assertEquals("Test Coastal Storm Warning", annLog.newState)

        // 2. Create Event
        repo.createEvent(
            title = "Barangay Assembly 2026",
            description = "Quarterly Assembly",
            date = "October 20, 2026",
            time = "9:00 AM",
            location = "Multipurpose Hall",
            organizer = "Council",
            category = "Governance"
        )
        val eventLog = repo.auditLogs.value.first()
        assertEquals("CREATE_EVENT", eventLog.action)
        assertEquals("BarangayEvent", eventLog.targetType)
        assertEquals("Barangay Assembly 2026", eventLog.newState)
    }

    @Test
    fun openMeteoServiceHasValidCoordinatesAndDefaults() {
        assertEquals(10.3340, com.example.services.OpenMeteoService.SUA_LATITUDE, 0.001)
        assertEquals(124.9810, com.example.services.OpenMeteoService.SUA_LONGITUDE, 0.001)
        val defaultTelemetry = com.example.services.CoastalTelemetry()
        assertTrue("Wave height must be positive", defaultTelemetry.waveHeightMeters > 0)
        assertTrue("Wind speed must be positive", defaultTelemetry.windSpeedKmH > 0)
        assertTrue("Rain probability between 0 and 100", defaultTelemetry.rainProbability in 0..100)
        assertTrue("Safety status must be defined", defaultTelemetry.safetyStatus.label.isNotBlank())
    }
}
