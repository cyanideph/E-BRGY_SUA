package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.data.BarangayRepository
import com.example.data.local.AppDatabase
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.EBarangaySuaTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ComprehensiveAppTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testWholeAppRenders() {
        composeTestRule.setContent {
            EBarangaySuaTheme {
                EBarangaySuaApp()
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun testSplashScreenRenders() {
        var finished = false
        composeTestRule.setContent {
            EBarangaySuaTheme {
                SplashScreen(onSplashFinished = { finished = true })
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun testOnboardingScreenRenders() {
        var finished = false
        composeTestRule.setContent {
            EBarangaySuaTheme {
                OnboardingScreen(onFinishOnboarding = { finished = true })
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun testLoginScreenRenders() {
        composeTestRule.setContent {
            EBarangaySuaTheme {
                LoginScreen(onLoginSuccess = {}, onNavigateToRegister = {})
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun testRegisterScreenRenders() {
        composeTestRule.setContent {
            EBarangaySuaTheme {
                RegisterScreen(onRegisterSuccess = {}, onNavigateToLogin = {})
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun testHomeScreenRenders() {
        composeTestRule.setContent {
            EBarangaySuaTheme {
                HomeScreen(
                    onNavigateToServices = {},
                    onNavigateToServiceDetails = {},
                    onNavigateToEmergency = {},
                    onNavigateToMyRequests = {},
                    onNavigateToRequestDetails = {},
                    onNavigateToAnnouncements = {},
                    onNavigateToAnnouncementDetails = {},
                    onNavigateToEvents = {},
                    onNavigateToEventDetails = {},
                    onNavigateToAssistant = {},
                    onNavigateToAdminDashboard = {}
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun testServicesScreenRenders() {
        composeTestRule.setContent {
            EBarangaySuaTheme {
                ServicesScreen(onNavigateToServiceDetails = {})
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun testEmergencyScreenRenders() {
        composeTestRule.setContent {
            EBarangaySuaTheme {
                EmergencyScreen(
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun testCommunityHubScreenRenders() {
        composeTestRule.setContent {
            EBarangaySuaTheme {
                SuaCommunityHubScreen(
                    onNavigateToEmergency = {},
                    onNavigateToAnnouncements = {}
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun testProfileScreenRenders() {
        composeTestRule.setContent {
            EBarangaySuaTheme {
                ProfileScreen(
                    onNavigateToSettings = {},
                    onNavigateToAdminDashboard = {},
                    onLogout = {}
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun testSettingsScreenRenders() {
        composeTestRule.setContent {
            EBarangaySuaTheme {
                SettingsScreen(
                    onNavigateBack = {},
                    onLogout = {}
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun testAssistantScreenRenders() {
        composeTestRule.setContent {
            EBarangaySuaTheme {
                AssistantScreen(
                    onNavigateBack = {}
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun testDatabaseOperations() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getInstance(context)
        assertNotNull(db.documentRequestDao())
        assertNotNull(db.emergencyReportDao())
        assertNotNull(db.notificationDao())
        assertNotNull(db.auditLogDao())
    }
}
