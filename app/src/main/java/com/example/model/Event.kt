package com.example.model

data class BarangayEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "Barangay Sua Multipurpose Hall",
    val organizer: String = "Barangay Council of Sua",
    val category: String = "Assembly",
    val rsvpCount: Int = 0,
    val isUserRsvpd: Boolean = false
)
