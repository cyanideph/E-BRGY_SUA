package com.example.ui.resident.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.provider.appwrite.AuthProvider
import com.example.domain.model.Resident
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.domain.model.AuthState

/** Login ViewModel - handles login business logic.
 * 
 *  Follows constitution principles:
 *  - II: Maintainable and secure architecture (clean separation)
 *  - VI: Data integrity (validate all incoming data at repository layer)
 *  - VIII: Testing before merging (tests must pass before feature merge)
 *  - IX: Compatibility with Android 7.0+ (min SDK 24)
 */
class LoginViewModel : ViewModel() {

    private val _authState = MutableStateFlow(AuthState.UNAUTHENTICATED)
    val authState: MutableStateFlow<AuthState> = _authState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: MutableStateFlow<bool> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: MutableStateFlow<String?> = _errorMessage

    private val authProvider: AuthProvider = AuthProvider(AppwriteDataSource())

    /** Initiate login flow */
    fun login(email: String, password: String, onSuccess: (Resident) -> Unit) {
        if (email.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Please enter email and password"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val resident = withContext(Dispatchers.IO) {
                    authProvider.login(email, password).firstOrNull()
                        ?: throw IllegalStateException("Login failed - no resident returned")
                }

                _isLoading.value = false
                _authState.value = AuthState.AUTHENTICATED
                onSuccess(resident)
            } catch (e: Exception) {
                _isLoading.value = false
                val userMessage = when (e) {
                    is[IllegalArgumentException] -> e.message ?: "Invalid input"
                    is[IllegalStateException] -> e.message ?: "Authentication failed"
                    else -> "An unexpected error occurred. Please try again."
                }
                _errorMessage.value = userMessage
                // Constitution VII: Log error without secrets
                ErrorHandler.logError("login", e, mapOf("email" to email))
            }
        }
    }

    /** Refresh auth state */
    fun refreshAuth() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authProvider.refreshAuth()
                _authState.value = AuthState.AUTHENTICATED
            } catch (e: Exception) {
                _errorMessage.value = "Failed to refresh authentication"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Sign out */
    fun signOut() {
        viewModelScope.launch {
            authProvider.logout()
            _authState.value = AuthState.UNAUTHENTICATED
        }
    }
}