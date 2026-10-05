package com.example.model

enum class AnnouncementCategory(val displayName: String) {
    GENERAL("General"),
    COMMUNITY("Community"),
    HEALTH("Health"),
    DISASTER("Disaster & Weather"),
    EVENTS("Events"),
    EMERGENCY("Emergency Advisory")
}

enum class AnnouncementPriority(val displayName: String) {
    NORMAL("Normal"),
    IMPORTANT("Important"),
    EMERGENCY("Emergency")
}

data class Announcement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: AnnouncementCategory = AnnouncementCategory.GENERAL,
    val priority: AnnouncementPriority = AnnouncementPriority.NORMAL,
    val publishedDate: String = "",
    val authorName: String = "Barangay Information Office",
    val authorRole: String = "Barangay Sua Secretary",
    val isPinned: Boolean = false,
    val imageUrl: String? = null
)
