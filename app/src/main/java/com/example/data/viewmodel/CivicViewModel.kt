package com.example.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Announcement
import com.example.data.AnnouncementCategory
import com.example.data.AnnouncementPriority
import com.example.data.BarangayEvent
import com.example.data.official.BarangayOfficial
import com.example.data.official.OfficialHotline
import com.example.data.facility.Facility
import com.example.repository.BarangayRepository

/**
 * CivicViewModel manages public civic data: announcements, events, officials,
 * hotlines, and facilities. Backend (Appwrite) is authoritative; Room is cache.
 */
class CivicViewModel(repository: BarangayRepository = BarangayRepository.instance) : ViewModel() {

    // Announcements
    private val _announcements = repository.announcements
    val announcements = _announcements.asStateFlow()

    // Events
    private val _events = repository.events
    val events = _events.asStateFlow()

    // Officials
    private val _officials = repository.officials
    val officials = _officials.asStateFlow()

    // Hotlines
    private val _hotlines = repository.hotlines
    val hotlines = _hotlines.asStateFlow()

    // Facilities
    private val _facilities = repository.facilities
    val facilities = _facilities.asStateFlow()

    // Refresh all civic data from backend
    refreshPublicData()

    suspend fun refreshPublicData() {
        repository.refreshPublicData()
    }
}