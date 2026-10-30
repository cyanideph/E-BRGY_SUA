package com.example.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DocumentRequest
import com.example.data.RequestStatus
import com.example.data.service.BarangayService
import com.example.repository.BarangayRepository

/**
 * RequestsViewModel manages document request lifecycle.
 * The Repository remains authoritative for backend sync; this ViewModel
 * exposes UI-friendly state and handles submit/update operations.
 */
class RequestsViewModel(repository: BarangayRepository = BarangayRepository.instance) : ViewModel() {

    // UI state: current requests list
    private val _requests = repository.requests
    val requests = _requests.asStateFlow()

    // UI state: loading/submitting status
    private val _isSubmitting = androidx.lifecycle.MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    // Submit a new document request
    suspend fun submitRequest(
        service: BarangayService,
        purpose: String,
        deliveryMethod: String,
        remarks: String,
        attachmentNames: List<String>
    ): DocumentRequest {
        _isSubmitting.value = true
        try {
            val result = repository.submitRequest(
                service = service,
                purpose = purpose,
                deliveryMethod = deliveryMethod,
                remarks = remarks,
                attachmentNames = attachmentNames
            )
            // Refresh local cache after successful submit
            // The Repository already updates _requests.value internally
            return result
        } finally {
            _isSubmitting.value = false
        }
    }

    // Update request status (staff/admin only)
    fun updateRequestStatus(
        requestId: String,
        newStatus: RequestStatus,
        officialRemarks: String
    ) {
        repository.updateRequestStatus(requestId, newStatus, officialRemarks)
    }

    // Convenience: get a request by its reference number
    fun getRequestByRef(refNum: String): DocumentRequest? {
        return _requests.value.firstOrNull { it.referenceNumber == refNum }
    }

    // Convenience: get a request by its ID
    fun getRequestById(id: String): DocumentRequest? {
        return _requests.value.firstOrNull { it.id == id }
    }
}