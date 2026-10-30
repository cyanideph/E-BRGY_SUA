package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.navigation.decodeRoute
import com.example.ui.navigation.encode
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runCatching {
            val previous = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler(CrashHandler(application, previous))
        }

        // Backend, notification, and local-cache initialization must not prevent
        // the Compose UI from opening. Keep each failure visible in logcat so
        // startup problems can be diagnosed instead of silently swallowing them.
        runCatching {
            com.example.services.Appwrite.init(applicationContext)
        }.onFailure { error ->
            android.util.Log.e("EBargSuaStartup", "Appwrite client initialization failed", error)
        }

        runCatching {
            com.example.services.NotificationHelper.initChannels(applicationContext)
        }.onFailure { error ->
            android.util.Log.e("EBargSuaStartup", "Notification channel initialization failed", error)
        }

        runCatching {
            com.example.data.BarangayRepository.instance.initLocalDb(applicationContext)
        }.onFailure { error ->
            android.util.Log.e("EBargSuaStartup", "Local database initialization failed", error)
        }

        enableEdgeToEdge()
        setContent {
            // ViewModels — each owns its own UI-state; Repository remains for backend ops
            val authViewModel: AuthViewModel = viewModel()
            val requestsViewModel: RequestsViewModel = viewModel()
            val emergencyViewModel: EmergencyViewModel = viewModel()
            val civicViewModel: CivicViewModel = viewModel()

            val themeMode = authViewModel.themeMode
            val isDark = when (themeMode) {
                com.example.data.AppThemeMode.LIGHT -> false
                com.example.data.AppThemeMode.DARK -> true
                com.example.data.AppThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDark, dynamicColor = true) {
                EBarangaySuaApp(authViewModel, requestsViewModel, emergencyViewModel, civicViewModel)
            }
        }
    }
}

@Composable
fun EBarangaySuaApp(authViewModel: AuthViewModel,
                      requestsViewModel: RequestsViewModel,
                      emergencyViewModel: EmergencyViewModel,
                      civicViewModel: CivicViewModel) {
    // P0-8: persisted mirror of the back stack (encoded routes) so navigation
    // history survives process death. A plain ArrayList is Bundle/Serializable
    // safe, which `rememberSaveable` requires.
    val savedStack = rememberSaveable { arrayListOf<String>() }
    val backStack = remember { mutableStateListOf<Screen>(Screen.Splash) }
    // Restore persisted history exactly once, before the first frame renders.
    remember {
        if (savedStack.isNotEmpty() && backStack.size == 1 && backStack.lastOrNull() == Screen.Splash) {
            val restored = savedStack.map { decodeRoute(it) }
            backStack.clear()
            backStack.addAll(restored)
        }
    }
    val currentScreen = backStack.lastOrNull() ?: Screen.Home

    // P0-8: per-tab navigation history so switching bottom tabs keeps each
    // destination's own stack instead of wiping it.
    var currentTab by rememberSaveable { mutableStateOf(Screen.Home.route) }
    val tabStacks = remember { mutableStateMapOf<String, List<String>>() }
    val bottomTabRoutes = listOf(
        Screen.Home.route,
        Screen.Services.route,
        Screen.Sua.route,
        Screen.Alerts.route,
        Screen.Profile.route
    )

    fun navigateTo(screen: Screen) {
        // TODO(P1): slide transitions — persistence is the P0, motion is the P1.
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
        if (screen.route in bottomTabRoutes) {
            // Commit the outgoing tab's stack only when it really belongs to the
            // active tab (guards against the Login/Onboarding flow overwriting it).
            if (backStack.firstOrNull()?.route == currentTab) {
                tabStacks[currentTab] = backStack.map { it.encode() }
            }
            currentTab = screen.route
            backStack.clear()
            val saved = tabStacks[screen.route]?.takeIf { it.firstOrNull() == screen.encode() }
            (saved ?: listOf(screen.encode())).forEach { backStack.add(decodeRoute(it)) }
        } else {
            // Non-tab roots (e.g. Login) start a fresh session — drop stale stacks.
            tabStacks.clear()
            currentTab = screen.route
            backStack.clear()
            backStack.add(screen)
        }
    }

    // Keep the persisted mirror in step with the live stack (P0-8).
    LaunchedEffect(backStack.size, backStack.lastOrNull()) {
        savedStack.clear()
        savedStack.addAll(backStack.map { it.encode() })
    }

    // Android System Back Handling
    BackHandler(enabled = backStack.size > 1) {
        navigateBack()
    }

    val isBottomBarVisible = when (currentScreen) {
        Screen.Home, Screen.Services, Screen.Sua, Screen.Alerts, Screen.Profile -> true
        else -> false
    }

    val themeMode = authViewModel.themeMode
    val isDark = when (themeMode) {
        com.example.data.AppThemeMode.LIGHT -> false
        com.example.data.AppThemeMode.DARK -> true
        com.example.data.AppThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    val bgRes = if (isDark) R.drawable.background_dark else R.drawable.background_light

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
                        Triple(Screen.Alerts, "Alerts", Icons.Default.Notifications),
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
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = if (tabScreen == Screen.Sua) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
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
            if (currentScreen in listOf(Screen.Home, Screen.Services, Screen.Profile, Screen.Sua, Screen.Alerts)) {
                Image(
                    painter = painterResource(id = bgRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = if (isDark) 0.12f else 0.07f
                )
            }

            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    is Screen.Splash -> {
                        SplashScreen(
                            onSplashFinished = {
                                val currentUser = authViewModel.currentUser.value
                                val isLoggedIn = currentUser.uid.isNotBlank()
                                val hasOnboarded = authViewModel.hasCompletedOnboarding.value
                                when {
                                    isLoggedIn -> selectBottomTab(Screen.Home)
                                    hasOnboarded -> replaceTop(Screen.Login)
                                    else -> replaceTop(Screen.Onboarding)
                                }
                            }
                        )
                    }
                    is Screen.Onboarding -> {
                        OnboardingScreen(
                            onFinishOnboarding = {
                                authViewModel.hasCompletedOnboarding.value
                                if (authViewModel.currentUser.value.uid.isNotBlank()) {
                                    selectBottomTab(Screen.Home)
                                } else {
                                    replaceTop(Screen.Login)
                                }
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
                            onNavigateToEvents = { navigateTo(Screen.Events) },
                            onNavigateToEventDetails = { eId -> navigateTo(Screen.EventDetails(eId)) },
                            onNavigateToNotifications = { navigateTo(Screen.Notifications) },
                            onNavigateToAssistant = { navigateTo(Screen.Assistant) },
                            onNavigateToAdminDashboard = { navigateTo(Screen.AdminDashboard) },
                            screenColorScheme = null
                        )
                    }
                    is Screen.Services -> {
                        ServicesScreen(
                            onNavigateToServiceDetails = { sId ->
                                navigateTo(Screen.ServiceDetails(sId))
                            },
                            screenColorScheme = null
                        )
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
                            },
                            screenColorScheme = null
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
                            onNavigateToAnnouncements = { selectBottomTab(Screen.Alerts) },
                            screenColorScheme = null
                        )
                    }
                    is Screen.Alerts -> {
                        AnnouncementsScreen(
                            onNavigateToAnnouncementDetails = { aId ->
                                navigateTo(Screen.AnnouncementDetails(aId))
                            },
                            screenColorScheme = null
                        )
                    }
                    is Screen.Events -> {
                        EventsScreen(
                            onNavigateToEventDetails = { navigateTo(Screen.EventDetails(it)) },
                            screenColorScheme = null
                        )
                    }
                    is Screen.AnnouncementDetails -> {
                        AnnouncementDetailsScreen(
                            announcementId = screen.announcementId,
                            onNavigateBack = { navigateBack() },
                            screenColorScheme = null
                        )
                    }
                    is Screen.EventDetails -> {
                        EventDetailsScreen(
                            eventId = screen.eventId,
                            onNavigateBack = { navigateBack() },
                            screenColorScheme = null
                        )
                    }
                    is Screen.Emergency -> {
                        EmergencyScreen(
                            onNavigateBack = { navigateBack() },
                            screenColorScheme = null
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
                            onLogout = { scope.launch { authViewModel.logout(); selectBottomTab(Screen.Login) } }
                        )
                    }
                    is Screen.Settings -> {
                        SettingsScreen(
                            onNavigateBack = { navigateBack() },
                            onLogout = { scope.launch { authViewModel.logout(); selectBottomTab(Screen.Login) } }
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
                            screenColorScheme = null
                        )                            onNavigateToAnnouncements = { navigateTo(Screen.AdminAnnouncements) },
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
                            },
                            screenColorScheme = null
                        )
                    }
                    is Screen.AdminResidents -> {
                        AdminResidentsScreen(
                            onNavigateBack = { navigateBack() },
                            screenColorScheme = null
                    }
                    is Screen.AdminHouseholds -> {
                        AdminHouseholdsScreen(
                            onNavigateBack = { navigateBack() },
                            screenColorScheme = null
                    }
                    is Screen.AdminAnnouncements -> {
                        AdminAnnouncementsScreen(
                            onNavigateBack = { navigateBack() },
                            screenColorScheme = null
                    }
                    is Screen.AdminEvents -> {
                        AdminEventsScreen(
                            onNavigateBack = { navigateBack() },
                            screenColorScheme = null
                    }
                    is Screen.AdminEmergencies -> {
                        AdminEmergenciesScreen(
                            onNavigateBack = { navigateBack() },
                            screenColorScheme = null
                    }
                    is Screen.AdminReports -> {
                        AdminReportsScreen(
                            onNavigateBack = { navigateBack() },
                            screenColorScheme = null
                    }
                    is Screen.AdminAuditLogs -> {
                        AdminAuditLogsScreen(
                            onNavigateBack = { navigateBack() },
                            screenColorScheme = null
                    }
                }
            }
        }
    }
}
