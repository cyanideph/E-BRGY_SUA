package com.example.data

import com.example.model.*

/**
 * Standard civic records and directory for Barangay Sua, San Juan, Southern Leyte.
 * Provides offline-first baseline data when the Appwrite backend is unconfigured or offline.
 */
object DefaultBarangayData {

    val services = listOf(
        BarangayService(
            id = "srv_clearance",
            name = "Barangay Clearance",
            category = "Clearance & Certification",
            description = "Official certification issued for employment, police clearance, postal ID, bank transactions, and legal identification within San Juan.",
            purposeExamples = listOf("Local Employment", "Bank Account Opening", "Police Clearance Application", "Postal ID Application"),
            requirements = listOf(
                ServiceRequirement("Valid Government-issued ID", "Photo identification (e.g., PhilSys, Voter's ID, Driver's License, SSS)", true),
                ServiceRequirement("Cedula (Community Tax Certificate)", "Current year Community Tax Certificate from San Juan Municipal Hall or Barangay Office", true),
                ServiceRequirement("Purok Clearance Endorsement", "Endorsement from respective Purok leader confirming current residency", true)
            ),
            processingDays = "Same Day (1-2 Hours)",
            feeDescription = "₱50.00 (Waived for First-Time Jobseekers under RA 11261)",
            iconKey = "verified"
        ),
        BarangayService(
            id = "srv_residency",
            name = "Certificate of Residency",
            category = "Clearance & Certification",
            description = "Document affirming that the applicant is a bonafide permanent or temporary resident of Barangay Sua for at least six (6) months.",
            purposeExamples = listOf("School Enrollment", "Government Assistance Application", "Court Proceedings", "Utility Connection"),
            requirements = listOf(
                ServiceRequirement("Valid Government ID", "Present any government ID reflecting current address", true),
                ServiceRequirement("Household Verification", "Verification in the Barangay Sua Household Registry", true)
            ),
            processingDays = "Same Day (Within 1 Hour)",
            feeDescription = "₱50.00 (Standard)",
            iconKey = "home"
        ),
        BarangayService(
            id = "srv_indigency",
            name = "Certificate of Indigency",
            category = "Financial & Social Assistance",
            description = "Official certification provided to low-income residents for medical, educational, burial, or legal financial assistance.",
            purposeExamples = listOf("Hospital Bill Assistance (DSWD/MAIP)", "Public Attorney's Office (PAO) Legal Representation", "Scholarship Grants", "Funeral Aid"),
            requirements = listOf(
                ServiceRequirement("Barangay Social Welfare Evaluation", "Brief interview or endorsement by the Barangay Nutrition Scholar / Health Worker", true),
                ServiceRequirement("Valid ID or Purok Endorsement", "Proof of identity and residency in Purok 1 or Purok 2", true)
            ),
            processingDays = "Immediate Release",
            feeDescription = "Free of Charge (₱0.00)",
            iconKey = "handshake"
        ),
        BarangayService(
            id = "srv_business_clearance",
            name = "Barangay Business Clearance",
            category = "Business & Trade",
            description = "Required permit clearance for commercial establishments, sari-sari stores, fish vendors, and home businesses operating within Barangay Sua.",
            purposeExamples = listOf("Mayor's Permit Renewal", "DTI / SEC Business Registration", "Microfinance Application"),
            requirements = listOf(
                ServiceRequirement("DTI / SEC Certificate", "Official registration document of the business entity", true),
                ServiceRequirement("Previous Year Barangay Clearance", "Required only for permit renewal", false),
                ServiceRequirement("Sanitary and Environmental Clearance", "Inspection checklist compliance for food and retail kiosks", true)
            ),
            processingDays = "1-2 Business Days",
            feeDescription = "₱150.00 (Micro) / ₱300.00 (Commercial)",
            iconKey = "store"
        ),
        BarangayService(
            id = "srv_bpo_protection",
            name = "Barangay Protection Order (BPO) / Tanod Assistance",
            category = "Justice & Public Safety",
            description = "Emergency protection order issued by the Punong Barangay under RA 9262 (Anti-VAWC) to protect victims of domestic violence.",
            purposeExamples = listOf("Domestic Violence Protection", "Immediate Tanod Neighborhood Escort", "Mediation / Lupon Tagapamayapa Notice"),
            requirements = listOf(
                ServiceRequirement("Sworn Affidavit / Incident Blotter", "Barangay Tanod incident blotter statement or narrative incident report", true),
                ServiceRequirement("Valid Identification", "Any proof of identity or neighbor verification", true)
            ),
            processingDays = "Immediate (Within 24 Hours Mandatory)",
            feeDescription = "Free of Charge (₱0.00)",
            iconKey = "shield"
        ),
        BarangayService(
            id = "srv_first_time_jobseeker",
            name = "First-Time Jobseeker Certificate (RA 11261)",
            category = "Youth & Employment",
            description = "Statutory exemption certificate granting first-time jobseekers free government documentation (police clearance, medical cert, clearances).",
            purposeExamples = listOf("First-time Employment Application", "Civil Service Commission Examinations", "TESDA National Certificate Applications"),
            requirements = listOf(
                ServiceRequirement("Signed Oath of Undertaking", "Available and notarized at Barangay Secretary desk", true),
                ServiceRequirement("Barangay Residency Certification", "At least 6 months continuous residence in Barangay Sua", true)
            ),
            processingDays = "Same Day",
            feeDescription = "100% Free under Republic Act No. 11261",
            iconKey = "description"
        )
    )

    val announcements = listOf(
        Announcement(
            id = "ann_1",
            title = "Coastal Clean-up Drive & Marine Sanctuary Stewardship",
            description = "All Purok residents, youth leaders, and fisherfolk associations are invited to participate in our coastal clearing and mangrove planting activity at the Sua Beachfront this Saturday at 6:00 AM.",
            category = AnnouncementCategory.COMMUNITY,
            priority = AnnouncementPriority.IMPORTANT,
            publishedDate = "April 4, 2026",
            authorName = "Hon. Roberto D. Alaba",
            authorRole = "Punong Barangay",
            isPinned = true
        ),
        Announcement(
            id = "ann_2",
            title = "Southwest Monsoon Weather & Coastal Gale Warning",
            description = "San Juan MDRRMO and PAGASA report moderate to rough coastal waters off Sogod Bay. Small sea vessels and local fishermen are strongly advised not to venture out to sea until further advisory.",
            category = AnnouncementCategory.DISASTER,
            priority = AnnouncementPriority.EMERGENCY,
            publishedDate = "April 3, 2026",
            authorName = "Barangay Tanod Emergency Unit",
            authorRole = "Disaster Preparedness Committee",
            isPinned = true
        ),
        Announcement(
            id = "ann_3",
            title = "Mobile Health Check-Up & Free Pediatric Medicine Distribution",
            description = "The Municipal Health Office of San Juan and Barangay Sua Health Center will conduct comprehensive immunization, prenatal checkups, and free vitamins distribution on Monday, 8:00 AM to 3:00 PM.",
            category = AnnouncementCategory.HEALTH,
            priority = AnnouncementPriority.NORMAL,
            publishedDate = "April 1, 2026",
            authorName = "Maria Elena Cruz, RN",
            authorRole = "Barangay Health Center Head",
            isPinned = false
        ),
        Announcement(
            id = "ann_4",
            title = "Barangay Sua General Assembly & 2026 Annual Budget Report",
            description = "Notice is hereby given to all registered voting residents for the upcoming 1st Semester Barangay Assembly to discuss infrastructure updates, IRA allocations, and local youth programs.",
            category = AnnouncementCategory.GENERAL,
            priority = AnnouncementPriority.IMPORTANT,
            publishedDate = "March 28, 2026",
            authorName = "Liza M. Fernandez",
            authorRole = "Barangay Secretary",
            isPinned = false
        )
    )

    val events = listOf(
        BarangayEvent(
            id = "evt_1",
            title = "Barangay Sua General Assembly Meeting",
            description = "Community assembly covering barangay accomplishments, financial stewardship reports, and open forum with the Sangguniang Barangay.",
            date = "April 18, 2026",
            time = "09:00 AM",
            location = "Barangay Sua Multipurpose Gymnasium",
            organizer = "Barangay Council of Sua",
            category = "Assembly",
            rsvpCount = 92,
            isUserRsvpd = true
        ),
        BarangayEvent(
            id = "evt_2",
            title = "Sua Coastal Summer Youth Basketball League Opening",
            description = "Inter-Purok sports tournament aimed at youth engagement, sportsmanship, and drug-prevention awareness.",
            date = "April 25, 2026",
            time = "03:30 PM",
            location = "Barangay Covered Court",
            organizer = "Sangguniang Kabataan (SK) of Sua",
            category = "Sports & Youth",
            rsvpCount = 68,
            isUserRsvpd = false
        ),
        BarangayEvent(
            id = "evt_3",
            title = "Anti-Rabies Animal Vaccination & Tagging Drive",
            description = "Free vaccination for domestic dogs and cats sponsored by the Southern Leyte Provincial Veterinary Office.",
            date = "May 2, 2026",
            time = "08:00 AM",
            location = "Health Center Grounds",
            organizer = "Barangay Health & Agriculture Committee",
            category = "Health",
            rsvpCount = 34,
            isUserRsvpd = false
        )
    )

    val officials = listOf(
        BarangayOfficial(
            id = "off_1",
            name = "Hon. Roberto D. Alaba",
            position = "Punong Barangay (Barangay Captain)",
            roleCategory = "Executive",
            contactNumber = "+63 917 800 7821",
            officeHours = "Mon - Fri: 8:00 AM - 5:00 PM",
            committee = "Committee on Peace and Order / Public Safety",
            isDemoRecord = false,
            email = "punongbarangay.sua@sanjuan.gov.ph"
        ),
        BarangayOfficial(
            id = "off_2",
            name = "Hon. Jocelyn S. Mendez",
            position = "Barangay Kagawad",
            roleCategory = "Council",
            contactNumber = "+63 918 345 6789",
            officeHours = "Tue & Thu: 9:00 AM - 3:00 PM",
            committee = "Committee on Health, Sanitation & Social Welfare",
            isDemoRecord = false,
            email = "jocelyn.mendez@barangaysua.ph"
        ),
        BarangayOfficial(
            id = "off_3",
            name = "Hon. Arnel V. Balaba",
            position = "Barangay Kagawad",
            roleCategory = "Council",
            contactNumber = "+63 917 234 5678",
            officeHours = "Mon & Wed: 8:00 AM - 1:00 PM",
            committee = "Committee on Agriculture, Fisheries & Aquatic Resources",
            isDemoRecord = false,
            email = "arnel.balaba@barangaysua.ph"
        ),
        BarangayOfficial(
            id = "off_4",
            name = "Hon. Corazon L. Dizon",
            position = "Barangay Kagawad",
            roleCategory = "Council",
            contactNumber = "+63 920 456 7890",
            officeHours = "Wed & Fri: 9:00 AM - 2:00 PM",
            committee = "Committee on Education, Culture & Public Information",
            isDemoRecord = false,
            email = "corazon.dizon@barangaysua.ph"
        ),
        BarangayOfficial(
            id = "off_5",
            name = "Hon. Danilo M. Tan",
            position = "Barangay Kagawad",
            roleCategory = "Council",
            contactNumber = "+63 915 678 1234",
            officeHours = "Mon - Thu: 8:00 AM - 12:00 PM",
            committee = "Committee on Infrastructure & Public Works",
            isDemoRecord = false,
            email = "danilo.tan@barangaysua.ph"
        ),
        BarangayOfficial(
            id = "off_6",
            name = "Hon. Marites C. Oporto",
            position = "Barangay Kagawad",
            roleCategory = "Council",
            contactNumber = "+63 919 789 2345",
            officeHours = "Tue & Fri: 10:00 AM - 3:00 PM",
            committee = "Committee on Women, Family & Gender Development",
            isDemoRecord = false,
            email = "marites.oporto@barangaysua.ph"
        ),
        BarangayOfficial(
            id = "off_7",
            name = "Hon. Gabriel P. Reyes",
            position = "SK Chairperson (Youth Council Head)",
            roleCategory = "Council",
            contactNumber = "+63 927 890 3456",
            officeHours = "Sat & Sun: 1:00 PM - 5:00 PM",
            committee = "Committee on Youth & Sports Development",
            isDemoRecord = false,
            email = "sk.gabrielreyes@barangaysua.ph"
        ),
        BarangayOfficial(
            id = "off_8",
            name = "Liza M. Fernandez",
            position = "Barangay Secretary",
            roleCategory = "Administration",
            contactNumber = "+63 916 234 8901",
            officeHours = "Mon - Fri: 8:00 AM - 5:00 PM",
            committee = "Administrative Secretariat & Civil Registry",
            isDemoRecord = false,
            email = "sec.sua@sanjuan.gov.ph"
        ),
        BarangayOfficial(
            id = "off_9",
            name = "Edgardo T. Santos",
            position = "Barangay Treasurer",
            roleCategory = "Administration",
            contactNumber = "+63 918 567 9012",
            officeHours = "Mon - Fri: 8:00 AM - 5:00 PM",
            committee = "Barangay Treasury & Disbursing Office",
            isDemoRecord = false,
            email = "treasurer.sua@sanjuan.gov.ph"
        ),
        BarangayOfficial(
            id = "off_10",
            name = "Rolando K. Navarro",
            position = "Chief Barangay Tanod",
            roleCategory = "Health & Security",
            contactNumber = "+63 917 800 7821",
            officeHours = "24/7 On-Call Barangay Security Watch",
            committee = "Barangay Tanod Brigade & Night Patrol",
            isDemoRecord = false,
            email = "tanod.sua@sanjuan.gov.ph"
        )
    )

    val hotlines = listOf(
        OfficialHotline(
            name = "Barangay Sua Tanod Emergency Dispatch",
            number = "+63 917 800 7821",
            agency = "Barangay Tanod Brigade",
            description = "Immediate village security, neighborhood disputes, and local first response"
        ),
        OfficialHotline(
            name = "San Juan Municipal Police Station",
            number = "+63 998 598 6377",
            agency = "Philippine National Police (PNP)",
            description = "Law enforcement, municipal emergency operations, and emergency police response"
        ),
        OfficialHotline(
            name = "San Juan MDRRMO Disaster Operations",
            number = "+63 920 918 2345",
            agency = "Municipal Disaster Risk Reduction & Management",
            description = "Flooding, coastal typhoons, landslide warnings, and ambulance dispatch"
        ),
        OfficialHotline(
            name = "Bureau of Fire Protection (BFP) San Juan",
            number = "+63 915 234 5678",
            agency = "BFP Southern Leyte",
            description = "Fire emergencies, hazardous material containment, and structural collapse"
        ),
        OfficialHotline(
            name = "Barangay Sua Health Station / RHU",
            number = "+63 939 456 7890",
            agency = "Rural Health Unit",
            description = "Medical emergencies, maternal care, primary clinic, and first aid triage"
        ),
        OfficialHotline(
            name = "Philippine Coast Guard Sub-Station San Juan",
            number = "+63 917 123 4567",
            agency = "Philippine Coast Guard",
            description = "Maritime search & rescue, coastal vessel distress, and rough sea advisories"
        )
    )

    val households = listOf(
        Household(
            id = "hh_1",
            householdNumber = "HH-SUA-0012",
            headName = "Ernesto Santos",
            address = "Purok 1 Coastal Boulevard, Barangay Sua",
            memberCount = 4,
            memberNames = listOf("Ernesto Santos", "Maria Santos", "Elena Santos", "Joshua Santos"),
            emergencyNotes = "Elderly member requiring mobility assistance during flood advisories."
        ),
        Household(
            id = "hh_2",
            householdNumber = "HH-SUA-0028",
            headName = "Ramon Balaba",
            address = "Purok 2 Hillside, Barangay Sua",
            memberCount = 5,
            memberNames = listOf("Ramon Balaba", "Gina Balaba", "Kenneth Balaba", "Chloe Balaba", "Mateo Balaba"),
            emergencyNotes = "Registered fishing family with pump boat registered at Sua Cove."
        ),
        Household(
            id = "hh_3",
            householdNumber = "HH-SUA-0045",
            headName = "Vicente Oporto",
            address = "Purok 1 Main Road, Barangay Sua",
            memberCount = 3,
            memberNames = listOf("Vicente Oporto", "Carmela Oporto", "Angelo Oporto"),
            emergencyNotes = "Near barangay hall evacuation center."
        )
    )

    val initialRequests = listOf(
        DocumentRequest(
            id = "req_101",
            referenceNumber = "BRG-SUA-2026-000121",
            serviceId = "srv_clearance",
            serviceName = "Barangay Clearance",
            residentUid = "res_sua_001",
            residentName = "Elena Santos",
            residentAddress = "Purok 1, Barangay Sua, San Juan, Southern Leyte",
            residentContact = "+63 917 555 0192",
            purpose = "Local Employment Application at San Juan Port",
            deliveryMethod = "Pick-up at Barangay Hall",
            remarks = "Ready with Cedula CTC-2026-9812",
            status = RequestStatus.READY,
            officialRemarks = "Approved by Brgy. Sec. Liza Fernandez. Ready for dry-seal and pickup.",
            attachmentNames = listOf("phil_id_scan.pdf", "cedula_2026.jpg"),
            createdAt = System.currentTimeMillis() - 86400000L * 2,
            updatedAt = System.currentTimeMillis() - 3600000L * 4,
            timeline = listOf(
                RequestTimelineEvent("Application Submitted", "Submitted online via e-Barangay Sua", System.currentTimeMillis() - 86400000L * 2, "Elena Santos"),
                RequestTimelineEvent("Under Review", "Records verified in Barangay Sua Registry", System.currentTimeMillis() - 86400000L, "Liza Fernandez (Secretary)"),
                RequestTimelineEvent("Ready for Release", "Certificate printed, signed, and logged. Please claim at Window 1.", System.currentTimeMillis() - 3600000L * 4, "Edgardo Santos (Treasurer)")
            ),
            isSyncedToServer = true
        ),
        DocumentRequest(
            id = "req_102",
            referenceNumber = "BRG-SUA-2026-000122",
            serviceId = "srv_residency",
            serviceName = "Certificate of Residency",
            residentUid = "res_sua_001",
            residentName = "Elena Santos",
            residentAddress = "Purok 1, Barangay Sua, San Juan, Southern Leyte",
            residentContact = "+63 917 555 0192",
            purpose = "Application for Government Fisherfolk Assistance",
            deliveryMethod = "Pick-up at Barangay Hall",
            remarks = "Continuous resident for 8 years",
            status = RequestStatus.PROCESSING,
            officialRemarks = "Purok 1 leader endorsement verified. Printing certificate.",
            attachmentNames = listOf("resident_affidavit.pdf"),
            createdAt = System.currentTimeMillis() - 86400000L,
            updatedAt = System.currentTimeMillis() - 7200000L,
            timeline = listOf(
                RequestTimelineEvent("Application Submitted", "Submitted online via e-Barangay Sua", System.currentTimeMillis() - 86400000L, "Elena Santos"),
                RequestTimelineEvent("Processing Started", "Purok 1 leader endorsement verified.", System.currentTimeMillis() - 7200000L, "Liza Fernandez (Secretary)")
            ),
            isSyncedToServer = true
        )
    )

    val initialNotifications = listOf(
        BarangayNotification(
            id = "notif_init_1",
            title = "Document Ready: Barangay Clearance",
            message = "Your request (BRG-SUA-2026-000121) is approved and ready for pickup at Barangay Hall Window 1.",
            timestamp = System.currentTimeMillis() - 3600000L * 4,
            isRead = false,
            category = "Service Request",
            priority = "Important",
            referenceId = "BRG-SUA-2026-000121"
        ),
        BarangayNotification(
            id = "notif_init_2",
            title = "Coastal Gale Warning",
            message = "Moderate to rough sea conditions reported off San Juan coast. Fisherfolk advised to take caution.",
            timestamp = System.currentTimeMillis() - 86400000L,
            isRead = true,
            category = "Emergency",
            priority = "Emergency",
            referenceId = "ann_2"
        )
    )
}
