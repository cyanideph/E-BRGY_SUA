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
    object Events : Screen("events")
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

/**
 * Every [Screen] that can be stored in a plain string list (back-stack mirror,
 * per-tab stacks). Only argument-less destinations belong here — the six
 * argument routes are rebuilt by [decodeRoute] from their prefix.
 */
private val plainScreens: List<Screen> = listOf(
    Screen.Splash,
    Screen.Onboarding,
    Screen.Login,
    Screen.Register,
    Screen.Home,
    Screen.Services,
    Screen.Sua,
    Screen.Alerts,
    Screen.Profile,
    Screen.MyRequests,
    Screen.Emergency,
    Screen.Notifications,
    Screen.Settings,
    Screen.Assistant,
    Screen.Events,
    Screen.AdminDashboard,
    Screen.AdminRequests,
    Screen.AdminResidents,
    Screen.AdminHouseholds,
    Screen.AdminAnnouncements,
    Screen.AdminEvents,
    Screen.AdminEmergencies,
    Screen.AdminReports,
    Screen.AdminAuditLogs
)

/**
 * Stable string form of a [Screen] so navigation history can be persisted in a
 * Bundle/`rememberSaveable` list and restored after process death (P0-8).
 */
fun Screen.encode(): String = when (this) {
    is Screen.ServiceDetails -> "service_details/$serviceId"
    is Screen.RequestForm -> "request_form/$serviceId"
    is Screen.RequestConfirmation -> "request_confirmation/$referenceNumber"
    is Screen.RequestDetails -> "request_details/$requestId"
    is Screen.AnnouncementDetails -> "announcement_details/$announcementId"
    is Screen.EventDetails -> "event_details/$eventId"
    else -> route
}

/**
 * Inverse of [encode]. Unknown/legacy routes fall back to [Screen.Home] so a
 * stale persisted stack can never leave the app on a broken destination.
 */
fun decodeRoute(r: String): Screen = when {
    r.startsWith("service_details/") -> Screen.ServiceDetails(r.removePrefix("service_details/"))
    r.startsWith("request_form/") -> Screen.RequestForm(r.removePrefix("request_form/"))
    r.startsWith("request_confirmation/") -> Screen.RequestConfirmation(r.removePrefix("request_confirmation/"))
    r.startsWith("request_details/") -> Screen.RequestDetails(r.removePrefix("request_details/"))
    r.startsWith("announcement_details/") -> Screen.AnnouncementDetails(r.removePrefix("announcement_details/"))
    r.startsWith("event_details/") -> Screen.EventDetails(r.removePrefix("event_details/"))
    else -> plainScreens.firstOrNull { it.route == r } ?: Screen.Home
}
