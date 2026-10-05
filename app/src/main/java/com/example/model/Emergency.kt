package com.example.model

enum class EmergencyType(val displayName: String, val iconDescription: String) {
    BARANGAY_EMERGENCY("Barangay Emergency", "Tanod & Local Patrol"),
    MEDICAL("Medical Emergency", "Ambulance & Rural Health Unit"),
    FIRE("Fire Emergency", "Bureau of Fire Protection"),
    POLICE("Police Assistance", "Philippine National Police"),
    RESCUE_DISASTER("Rescue & Coastal Disaster", "MDRRMO & Coast Guard")
}

enum class EmergencyStatus(val label: String) {
    RECEIVED("Report Received"),
    RESPONDING("Units Dispatched"),
    RESOLVED("Incident Resolved")
}

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
