package com.example.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.EmergencyReport
import com.example.data.EmergencyStatus
import com.example.repository.BarangayRepository

/**
 * EmergencyViewModel manages emergency/SOS report lifecycle.
 * Backend is authoritative; local Room cache is updated after successful sync.
 */
class EmergencyViewModel(repository: BarangayRepository = BarangayRepository.instance) : ViewModel() {

    // UI state: emergency reports list
    private val _emergencyReports = repository.emergencyReports
    val emergencyReports = _emergencyReports.asStateFlow()

    // UI state: submitting status
    private val _isSubmitting = androidx.lifecycle.MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    // Submit a new emergency report
    suspend fun submitEmergency(
        type: EmergencyType,
        description: String,
        latitude: Double?,
        longitude: Double?,
        locationDescription: String
    ): EmergencyReport {
        _isSubmitting.value = true
        try {
            val result = repository.submitEmergency(
                type = type,
                description = description,
                latitude = latitude,
                longitude = longitude,
                locationDescription = locationDescription
            )
            return result
        } finally {
            _isSubmitting.value = false
        }
    }

    // Update emergency status (staff/admin only)
    fun updateEmergencyStatus(
        reportId: String,
        newStatus: EmergencyStatus,
        assignedResponder: String,
        notes: String
    ) {
        repository.updateEmergencyStatus(reportId, newStatus, assignedResponder, notes)
    }

    // Convenience: get emergency report by ID
    fun getEmergencyById(id: String): EmergencyReport? {
        return _emergencyReports.value.firstOrNull { it.id == id }
    }
}