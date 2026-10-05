package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BarangayRepository
import com.example.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
  fun `repository session role switching is stable`() {
    val repository = BarangayRepository.instance
    assertNotNull(repository.currentUser.value)

    repository.switchRole(UserRole.ADMIN)
    assertEquals(UserRole.ADMIN, repository.currentUser.value.role)

    repository.switchRole(UserRole.RESIDENT)
    assertEquals(UserRole.RESIDENT, repository.currentUser.value.role)
  }
}
