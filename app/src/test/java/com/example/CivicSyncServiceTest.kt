package com.example

import com.example.model.*
import com.example.services.CivicSyncService
import com.example.services.Appwrite
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.robolectric.annotation.Config

@Config(sdk = [36])
class CivicSyncServiceTest {

    @Before
    fun setup() {
        Appwrite.init(mockContext())
    }

    @Test
    fun testRegisterResidentProfile_validData() {
        val result = CivicSyncService.registerResidentProfile(
            fullName = "Test User",
            email = "test@example.com",
            mobileNumber = "1234567890",
            address = "Test Address",
            dateOfBirth = "1990-01-01",
            civilStatus = "Single",
            occupation = "Test Occupation",
            emergencyContactName = "Contact",
            emergencyContactRelationship = "Relative",
            emergencyContactPhone = "0987654321",
            latitude = 1.0,
            longitude = 2.0
        )
        assertTrue(result.isSuccess)
    }

    @Test
    fun testCreateRequest_validRequest() {
        val request = DocumentRequest(
            id = "req-1",
            serviceId = "clearance",
            referenceNumber = "REF-1",
            purpose = DocumentRequest.Purpose.Clearance,
            deliveryMethod = DocumentRequest.DeliveryMethod.Pickup,
            remarks = "Test remarks",
            createdAt = System.currentTimeMillis(),
            status = DocumentRequest.Status.Submitted
        )
        val result = CivicSyncService.createRequest(request)
        assertTrue(result.isSuccess)
    }

    @Test
    fun testUpdateRequestStatus_staffUser() {
        val result = CivicSyncService.updateRequestStatus(
            requestId = "req-1",
            status = "Processing",
            remarks = "Checked by staff",
            actorUid = "staff-1"
        )
        assertTrue(result.isSuccess)
    }

    @Test
    fun testCreateEmergency_validReport() {
        val report = EmergencyReport(
            id = "rep-1",
            type = EmergencyReport.Type.Fire,
            description = "House fire",
            latitude = 1.0,
            longitude = 2.0,
            timestamp = System.currentTimeMillis()
        )
        val result = CivicSyncService.createEmergency(report)
        assertTrue(result.isSuccess)
    }

    @Test
    fun testUpdateEmergencyStatus_staffUser() {
        val result = CivicSyncService.updateEmergencyStatus(
            reportId = "rep-1",
            status = "Responding",
            responder = "Staff Name",
            notes = "En route to location",
            actorUid = "staff-1"
        )
        assertTrue(result.isSuccess)
    }

    @Test
    fun testCreateAnnouncement_officialUser() {
        val result = CivicSyncService.createAnnouncement(
            title = "Test Announcement",
            description = "Test description",
            category = AnnouncementCategory.General,
            priority = AnnouncementPriority.Normal,
            isPinned = false,
            authorName = "Official Name",
            authorRole = "official"
        )
        assertTrue(result.isSuccess)
    }

    @Test
    fun testCreateEvent_officialUser() {
        val result = CivicSyncService.createEvent(
            title = "Test Event",
            description = "Test description",
            startsAt = "2024-01-01T10:00:00",
            endsAt = "2024-01-01T12:00:00",
            location = "Test Location",
            organizer = "Test Organizer",
            category = "Community"
        )
        assertTrue(result.isSuccess)
    }

    @Test
    fun testListRequestsForUser() {
        val result = CivicSyncService.listRequestsForUser("user-1")
        assertTrue(result.isSuccess)
    }

    @Test
    fun testListRequestHistoryForUser() {
        val result = CivicSyncService.listRequestHistoryForUser("user-1")
        assertTrue(result.isSuccess)
    }

    @Test
    fun testListEmergencyHistoryForUser() {
        val result = CivicSyncService.listEmergencyHistoryForUser("user-1")
        assertTrue(result.isSuccess)
    }

    @Test
    fun testMarkNotificationRead() {
        val result = CivicSyncService.markNotificationRead("user-1", "notif-1")
        assertTrue(result.isSuccess)
    }
}