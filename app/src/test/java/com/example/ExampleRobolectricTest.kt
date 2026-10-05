package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BarangayRepository
import com.example.model.RequestStatus
import com.example.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("e-Barangay Sua", appName)
  }

  @Test
  fun `verify services and requests repository logic`() {
    val repository = BarangayRepository.instance
    val services = repository.services.value
    assertTrue("Should have barangay services", services.isNotEmpty())

    val clearanceService = services.first { it.id == "srv_clearance" }
    assertEquals("Barangay Clearance", clearanceService.name)

    // Test request submission
    val request = repository.submitRequest(
      service = clearanceService,
      purpose = "Employment Application",
      deliveryMethod = "Pick-up at Barangay Hall",
      remarks = "Test remarks",
      attachmentNames = listOf("id_proof.jpg")
    )
    assertNotNull(request.referenceNumber)
    assertTrue(request.referenceNumber.startsWith("BRG-SUA-2026-"))
    assertEquals(RequestStatus.SUBMITTED, request.status)

    // Test staff status update
    repository.updateRequestStatus(
      requestId = request.id,
      newStatus = RequestStatus.READY,
      officialRemarks = "Signed and ready for release"
    )

    val updatedRequest = repository.requests.value.first { it.id == request.id }
    assertEquals(RequestStatus.READY, updatedRequest.status)

    // Test role switching
    repository.switchRole(UserRole.ADMIN)
    assertEquals(UserRole.ADMIN, repository.currentUser.value.role)
  }
}
