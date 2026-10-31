package com.example.model

enum class EmergencyType(val displayName: String, val iconDescription: String) {
    BARANGAY_EMERGENCY("Barangay Emergency", "Tanod & Local Patrol"),
    MEDICAL("Medical Emergency", "Ambulance & Rural Health Unit"),
    FIRE("Fire Emergency", "Bureau of Fire Protection"),
    POLICE("Police Assistance", "Philippine National Police"),
    RESCUE_DISASTER("Rescue & Coastal Disaster", "MDRRMO & Coast Guard"),
    TANOD_PATROL("Tanod Patrol", "Barangay Security Patrol")
}

enum class EmergencyStatus(val label: String) {
    RECEIVED("Report Received"),
    RESPONDING("Units Dispatched"),
    RESOLVED("Incident Resolved")
}

data class TanodPatrolReport(
    val id: String = "",
    val officerName: String = "",
    val patrolArea: String = "",
    val status: String = "Active",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val locationLatitude: Double? = null,
    val locationLongitude: Double? = null
)

data class EmergencyReport(
    val id: String = "",
    val type: EmergencyType = EmergencyType.BARANGAY_EMERGENCY,
    val description: String = "",
    val residentName: String = "",
    val residentContact: String = "",
    val residentUid: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationDescription: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: EmergencyStatus = EmergencyStatus.RECEIVED,
    val assignedResponder: String = "",
    val responseNotes: String = ""
)

data class OfficialHotline(
    val name: String,
    val number: String,
    val agency: String,
    val description: String
)

data class PhilSysRecord(
    val philId: String = "",
    val fullName: String = "",
    val dateOfBirth: String = "",
    val address: String = "",
    val gender: String = "",
    val civilStatus: String = "",
    val bloodType: String = "",
    val dateIssued: String = "",
    val expiryDate: String = ""
)
