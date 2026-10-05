package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.services.Appwrite.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            val repository = remember { com.example.data.BarangayRepository.instance }
            val themeMode by repository.themeMode.collectAsState()
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val isDark = when (themeMode) {
                com.example.data.AppThemeMode.LIGHT -> false
                com.example.data.AppThemeMode.DARK -> true
                com.example.data.AppThemeMode.SYSTEM -> systemDark
            }

            MyApplicationTheme(darkTheme = isDark) {
                EBarangaySuaApp()
            }
        }
    }
}

@Composable
fun EBarangaySuaApp() {
    val repository = remember { com.example.data.BarangayRepository.instance }
    val scope = rememberCoroutineScope()
    val backStack = remember { mutableStateListOf<Screen>(Screen.Splash) }
    val currentScreen = backStack.lastOrNull() ?: Screen.Home

    fun navigateTo(screen: Screen) {
        backStack.add(screen)
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
        }
    }

    fun replaceTop(screen: Screen) {
        if (backStack.isNotEmpty()) {
            backStack.removeAt(backStack.size - 1)
        }
        backStack.add(screen)
    }

    fun selectBottomTab(screen: Screen) {
        // Keep bottom destination at root
        backStack.clear()
        backStack.add(screen)
    }

    // Android System Back Handling
    BackHandler(enabled = backStack.size > 1) {
        navigateBack()
    }

    val isBottomBarVisible = when (currentScreen) {
        Screen.Home, Screen.Services, Screen.Sua, Screen.Alerts, Screen.Profile -> true
        else -> false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    val items = listOf(
                        Triple(Screen.Home, "Home", Icons.Default.Home),
                        Triple(Screen.Services, "Services", Icons.Default.Widgets),
                        Triple(Screen.Sua, "Sua", Icons.Default.Waves),
                        Triple(Screen.Alerts, "Alerts", Icons.Default.Campaign),
                        Triple(Screen.Profile, "Profile", Icons.Default.Person)
                    )

                    items.forEach { (tabScreen, label, icon) ->
                        val isSelected = currentScreen == tabScreen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectBottomTab(tabScreen) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) DeepOceanBlue else DeepNavyMuted,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) DeepOceanBlue else DeepNavyMuted
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = if (tabScreen == Screen.Sua) SouthernSeaTealContainer else DeepOceanContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    is Screen.Splash -> {
                        SplashScreen(
                            onSplashFinished = {
                                replaceTop(Screen.Onboarding)
                            }
                        )
                    }
                    is Screen.Onboarding -> {
                        OnboardingScreen(
                            onFinishOnboarding = {
                                replaceTop(Screen.Login)
                            }
                        )
                    }
                    is Screen.Login -> {
                        LoginScreen(
                            onLoginSuccess = {
                                selectBottomTab(Screen.Home)
                            },
                            onNavigateToRegister = {
                                navigateTo(Screen.Register)
                            }
                        )
                    }
                    is Screen.Register -> {
                        RegisterScreen(
                            onRegisterSuccess = {
                                selectBottomTab(Screen.Home)
                            },
                            onNavigateToLogin = {
                                navigateBack()
                            }
                        )
                    }
                    is Screen.Home -> {
                        HomeScreen(
                            onNavigateToServices = { selectBottomTab(Screen.Services) },
                            onNavigateToServiceDetails = { sId -> navigateTo(Screen.ServiceDetails(sId)) },
                            onNavigateToEmergency = { navigateTo(Screen.Emergency) },
                            onNavigateToMyRequests = { navigateTo(Screen.MyRequests) },
                            onNavigateToRequestDetails = { rId -> navigateTo(Screen.RequestDetails(rId)) },
                            onNavigateToAnnouncements = { selectBottomTab(Screen.Alerts) },
                            onNavigateToAnnouncementDetails = { aId -> navigateTo(Screen.AnnouncementDetails(aId)) },
                            onNavigateToEvents = { selectBottomTab(Screen.Alerts) },
                            onNavigateToEventDetails = { eId -> navigateTo(Screen.EventDetails(eId)) },
                            onNavigateToAssistant = { navigateTo(Screen.Assistant) },
                            onNavigateToAdminDashboard = { navigateTo(Screen.AdminDashboard) }
                        )
                    }
                    is Screen.Services -> {
                        ServicesScreen(
                            onNavigateToServiceDetails = { sId ->
                                navigateTo(Screen.ServiceDetails(sId))
                            }
                        )
                    }
                    is Screen.ServiceDetails -> {
                        ServiceDetailsScreen(
                            serviceId = screen.serviceId,
                            onNavigateBack = { navigateBack() },
                            onNavigateToRequestForm = { sId ->
                                navigateTo(Screen.RequestForm(sId))
                            }
                        )
                    }
                    is Screen.RequestForm -> {
                        RequestFormScreen(
                            serviceId = screen.serviceId,
                            onNavigateBack = { navigateBack() },
                            onRequestSubmitted = { refNum ->
                                replaceTop(Screen.RequestConfirmation(refNum))
                            }
                        )
                    }
                    is Screen.RequestConfirmation -> {
                        RequestConfirmationScreen(
                            referenceNumber = screen.referenceNumber,
                            onNavigateToHome = { selectBottomTab(Screen.Home) },
                            onNavigateToRequestDetails = { rId ->
                                replaceTop(Screen.RequestDetails(rId))
                            }
                        )
                    }
                    is Screen.MyRequests -> {
                        MyRequestsScreen(
                            onNavigateToRequestDetails = { rId ->
                                navigateTo(Screen.RequestDetails(rId))
                            },
                            onNavigateToServices = {
                                selectBottomTab(Screen.Services)
                            }
                        )
                    }
                    is Screen.RequestDetails -> {
                        RequestDetailsScreen(
                            requestId = screen.requestId,
                            onNavigateBack = { navigateBack() }
                        )
                    }
                    is Screen.Sua -> {
                        SuaCommunityHubScreen(
                            onNavigateToEmergency = { navigateTo(Screen.Emergency) },
                            onNavigateToAnnouncements = { selectBottomTab(Screen.Alerts) }
                        )
                    }
                    is Screen.Alerts -> {
                        AnnouncementsScreen(
                            onNavigateToAnnouncementDetails = { aId ->
                                navigateTo(Screen.AnnouncementDetails(aId))
                            }
                        )
                    }
                    is Screen.AnnouncementDetails -> {
                        AnnouncementDetailsScreen(
                            announcementId = screen.announcementId,
                            onNavigateBack = { navigateBack() }
                        )
                    }
                    is Screen.EventDetails -> {
                        EventDetailsScreen(
                            eventId = screen.eventId,
                            onNavigateBack = { navigateBack() }
                        )
                    }
                    is Screen.Emergency -> {
                        EmergencyScreen(
                            onNavigateBack = { navigateBack() }
                        )
                    }
                    is Screen.Notifications -> {
                        NotificationsScreen(
                            onNavigateToRequestDetails = { rId ->
                                navigateTo(Screen.RequestDetails(rId))
                            }
                        )
                    }
                    is Screen.Profile -> {
                        ProfileScreen(
                            onNavigateToSettings = { navigateTo(Screen.Settings) },
                            onNavigateToAdminDashboard = { navigateTo(Screen.AdminDashboard) },
                            onLogout = { scope.launch { repository.logout(); selectBottomTab(Screen.Login) } }
                        )
                    }
                    is Screen.Settings -> {
                        SettingsScreen(
                            onNavigateBack = { navigateBack() },
                            onLogout = { selectBottomTab(Screen.Login) }
                        )
                    }
                    is Screen.Assistant -> {
                        AssistantScreen(
                            onNavigateBack = { navigateBack() }
                        )
                    }
                    is Screen.AdminDashboard -> {
                        AdminDashboardScreen(
                            onNavigateBack = { navigateBack() },
                            onNavigateToRequests = { navigateTo(Screen.AdminRequests) },
                            onNavigateToResidents = { navigateTo(Screen.AdminResidents) },
                            onNavigateToHouseholds = { navigateTo(Screen.AdminHouseholds) },
                            onNavigateToAnnouncements = { navigateTo(Screen.AdminAnnouncements) },
                            onNavigateToEvents = { navigateTo(Screen.AdminEvents) },
                            onNavigateToEmergencies = { navigateTo(Screen.AdminEmergencies) },
                            onNavigateToReports = { navigateTo(Screen.AdminReports) },
                            onNavigateToAuditLogs = { navigateTo(Screen.AdminAuditLogs) }
                        )
                    }
                    is Screen.AdminRequests -> {
                        AdminRequestsScreen(
                            onNavigateBack = { navigateBack() },
                            onNavigateToRequestDetails = { rId ->
                                navigateTo(Screen.RequestDetails(rId))
                            }
                        )
                    }
                    is Screen.AdminResidents -> {
                        AdminResidentsScreen(
                            onNavigateBack = { navigateBack() }
                        )
                    }
                    is Screen.AdminHouseholds -> {
                        AdminHouseholdsScreen(
                            onNavigateBack = { navigateBack() }
                        )
                    }
                    is Screen.AdminAnnouncements -> {
                        AdminAnnouncementsScreen(
                            onNavigateBack = { navigateBack() }
                        )
                    }
                    is Screen.AdminEvents -> {
                        AdminEventsScreen(
                            onNavigateBack = { navigateBack() }
                        )
                    }
                    is Screen.AdminEmergencies -> {
                        AdminEmergenciesScreen(
                            onNavigateBack = { navigateBack() }
                        )
                    }
                    is Screen.AdminReports -> {
                        AdminReportsScreen(
                            onNavigateBack = { navigateBack() }
                        )
                    }
                    is Screen.AdminAuditLogs -> {
                        AdminAuditLogsScreen(
                            onNavigateBack = { navigateBack() }
                        )
                    }
                }
            }
        }
    }
}
