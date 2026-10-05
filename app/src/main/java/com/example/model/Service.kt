package com.example.model

enum class RequestStatus(val label: String) {
    SUBMITTED("Submitted"),
    UNDER_REVIEW("Under Review"),
    PROCESSING("Processing"),
    READY("Ready for Release"),
    COMPLETED("Completed"),
    REJECTED("Rejected")
}

data class ServiceRequirement(
    val title: String,
    val description: String,
    val isMandatory: Boolean = true
)

data class BarangayService(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val purposeExamples: List<String>,
    val requirements: List<ServiceRequirement>,
    val processingDays: String,
    val feeDescription: String,
    val iconKey: String
)

data class RequestTimelineEvent(
    val title: String,
    val description: String,
    val timestamp: Long,
    val actorName: String
)

data class DocumentRequest(
    val id: String = "",
    val referenceNumber: String = "",
    val serviceId: String = "",
    val serviceName: String = "",
    val residentUid: String = "",
    val residentName: String = "",
    val residentAddress: String = "",
    val residentContact: String = "",
    val purpose: String = "",
    val remarks: String = "",
    val deliveryMethod: String = "Pick-up at Barangay Hall",
    val status: RequestStatus = RequestStatus.SUBMITTED,
    val officialRemarks: String = "",
    val attachmentNames: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val timeline: List<RequestTimelineEvent> = emptyList(),
    val isSyncedToServer: Boolean = true
)
