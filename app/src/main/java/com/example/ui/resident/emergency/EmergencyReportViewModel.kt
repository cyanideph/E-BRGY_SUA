package com.example.ui.resident.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.provider.appwrite.AuthProvider
import com.example.domain.model.EmergencyReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.data.error.ErrorHandler

/** Emergency Report ViewModel - handles emergency report submission business logic.
 * 
 *  Follows constitution principles:
 *  - II: Maintainable and secure architecture (clean separation)
 *  - VI: Data integrity (all validation at repository layer before writes)
 *  - VII: Environment-based secrets, no hardcoded credentials, HTTPS enforcement
 *  - VIII: Testing before merging (tests must pass before feature merge)
 *  - IX: Compatibility with Android 7.0+ (min SDK 24)
 */
class EmergencyReportViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: MutableStateFlow<bool> = _isLoading

    private val _success = MutableStateFlow<Boolean>(false)
    val success: MutableStateFlow<bool> = _success

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: MutableStateFlow<String?> = _errorMessage

    private val emergencyProvider: AuthProvider = AuthProvider(AppwriteDataSource())

    /** Report emergency */
    fun reportEmergency(
        emergencyType: String,
        severity: String,
        location: String,
        description: String,
        onSuccess: ((EmergencyReport) -> Unit)?
    ) {
        _isLoading.value = true
        _errorMessage.value = null
        _success.value = false

        viewModelScope.launch {
            try {
                val report = EmergencyReport(
                    type = emergencyType,
                    severity = severity,
                    reportedBy = "", // Will be set from authenticated resident
                    location = location,
                    description = description,
                    reportedAt = java.time.Instant.now().toString(),
                    status = "ACTIVE"
                )

                withContext(Dispatchers.IO) {
                    emergencyProvider.reportEmergency(report).firstOrNull()
                        ?: throw IllegalStateException("Emergency report submission failed")
                }

                _isLoading.value = false
                _success.value = true
                _errorMessage.value = "Emergency report submitted successfully"
                onSuccess?.invoke(report)
            } catch (e: Exception) {
                _isLoading.value = false
                val userMessage = when (e) {
                    is[IllegalArgumentException] -> e.message ?: "Invalid input data"
                    is[IllegalStateException] -> e.message ?: "Failed to submit emergency report"
                    else -> "An unexpected error occurred. Please try again."
                }
                _errorMessage.value = userMessage
                // Constitution VII: Log error without exposing secrets
                ErrorHandler.logError("reportEmergency", e, mapOf(
                    "emergencyType" to emergencyType,
                    "location" to location
                ))
            }
        }
    }
}