package com.example.ui.resident.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.provider.appwrite.AuthProvider
import com.example.domain.model.ServiceRequest
import com.example.domain.model.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.data.error.ErrorHandler

/** Service Request ViewModel - handles service request submission business logic.
 * 
 *  Follows constitution principles:
 *  - II: Maintainable and secure architecture (clean separation between UI, domain, data)
 *  - VI: Data integrity (all validation at repository layer before writes)
 *  - VIII: Testing before merging (tests must pass before feature merge)
 *  - IX: Compatibility with Android 7.0+ (min SDK 24)
 */
class ServiceRequestViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: MutableStateFlow<bool> = _isLoading

    private val _success = MutableStateFlow<Boolean>(false)
    val success: MutableStateFlow<bool> = _success

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: MutableStateFlow<String?> = _errorMessage

    private val serviceProvider: AuthProvider = AuthProvider(AppwriteDataSource())

    /** Submit service request */
    fun submitServiceRequest(
        serviceType: String,
        description: String,
        residentName: String,
        address: String,
        contactNumber: String,
        onSuccess: ((ServiceRequest) -> Unit)?
    ) {
        _isLoading.value = true
        _errorMessage.value = null
        _success.value = false

        viewModelScope.launch {
            try {
                // Build service request domain model
                val request = ServiceRequest(
                    type = serviceType,
                    status = "PENDING",
                    requestedBy = "", // Will be set from authenticated resident
                    description = description,
                    requiredDocuments = emptyList(), // To be filled later
                    requestDate = java.time.LocalDate.now().toString()
                )

                withContext(Dispatchers.IO) {
                    serviceProvider.createServiceRequest(request).firstOrNull()
                        ?: throw IllegalStateException("Service request submission failed")
                }

                _isLoading.value = false
                _success.value = true
                onSuccess?.invoke(request)
            } catch (e: Exception) {
                _isLoading.value = false
                val userMessage = when (e) {
                    is[IllegalArgumentException] -> e.message ?: "Invalid input data"
                    is[IllegalStateException] -> e.message ?: "Failed to submit service request"
                    else -> "An unexpected error occurred. Please try again."
                }
                _errorMessage.value = userMessage
                // Constitution VII: Log error without exposing secrets
                ErrorHandler.logError("submitServiceRequest", e, mapOf(
                    "serviceType" to serviceType,
                    "residentName" to residentName
                ))
            }
        }
    }
}