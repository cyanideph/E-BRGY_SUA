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
    val address: String = "",
    val mobileNumber: String = "",
    val dateOfBirth: String = "",
    val civilStatus: String = "",
    val sex: String = "",
    val occupation: String = "",
    val householdId: String = "",
    val registrationStatus: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = ""
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
