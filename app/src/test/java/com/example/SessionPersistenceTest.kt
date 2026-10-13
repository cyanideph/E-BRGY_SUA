package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BarangayRepository
import com.example.model.ResidentProfile
import com.example.model.UserRole
import com.example.model.UserSession
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SessionPersistenceTest {

    @Test
    fun testOnboardingPersistence() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = BarangayRepository.instance
        repository.initLocalDb(context)

        repository.setCompletedOnboarding(true)
        assertTrue(repository.hasCompletedOnboarding())

        repository.setCompletedOnboarding(false)
        assertFalse(repository.hasCompletedOnboarding())
    }

    @Test
    fun testSessionRestorationAcrossSimulatedRestart() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("barangay_sua_prefs", Context.MODE_PRIVATE)

        // Simulate a saved user session from previous login
        prefs.edit()
            .putString("session_uid", "user_12345")
            .putString("session_email", "resident@sua.ph")
            .putString("session_role", "RESIDENT")
            .putString("profile_full_name", "Juan Dela Cruz")
            .putBoolean("has_completed_onboarding", true)
            .apply()

        val repository = BarangayRepository.instance
        repository.initLocalDb(context)

        // Session must be restored immediately into currentUser without waiting for network
        val currentUser = repository.currentUser.value
        assertEquals("user_12345", currentUser.uid)
        assertEquals("resident@sua.ph", currentUser.email)
        assertEquals(UserRole.RESIDENT, currentUser.role)
        assertEquals("Juan Dela Cruz", currentUser.profile.fullName)
        assertTrue(repository.hasCompletedOnboarding())
    }
}
