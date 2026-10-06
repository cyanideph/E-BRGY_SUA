package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BarangayRepository
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
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
  fun `repository starts with a non-null session state`() {
    val repository = BarangayRepository.instance
    assertNotNull(repository.currentUser.value)
    assertNotNull(repository.currentUser.value.role)
  }
}
