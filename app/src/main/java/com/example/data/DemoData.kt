package com.example.data

import com.example.model.*

object DemoData {

    val services = listOf(
        BarangayService(
            id = "srv_clearance",
            name = "Barangay Clearance",
            category = "Identity & Verification",
            description = "Official certification issued to residents confirming clean record and residency in Barangay Sua for local employment, postal ID, government transactions, or banking.",
            purposeExamples = listOf(
                "Local Employment Application",
                "Postal ID / Government ID Application",
                "Bank Account Opening",
                "Police / NBI Clearance Requirement",
                "Scholarship / Financial Grant"
            ),
            requirements = listOf(
                ServiceRequirement("Valid Government ID or School ID", "Government-issued ID showing your identity and photo", true),
                ServiceRequirement("Proof of Residency", "Utility bill or household certification in Barangay Sua", true),
                ServiceRequirement("Community Tax Certificate (Cedula)", "Current year cedula issued by the Municipal Treasurer", true),
                ServiceRequirement("Recent 2x2 ID Photo", "Clean white background photo taken within the last 6 months", false)
            ),
            processingDays = "1 to 2 Working Days",
            feeDescription = "PHP 50.00 (Waived for First-Time Jobseekers under RA 11261)",
            iconKey = "verified"
        ),
        BarangayService(
            id = "srv_residency",
            name = "Certificate of Residency",
            category = "Identity & Verification",
            description = "Certifies that the applicant is a bona fide resident of Barangay Sua, San Juan, Southern Leyte with at least six months of continuous domicile.",
            purposeExamples = listOf(
                "Driver's License Application",
                "Passport Application / DFA Requirement",
                "Water / Electric Meter Installation",
                "School Admission & Transfer",
                "Proof of Domicile"
            ),
            requirements = listOf(
                ServiceRequirement("Valid Government ID", "Any photo ID with address", true),
                ServiceRequirement("Purok Leader Endorsement", "Signed slip or certification from your designated Purok leader", true),
                ServiceRequirement("Minimum 6 Months Stay", "Verified in Barangay Household Registry", true)
            ),
            processingDays = "1 Working Day",
            feeDescription = "PHP 40.00",
            iconKey = "home"
        ),
        BarangayService(
            id = "srv_indigency",
            name = "Certificate of Indigency",
            category = "Social Assistance",
            description = "Issued to low-income residents and families seeking financial, medical, or educational assistance from government agencies or hospitals.",
            purposeExamples = listOf(
                "Medical Assistance (DSWD / Malasakit Center / PCSO)",
                "Hospital Bill Assistance",
                "Educational Scholarship / Student Grant",
                "Burial / Funeral Financial Aid",
                "Public Attorney's Office (PAO) Legal Aid"
            ),
            requirements = listOf(
                ServiceRequirement("Valid ID of Applicant", "Government or voter's ID", true),
                ServiceRequirement("Proof of Need", "Hospital bill, medical prescription, or enrollment assessment form", true),
                ServiceRequirement("Social Worker or Purok Verification", "Confirmation of income bracket and household status", true)
            ),
            processingDays = "Same Day to 1 Working Day",
            feeDescription = "Free of Charge (Gratis)",
            iconKey = "handshake"
        ),
        BarangayService(
            id = "srv_certificate",
            name = "Barangay Certificate",
            category = "General Certification",
            description = "General-purpose certification confirming good community standing, residency, or specific factual circumstances verified by the Barangay Council.",
            purposeExamples = listOf(
                "Proof of Income / Livelihood",
                "Senior Citizen / Solo Parent Application",
                "Travel / Relocation Documentation",
                "Community Reference"
            ),
            requirements = listOf(
                ServiceRequirement("Valid ID", "Government issued identification", true),
                ServiceRequirement("Specific Purpose Statement", "Written note or agency form requesting certification", true)
            ),
            processingDays = "1 Working Day",
            feeDescription = "PHP 40.00",
            iconKey = "description"
        ),
        BarangayService(
            id = "srv_good_moral",
            name = "Good Moral Certificate",
            category = "Identity & Verification",
            description = "Official declaration attesting to the applicant's peaceful standing and good moral character in Barangay Sua, with no pending blotter or criminal record.",
            purposeExamples = listOf(
                "College or University Enrollment",
                "Board Examination / PRC Requirement",
                "Armed Forces / PNP / Coast Guard Enlistment",
                "Overseas Employment Document"
            ),
            requirements = listOf(
                ServiceRequirement("Valid Government ID", "Any official ID", true),
                ServiceRequirement("Barangay Blotter Clearance", "Clearance check conducted by Barangay Tanod Desk", true),
                ServiceRequirement("2 Character References", "From teachers, community leaders, or church officials", false)
            ),
            processingDays = "1 to 2 Working Days",
            feeDescription = "PHP 50.00",
            iconKey = "shield"
        ),
        BarangayService(
            id = "srv_business_clearance",
            name = "Business Clearance",
            category = "Commerce & Livelihood",
            description = "Required clearance for operating small commercial establishments, sari-sari stores, coastal eateries, fish processing, and home-based businesses within Barangay Sua.",
            purposeExamples = listOf(
                "New Business Mayor's Permit Application",
                "Annual Business Permit Renewal",
                "DTI Business Name Registration",
                "Cooperative Operations"
            ),
            requirements = listOf(
                ServiceRequirement("DTI Certificate of Registration", "Business name registration document", true),
                ServiceRequirement("Location / Sanitary Inspection", "Satisfactory check by Barangay Health & Sanitation committee", true),
                ServiceRequirement("Fire Safety Inspection Slip", "Preliminary inspection recommendation", true),
                ServiceRequirement("Lease Agreement or Title", "Proof of commercial venue rights", true)
            ),
            processingDays = "2 to 3 Working Days",
            feeDescription = "PHP 150.00 - PHP 300.00 (Depends on enterprise classification)",
            iconKey = "store"
        ),
        BarangayService(
            id = "srv_other",
            name = "Other Barangay Services",
            category = "Public Services",
            description = "Assistance for first-time jobseekers, oath of undertakings, mediation referrals under Katarungang Pambarangay, and custom administrative certifications.",
            purposeExamples = listOf(
                "First-Time Jobseeker RA 11261 Certification",
                "Amicable Settlement Referral",
                "Tree Cutting / Coconut Transport Permit Endorsement",
                "Community Facility Reservation"
            ),
            requirements = listOf(
                ServiceRequirement("Valid ID", "Government or school ID", true),
                ServiceRequirement("Letter of Request", "Addressed to Punong Barangay", true)
            ),
            processingDays = "1 to 3 Working Days",
            feeDescription = "Free for First-Time Jobseekers / Standard administrative stamp",
            iconKey = "more_horiz"
        )
    )

    val officials = listOf(
        BarangayOfficial("off_1", "RAYMUND Q. VASQUEZ", "Punong Barangay", "Executive", "0", "", "Barangay Executive Office", false),
        BarangayOfficial("off_2", "LEONIDES Y. MALUBAY", "Sangguniang Barangay Member", "Council", "0", "", "Barangay Council", false),
        BarangayOfficial("off_3", "RENE O. BALIC", "Sangguniang Barangay Member", "Council", "0", "", "Barangay Council", false),
        BarangayOfficial("off_4", "MARCIANITO D. BALABA", "Sangguniang Barangay Member", "Council", "0", "", "Barangay Council", false),
        BarangayOfficial("off_5", "EVELYN D. RANEZ", "Sangguniang Barangay Member", "Council", "0", "", "Barangay Council", false),
        BarangayOfficial("off_6", "JESUS D. POJAS", "Sangguniang Barangay Member", "Council", "0", "", "Barangay Council", false),
        BarangayOfficial("off_7", "MELBOY S. MONTER", "Sangguniang Barangay Member", "Council", "0", "", "Barangay Council", false),
        BarangayOfficial("off_8", "PANFILO S. CORAZON", "Sangguniang Barangay Member", "Council", "0", "", "Barangay Council", false),
        BarangayOfficial("off_9", "ALEX F. QUIBAN", "SK Chairperson", "Youth Council", "N/A", "", "Sangguniang Kabataan", false, "quibanalex2@gmail.com"),
        BarangayOfficial("off_10", "ANALYN F. QUIBAN", "Barangay Secretary", "Administration", "0", "", "Barangay Secretariat & Records", false)
    )

    val hotlines = listOf(
        OfficialHotline("San Juan Municipal Police Station", "+63 998 598 6789", "Philippine National Police", "Direct 24/7 police dispatch for San Juan, Southern Leyte"),
        OfficialHotline("San Juan MDRRMO Emergency Operations", "+63 917 800 2345", "Disaster Risk Reduction Office", "Coastal rescue, flood, storm surge, and ambulance service"),
        OfficialHotline("Bureau of Fire Protection - San Juan", "+63 929 123 4567", "BFP Southern Leyte", "Fire suppression and rescue team"),
        OfficialHotline("Southern Leyte Provincial Hospital", "+63 53 570 9000", "Provincial Health Center", "Level-2 emergency hospital and trauma unit"),
        OfficialHotline("Philippine Coast Guard Substation San Juan", "+63 927 654 3210", "PCG Eastern Visayas", "Maritime safety, maritime search, and coastal distress"),
        OfficialHotline("Barangay Sua Tanod Desk & Night Watch", "+63 930 112 3344", "Barangay Sua Security", "Local emergency first responder outpost")
    )

    val announcements = listOf(
        Announcement(
            id = "ann_1",
            title = "Coastal Clean-Up and Marine Sanctuary Conservation Day",
            description = "In coordination with the San Juan Municipal Environment and Natural Resources Office, Barangay Sua will conduct its quarterly coastal preservation drive along the Sua shoreline. All puroks, youth leaders, and fishermen cooperatives are encouraged to participate. Garbage sacks, gloves, and coastal collection points will be stationed at Purok 1 and Purok 3.",
            category = AnnouncementCategory.DISASTER,
            priority = AnnouncementPriority.IMPORTANT,
            publishedDate = "October 2, 2026",
            authorName = "Hon. Roberto Mendoza",
            authorRole = "Chairman, Committee on Coastal Environment",
            isPinned = true
        ),
        Announcement(
            id = "ann_2",
            title = "Monthly Free Community Medical and Pediatric Consultation",
            description = "The Municipal Health Office of San Juan together with our Barangay Health Workers will hold a free medical mission at the Barangay Sua Multipurpose Hall this coming Saturday. Services include general checkup, blood pressure screening, immunization for infants, and distribution of essential maintenance medicines for senior citizens.",
            category = AnnouncementCategory.HEALTH,
            priority = AnnouncementPriority.NORMAL,
            publishedDate = "September 29, 2026",
            authorName = "Mrs. Lilibeth M. Flores",
            authorRole = "Head Barangay Health Worker",
            isPinned = false
        ),
        Announcement(
            id = "ann_3",
            title = "Notice of Second Semester General Barangay Assembly",
            description = "Pursuant to the Local Government Code, notice is hereby given to all registered voting residents of Barangay Sua for the 2026 Second Semester Barangay Assembly. Agenda includes the Punong Barangay's State of the Barangay Address, financial treasury report, disaster readiness briefing, and community open forum.",
            category = AnnouncementCategory.COMMUNITY,
            priority = AnnouncementPriority.IMPORTANT,
            publishedDate = "September 25, 2026",
            authorName = "Ms. Arlene P. Castillo",
            authorRole = "Barangay Secretary",
            isPinned = false
        ),
        Announcement(
            id = "ann_4",
            title = "Gale Advisory: High Ocean Swell Warning for Southern Leyte Coastline",
            description = "PAGASA weather advisory warns of strong northeasterly swells affecting the eastern seaboard of Southern Leyte. Small sea crafts and artisanal fishermen in Barangay Sua are strongly advised to suspend sailing until sea conditions moderate. Barangay Tanods are on heightened watch along beach sectors.",
            category = AnnouncementCategory.EMERGENCY,
            priority = AnnouncementPriority.EMERGENCY,
            publishedDate = "October 4, 2026",
            authorName = "Mr. Victorino B. Santos",
            authorRole = "Chief Tanod",
            isPinned = true
        )
    )

    val events = listOf(
        BarangayEvent(
            id = "evt_1",
            title = "Barangay Sua General Assembly 2026",
            description = "Annual assembly for all residents of Barangay Sua to discuss community development projects, budget allocation, and local resolutions.",
            date = "October 18, 2026",
            time = "9:00 AM - 12:30 PM",
            location = "Barangay Sua Multipurpose Covered Court",
            organizer = "Barangay Council of Sua",
            category = "Assembly",
            rsvpCount = 142,
            isUserRsvpd = true
        ),
        BarangayEvent(
            id = "evt_2",
            title = "Coastal Mangrove Planting & Bay Rehabilitation",
            description = "Community volunteer activity to plant 500 mangrove propagules along the protected coastal buffer zone of Barangay Sua.",
            date = "October 24, 2026",
            time = "6:30 AM - 10:00 AM",
            location = "Sua Marine Buffer Zone, San Juan",
            organizer = "Sua Youth Council & Fisheries Co-op",
            category = "Environment",
            rsvpCount = 88,
            isUserRsvpd = false
        ),
        BarangayEvent(
            id = "evt_3",
            title = "Inter-Purok Youth Sports and Volleyball Cup",
            description = "Friendly sports tournament fostering camaraderie and youth wellness across all puroks of Barangay Sua.",
            date = "November 5, 2026",
            time = "3:30 PM - 7:00 PM",
            location = "Barangay Sua Sports Grounds",
            organizer = "Sangguniang Kabataan (SK) Sua",
            category = "Sports & Youth",
            rsvpCount = 65,
            isUserRsvpd = false
        ),
        BarangayEvent(
            id = "evt_4",
            title = "Senior Citizens Wellness and Nutrition Morning",
            description = "Zumba gold, blood sugar test, dietary guidance, and distribution of quarterly vitamin supplements for all elderly residents.",
            date = "November 14, 2026",
            time = "7:30 AM - 11:00 AM",
            location = "Barangay Sua Health Center Grounds",
            organizer = "Barangay Health Center & OSCA",
            category = "Health & Senior",
            rsvpCount = 74,
            isUserRsvpd = true
        )
    )

    val initialRequests = listOf(
        DocumentRequest(
            id = "req_101",
            referenceNumber = "BRG-SUA-2026-000101",
            serviceId = "srv_clearance",
            serviceName = "Barangay Clearance",
            residentUid = "res_user_1",
            residentName = "Elena B. Alcantara",
            residentAddress = "Purok 2, Barangay Sua, San Juan, Southern Leyte",
            residentContact = "+63 917 555 0192",
            purpose = "Local Employment Application at San Juan Commercial Center",
            remarks = "Attached photo of Cedula and PhilSys National ID.",
            deliveryMethod = "Pick-up at Barangay Hall",
            status = RequestStatus.READY,
            officialRemarks = "Document signed by Punong Barangay. Ready for pick-up at Window 2. Please present original ID.",
            attachmentNames = listOf("philsys_id_front.jpg", "cedula_2026.pdf"),
            createdAt = System.currentTimeMillis() - (86400000L * 2),
            updatedAt = System.currentTimeMillis() - 3600000L,
            timeline = listOf(
                RequestTimelineEvent("Application Submitted", "Request received via e-Barangay Sua portal", System.currentTimeMillis() - (86400000L * 2), "Elena B. Alcantara"),
                RequestTimelineEvent("Under Review", "Records and blotter check initiated by Secretariat", System.currentTimeMillis() - (86400000L * 1), "Ms. Arlene P. Castillo"),
                RequestTimelineEvent("Processing Complete", "Clearance issued and signed by Hon. Alejandro Fernandez", System.currentTimeMillis() - 14400000L, "Secretariat"),
                RequestTimelineEvent("Ready for Release", "Document printed with official seal and dry seal", System.currentTimeMillis() - 3600000L, "Window 2 Officer")
            ),
            isSyncedToServer = true
        ),
        DocumentRequest(
            id = "req_102",
            referenceNumber = "BRG-SUA-2026-000102",
            serviceId = "srv_residency",
            serviceName = "Certificate of Residency",
            residentUid = "res_user_1",
            residentName = "Elena B. Alcantara",
            residentAddress = "Purok 2, Barangay Sua, San Juan, Southern Leyte",
            residentContact = "+63 917 555 0192",
            purpose = "Water Meter Connection Application (San Juan Water District)",
            remarks = "Resident of Purok 2 for 4 years.",
            deliveryMethod = "Digital Copy & Hall Pick-up",
            status = RequestStatus.PROCESSING,
            officialRemarks = "Verified with Purok 2 leader endorsement.",
            attachmentNames = listOf("water_district_endorsement.pdf"),
            createdAt = System.currentTimeMillis() - (86400000L * 1),
            updatedAt = System.currentTimeMillis() - 7200000L,
            timeline = listOf(
                RequestTimelineEvent("Application Submitted", "Received online application", System.currentTimeMillis() - (86400000L * 1), "Elena B. Alcantara"),
                RequestTimelineEvent("Under Review", "Purok 2 household registry verified", System.currentTimeMillis() - 18000000L, "Ms. Arlene P. Castillo"),
                RequestTimelineEvent("Processing", "Document drafted for official signing", System.currentTimeMillis() - 7200000L, "Barangay Staff")
            ),
            isSyncedToServer = true
        ),
        DocumentRequest(
            id = "req_103",
            referenceNumber = "BRG-SUA-2026-000098",
            serviceId = "srv_indigency",
            serviceName = "Certificate of Indigency",
            residentUid = "res_user_1",
            residentName = "Elena B. Alcantara",
            residentAddress = "Purok 2, Barangay Sua, San Juan, Southern Leyte",
            residentContact = "+63 917 555 0192",
            purpose = "Hospital Medical Prescription Assistance (Malasakit Center)",
            remarks = "Assistance for mother's maintenance medicines.",
            deliveryMethod = "Pick-up at Barangay Hall",
            status = RequestStatus.COMPLETED,
            officialRemarks = "Document claimed on Sep 20, 2026. Signed receipt on file.",
            attachmentNames = listOf("medical_prescription.jpg"),
            createdAt = System.currentTimeMillis() - (86400000L * 14),
            updatedAt = System.currentTimeMillis() - (86400000L * 13),
            timeline = listOf(
                RequestTimelineEvent("Application Submitted", "Submitted online", System.currentTimeMillis() - (86400000L * 14), "Elena B. Alcantara"),
                RequestTimelineEvent("Processed", "Certified by Social Services Committee", System.currentTimeMillis() - (86400000L * 13), "Hon. Carmen Del Rosario"),
                RequestTimelineEvent("Completed", "Official hardcopy released to resident", System.currentTimeMillis() - (86400000L * 13), "Records Section")
            ),
            isSyncedToServer = true
        )
    )

    val demoResidents = listOf(
        ResidentProfile(
            id = "res_1",
            residentId = "RES-SUA-2024-0012",
            fullName = "Elena B. Alcantara",
            address = "Purok 2, Coastal Road, Barangay Sua, San Juan",
            mobileNumber = "+63 917 555 0192",
            dateOfBirth = "1994-08-22",
            civilStatus = "Single",
            sex = "Female",
            occupation = "Community Fishery Co-op Officer",
            householdId = "HH-SUA-0012",
            registrationStatus = "Verified Resident"
        ),
        ResidentProfile(
            id = "res_2",
            residentId = "RES-SUA-2023-0045",
            fullName = "Ramon G. Bautista",
            address = "Purok 1, Beachfront, Barangay Sua, San Juan",
            mobileNumber = "+63 920 334 1122",
            dateOfBirth = "1985-03-11",
            civilStatus = "Married",
            sex = "Male",
            occupation = "Artisanal Fisherman / Boat Operator",
            householdId = "HH-SUA-0005",
            registrationStatus = "Verified Resident"
        ),
        ResidentProfile(
            id = "res_3",
            residentId = "RES-SUA-2022-0089",
            fullName = "Lourdes M. Catubig",
            address = "Purok 3, Hillside, Barangay Sua, San Juan",
            mobileNumber = "+63 919 778 9900",
            dateOfBirth = "1960-11-04",
            civilStatus = "Widowed",
            sex = "Female",
            occupation = "Sari-Sari Store Owner",
            householdId = "HH-SUA-0021",
            registrationStatus = "Verified Resident"
        ),
        ResidentProfile(
            id = "res_4",
            residentId = "RES-SUA-2025-0104",
            fullName = "Mark Anthony D. Soriano",
            address = "Purok 2, Barangay Sua, San Juan",
            mobileNumber = "+63 995 123 4567",
            dateOfBirth = "2002-05-19",
            civilStatus = "Single",
            sex = "Male",
            occupation = "College Student (Information Technology)",
            householdId = "HH-SUA-0014",
            registrationStatus = "Verified Resident"
        ),
        ResidentProfile(
            id = "res_5",
            residentId = "RES-SUA-2021-0003",
            fullName = "Teresa P. Mendoza",
            address = "Purok 1, Barangay Sua, San Juan",
            mobileNumber = "+63 918 890 1234",
            dateOfBirth = "1978-09-30",
            civilStatus = "Married",
            sex = "Female",
            occupation = "Elementary Public School Teacher",
            householdId = "HH-SUA-0002",
            registrationStatus = "Verified Resident"
        )
    )

    val demoHouseholds = listOf(
        Household(
            id = "hh_1",
            householdNumber = "HH-SUA-0012",
            headName = "Josefa B. Alcantara",
            address = "Purok 2, Coastal Road, Barangay Sua",
            memberCount = 4,
            memberNames = listOf("Josefa B. Alcantara (Head)", "Elena B. Alcantara (Daughter)", "Manuel B. Alcantara (Son)", "Sofia B. Alcantara (Granddaughter)"),
            emergencyNotes = "Proximity to shoreline; prioritized in storm surge advisory"
        ),
        Household(
            id = "hh_2",
            householdNumber = "HH-SUA-0005",
            headName = "Ramon G. Bautista",
            address = "Purok 1, Beachfront, Barangay Sua",
            memberCount = 5,
            memberNames = listOf("Ramon G. Bautista (Head)", "Corazon V. Bautista (Spouse)", "John Paul Bautista (Son)", "Anna Rose Bautista (Daughter)", "Lito Bautista (Son)"),
            emergencyNotes = "Operates motorized motorized banca for coastal rescue assistance"
        ),
        Household(
            id = "hh_3",
            householdNumber = "HH-SUA-0021",
            headName = "Lourdes M. Catubig",
            address = "Purok 3, Hillside, Barangay Sua",
            memberCount = 2,
            memberNames = listOf("Lourdes M. Catubig (Head/Senior)", "Gerald C. Santos (Grandson)"),
            emergencyNotes = "Senior citizen with mobility assistance needs"
        ),
        Household(
            id = "hh_4",
            householdNumber = "HH-SUA-0014",
            headName = "Eduardo P. Soriano",
            address = "Purok 2, Barangay Sua",
            memberCount = 4,
            memberNames = listOf("Eduardo P. Soriano (Head)", "Miriam D. Soriano (Spouse)", "Mark Anthony D. Soriano (Son)", "Angelica D. Soriano (Daughter)"),
            emergencyNotes = "Designated secondary neighborhood assembly point"
        )
    )

    val notifications = listOf(
        BarangayNotification(
            id = "notif_1",
            title = "Document Ready for Release",
            message = "Your Barangay Clearance (BRG-SUA-2026-000101) is signed and ready for pick-up at Window 2.",
            timestamp = System.currentTimeMillis() - 3600000L,
            isRead = false,
            category = "Service Request",
            priority = "Important",
            referenceId = "BRG-SUA-2026-000101"
        ),
        BarangayNotification(
            id = "notif_2",
            title = "Emergency Gale Advisory",
            message = "High ocean swells warning issued for coastal sectors of Barangay Sua. Artisanal sailing suspended.",
            timestamp = System.currentTimeMillis() - 7200000L,
            isRead = false,
            category = "Emergency Advisory",
            priority = "Emergency",
            referenceId = "ann_4"
        ),
        BarangayNotification(
            id = "notif_3",
            title = "Request Under Review",
            message = "Your Certificate of Residency application is currently undergoing household registry validation.",
            timestamp = System.currentTimeMillis() - 18000000L,
            isRead = true,
            category = "Service Request",
            priority = "Normal",
            referenceId = "BRG-SUA-2026-000102"
        ),
        BarangayNotification(
            id = "notif_4",
            title = "Upcoming Barangay Assembly",
            message = "Join us on October 18, 2026 at the Multipurpose Covered Court for the 2nd Semester Assembly.",
            timestamp = System.currentTimeMillis() - (86400000L * 2),
            isRead = true,
            category = "Community Event",
            priority = "Normal",
            referenceId = "evt_1"
        )
    )

    val emergencyReports = listOf(
        EmergencyReport(
            id = "emg_001",
            type = EmergencyType.RESCUE_DISASTER,
            description = "High coastal waves reaching beachfront perimeter in Purok 1. Sandbags requested.",
            residentName = "Ramon G. Bautista",
            residentContact = "+63 920 334 1122",
            residentUid = "res_2",
            latitude = 10.3235,
            longitude = 124.9782,
            locationDescription = "Purok 1 Beachfront near fisherman wharf, Barangay Sua",
            timestamp = System.currentTimeMillis() - 1800000L,
            status = EmergencyStatus.RESPONDING,
            assignedResponder = "Tanod Team Alpha & MDRRMO Auxiliary",
            responseNotes = "Dispatched 5 tanods with emergency sandbags and barrier ties."
        ),
        EmergencyReport(
            id = "emg_002",
            type = EmergencyType.MEDICAL,
            description = "Elderly resident experiencing dizziness and high blood pressure spike.",
            residentName = "Gerald C. Santos",
            residentContact = "+63 919 778 9900",
            residentUid = "res_3",
            latitude = 10.3250,
            longitude = 124.9795,
            locationDescription = "Purok 3 Hillside, Barangay Sua",
            timestamp = System.currentTimeMillis() - 7200000L,
            status = EmergencyStatus.RESOLVED,
            assignedResponder = "BHW Lilibeth Flores & San Juan Ambulance",
            responseNotes = "Patient stabilized by BHW; blood pressure brought under control. Medicine provided."
        )
    )

    val auditLogs = listOf(
        AuditLog("log_001", "staff_castillo", "Ms. Arlene P. Castillo", "Barangay Staff", "STATUS_CHANGE", "DocumentRequest", "BRG-SUA-2026-000101", "PROCESSING", "READY", System.currentTimeMillis() - 3600000L),
        AuditLog("log_002", "off_mendoza", "Hon. Roberto Mendoza", "Barangay Official", "PUBLISH_ANNOUNCEMENT", "Announcement", "ann_1", null, "PUBLISHED", System.currentTimeMillis() - (86400000L * 2)),
        AuditLog("log_003", "tanod_santos", "Mr. Victorino B. Santos", "Barangay Official", "DISPATCH_EMERGENCY", "EmergencyReport", "emg_001", "RECEIVED", "RESPONDING", System.currentTimeMillis() - 1500000L),
        AuditLog("log_004", "admin_sys", "Administrator", "System Administrator", "UPDATE_CONFIG", "ServiceConfig", "srv_clearance", "FEE_40", "FEE_50", System.currentTimeMillis() - (86400000L * 5))
    )
}
