package com.example.model

enum class RequestStatus(val label: String) {
    SUBMITTED("Submitted"),
    UNDER_REVIEW("Under Review"),
    PROCESSING("Processing"),
    READY("Ready for Release"),
    COMPLETED("Completed"),
    REJECTED("Rejected"),
    FIRST_TIME_JOBSEEKER_CLEARANCE("First-Time Jobseeker Clearance (RA 11261)")
}

data class ServiceRequirement(
    val title: String,
    val description: String,
    val isMandatory: Boolean = true
)

data class FirstTimeJobseekerWaiver(
    val applicantName: String,
    val course: String,
    val graduationDate: String,
    val employmentStatus: String,
    val barangayClearanceDate: String
)

data class FinancialReport(
    val id: String = "",
    val reportType: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val currency: String = "PHP",
    val category: String = "General",
    val date: String = "",
    val preparedBy: String = "",
    val approvedBy: String? = null,
    val status: String = "Pending",
    val createdAt: Long = System.currentTimeMillis()
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
    val iconKey: String,
    val isFirstTimeJobseekerWaiverApplicable: Boolean = false
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
    val deliveryMethod: String = "",
    val status: RequestStatus = RequestStatus.SUBMITTED,
    val officialRemarks: String = "",
    val attachmentNames: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val timeline: List<RequestTimelineEvent> = emptyList(),
    val isSyncedToServer: Boolean = true
)
