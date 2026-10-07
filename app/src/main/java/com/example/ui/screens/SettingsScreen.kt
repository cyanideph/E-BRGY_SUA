package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.SoftSkeuomorphicCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val repository = remember { com.example.data.BarangayRepository.instance }
    val themeMode by repository.themeMode.collectAsState()
    val scope = rememberCoroutineScope()
    var appwriteStatus by remember { mutableStateOf("Not checked") }
    var appwriteChecking by remember { mutableStateOf(false) }
    val appwriteStatusColor = when {
        appwriteStatus.startsWith("Connected") -> MaterialTheme.colorScheme.primary
        appwriteStatus.startsWith("Connection failed") -> RestrainedCoralRed
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("barangay_sua_prefs", android.content.Context.MODE_PRIVATE) }
    var pushNotifsEnabled by remember { mutableStateOf(prefs.getBoolean("push_notifs", true)) }
    var emergencySmsEnabled by remember { mutableStateOf(prefs.getBoolean("emergency_sms", true)) }
    var biometricsEnabled by remember { mutableStateOf(prefs.getBoolean("biometrics", false)) }

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Quick one-tap theme toggle
                    IconButton(onClick = { repository.toggleTheme() }) {
                        Icon(
                            imageVector = if (themeMode == com.example.data.AppThemeMode.DARK) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Mode Settings
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Display Theme",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Default is Light Mode",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Visual indicator badge
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (themeMode == com.example.data.AppThemeMode.DARK) SouthernSeaTealContainer else DeepOceanContainer
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (themeMode == com.example.data.AppThemeMode.DARK) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (themeMode == com.example.data.AppThemeMode.DARK) SouthernSeaTealDark else DeepOceanBlue
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = themeMode.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (themeMode == com.example.data.AppThemeMode.DARK) SouthernSeaTealDark else DeepOceanBlue
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                com.example.data.AppThemeMode.values().forEach { mode ->
                    val isSelected = (themeMode == mode)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { repository.setThemeMode(mode) },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = mode.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = mode.subtitle,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Appwrite Connection
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Appwrite Connection",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Project: " + com.example.services.Appwrite.PROJECT_ID,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = appwriteStatus,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = appwriteStatusColor,
                    modifier = Modifier.testTag("appwrite_connection_status")
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            appwriteChecking = true
                            appwriteStatus = "Checking Appwrite…"
                            try {
                                val response = com.example.services.Appwrite.ping()
                                appwriteStatus = "Connected • " + response.trim().ifEmpty { "Appwrite responded" }
                            } catch (error: Throwable) {
                                appwriteStatus = "Connection failed • " + (error.message ?: error.javaClass.simpleName)
                            } finally {
                                appwriteChecking = false
                            }
                        }
                    },
                    enabled = !appwriteChecking,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_appwrite_connection")
                ) {
                    if (appwriteChecking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.CloudDone, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (appwriteChecking) "Testing Connection…" else "Test Appwrite Connection",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Notifications Settings
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Notification Preferences",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Document Status Alerts", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Get notified when requests change status", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = pushNotifsEnabled,
                        onCheckedChange = {
                            pushNotifsEnabled = it
                            prefs.edit().putBoolean("push_notifs", it).apply()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("High-Priority Coastal Advisories", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Typhoon, swell, and emergency alerts", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = emergencySmsEnabled,
                        onCheckedChange = {
                            emergencySmsEnabled = it
                            prefs.edit().putBoolean("emergency_sms", it).apply()
                        }
                    )
                }
            }

            // Language Settings
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Language & Locale",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Application Language: English (Official Active Language)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepOceanBlue
                )
                Text(
                    text = "In compliance with platform guidelines, all services, forms, announcements, and AI assistance operate exclusively in English.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Security & Privacy Settings
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Security & Privacy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Biometric Sign-In", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Use fingerprint or face recognition", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = biometricsEnabled,
                        onCheckedChange = {
                            biometricsEnabled = it
                            prefs.edit().putBoolean("biometrics", it).apply()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Data Protection: Barangay Sua enforces strict local privacy under the Data Privacy Act of 2012 (RA 10173). Resident records are accessible only to authorized municipal officers.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showPrivacyDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Policy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Read Data Privacy Policy (RA 10173)")
                }
            }

            // Account & Data Deletion
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Resident Account Management",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Residents have the statutory right under RA 10173 to request deletion or anonymization of digital records from the mobile platform.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { showDeleteAccountDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RestrainedCoralRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Resident Account & Data")
                }
            }

            // About e-Barangay Sua
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        modifier = Modifier.size(54.dp),
                        shape = CircleShape,
                        color = CoastalSurface,
                        shadowElevation = 3.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_sua_logo),
                                contentDescription = "Barangay Sua Emblem",
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "About e-Barangay Sua",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Barangay Sua Local Government Unit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepOceanBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Version: 1.0.0 (Release 2026)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "San Juan, Southern Leyte, Philippines",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RestrainedCoralRed)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text("Data Privacy Policy (RA 10173)", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "1. Information Collected:\nWe collect basic resident identity info (full name, address, contact, household ID) strictly for public certificate verification and life-safety SOS alerts.\n\n" +
                                "2. Legal Basis:\nProcessing is authorized under Republic Act 10173 and Republic Act 7160 (Local Government Code of the Philippines).\n\n" +
                                "3. Purpose:\nData is used solely to issue official barangay clearances, coordinate local first responders during typhoons/emergencies, and maintain the civil registry.\n\n" +
                                "4. Non-Disclosure:\nYour information is never sold or shared with commercial entities. It is accessible solely to designated Barangay Sua administrative and emergency officers.\n\n" +
                                "5. Data Subject Rights:\nYou have the right to inspect, correct, or request deletion of your digital records at any time.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Text("Delete Account & Data", fontWeight = FontWeight.Bold, color = RestrainedCoralRed)
            },
            text = {
                Text(
                    "Are you sure you want to delete your mobile resident account? This will permanently remove your stored local sessions, clearance requests, and notifications from this device. Physical municipal archives at Barangay Hall will remain intact as required by law.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        scope.launch {
                            repository.logout()
                            prefs.edit().clear().apply()
                            onLogout()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RestrainedCoralRed)
                ) {
                    Text("Confirm Deletion", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
