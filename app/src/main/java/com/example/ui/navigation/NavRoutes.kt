package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")

    // Main 5 Bottom Bar Destinations
    object Home : Screen("home")
    object Services : Screen("services")
    object Sua : Screen("sua")
    object Alerts : Screen("alerts")
    object Profile : Screen("profile")

    // Resident Sub-Screens
    data class ServiceDetails(val serviceId: String) : Screen("service_details/$serviceId")
    data class RequestForm(val serviceId: String) : Screen("request_form/$serviceId")
    data class RequestConfirmation(val referenceNumber: String) : Screen("request_confirmation/$referenceNumber")
    object MyRequests : Screen("my_requests")
    data class RequestDetails(val requestId: String) : Screen("request_details/$requestId")
    data class AnnouncementDetails(val announcementId: String) : Screen("announcement_details/$announcementId")
    data class EventDetails(val eventId: String) : Screen("event_details/$eventId")
    object Emergency : Screen("emergency")
    object Notifications : Screen("notifications")
    object Settings : Screen("settings")
    object Assistant : Screen("assistant")

    // Administrative Portal
    object AdminDashboard : Screen("admin_dashboard")
    object AdminRequests : Screen("admin_requests")
    object AdminResidents : Screen("admin_residents")
    object AdminHouseholds : Screen("admin_households")
    object AdminAnnouncements : Screen("admin_announcements")
    object AdminEvents : Screen("admin_events")
    object AdminEmergencies : Screen("admin_emergencies")
    object AdminReports : Screen("admin_reports")
    object AdminAuditLogs : Screen("admin_audit_logs")
}
