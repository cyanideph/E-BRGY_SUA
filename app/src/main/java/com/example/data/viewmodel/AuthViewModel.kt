package com.example.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.data.AppThemeMode
import com.example.data.UserSession
import com.example.model.UserRole
import com.example.repository.BarangayRepository

/**
 * AuthViewModel handles user session, authentication, and theme mode.
 * Exposes simple StateFlows for UI consumption; delegates backend operations
 * to BarangayRepository for data persistence and Appwrite integration.
 */
class AuthViewModel(repository: BarangayRepository = BarangayRepository.instance) : ViewModel() {

    // Theme mode — observe from repository, UI can observe this directly
    val themeMode: androidx.lifecycle.StateFlow<AppThemeMode>
        = repository.themeMode

    // Current user session
    val currentUser: androidx.lifecycle.StateFlow<UserSession>
        = repository.currentUser

    // Online/offline state
    val isOnline: androidx.lifecycle.StateFlow<Boolean>
        = repository.isOnline

    // Onboarding completion
    val hasCompletedOnboarding: androidx.lifecycle.StateFlow<Boolean>
        = repository.hasCompletedOnboarding.asStateFlow()

    // Convenience: is resident (not staff/official/admin)
    val isResident: Boolean
        get() = repository.currentUser.value.role == UserRole.RESIDENT

    // Convenience: can access admin features (admin or official)
    val isAdminOrOfficial: Boolean
        get() = repository.currentUser.value.role == UserRole.ADMIN ||
            repository.currentUser.value.role == UserRole.OFFICIAL

    // Login with email/password
    suspend fun login(email: String, password: String): Result<UserSession> {
        return repository.login(email, password)
    }

    // Register new user
    suspend fun register(
        fullName: String, email: String, password: String, mobile: String, address: String,
        dateOfBirth: String, civilStatus: String, occupation: String,
        emergencyContactName: String, emergencyContactRelationship: String, emergencyContactPhone: String,
        latitude: Double?, longitude: Double?
    ): Result<UserSession> {
        return repository.register(
            fullName, email, password, mobile, address,
            dateOfBirth, civilStatus, occupation,
            emergencyContactName, emergencyContactRelationship, emergencyContactPhone,
            latitude, longitude
        )
    }

    // Logout — clears session and local cache
    suspend fun logout() {
        repository.logout()
    }

    // Refresh profile from backend
    suspend fun refreshProfile(): Result<UserSession> {
        return repository.refreshProfile()
    }

    // Toggle theme mode
    fun toggleTheme() {
        repository.toggleTheme()
    }

    // Set theme mode explicitly
    fun setThemeMode(mode: AppThemeMode) {
        repository.setThemeMode(mode)
    }
}