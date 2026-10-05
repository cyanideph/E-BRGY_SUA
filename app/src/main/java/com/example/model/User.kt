package com.example.model

enum class UserRole(val displayName: String, val level: Int) {
    RESIDENT("Resident", 1),
    STAFF("Barangay Staff", 2),
    OFFICIAL("Barangay Official", 3),
    ADMIN("System Administrator", 4)
}

data class ResidentProfile(
    val id: String = "",
    val residentId: String = "",
    val fullName: String = "",
    val address: String = "Purok 1, Barangay Sua, San Juan, Southern Leyte",
    val mobileNumber: String = "+63 917 555 0192",
    val dateOfBirth: String = "1992-06-15",
    val civilStatus: String = "Single",
    val sex: String = "Female",
    val occupation: String = "Fisheries Co-op Member",
    val householdId: String = "HH-SUA-0012",
    val registrationStatus: String = "Verified Resident",
    val emergencyContactName: String = "Maria Santos (Mother)",
    val emergencyContactPhone: String = "+63 920 123 4567"
)

data class Household(
    val id: String = "",
    val householdNumber: String = "",
    val headName: String = "",
    val address: String = "",
    val memberCount: Int = 1,
    val memberNames: List<String> = emptyList(),
    val emergencyNotes: String = ""
)

data class UserSession(
    val uid: String = "",
    val email: String = "",
    val role: UserRole = UserRole.RESIDENT,
    val profile: ResidentProfile = ResidentProfile()
)
