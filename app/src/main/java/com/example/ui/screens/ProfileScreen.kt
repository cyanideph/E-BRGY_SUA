package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.BarangayRepository
import com.example.model.UserRole
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToAdminDashboard: () -> Unit,
    onLogout: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val userSession by repository.currentUser.collectAsState()
    val isOnline by repository.isOnline.collectAsState()

    var showRoleDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SuaWaveHeader(
            greeting = "Resident Portal",
            name = "My Profile",
            location = "Barangay Sua • San Juan, Southern Leyte",
            trailingAction = {
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }
        )

        Column(modifier = Modifier.padding(16.dp)) {
            // Profile Card
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = DeepOceanContainer,
                        modifier = Modifier.size(68.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = DeepOceanBlue,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userSession.profile.fullName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Resident ID: ${userSession.profile.residentId}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SouthernSeaTealDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        RoleBadge(role = userSession.role)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Official Digital Resident ID Card
            Text(
                text = "Official Resident Credential",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            DigitalResidentIdCard(profile = userSession.profile)

            Spacer(modifier = Modifier.height(16.dp))

            // Household & Demographic Details
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Household & Resident Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                ProfileFieldItem(icon = Icons.Default.Home, label = "Registered Address", value = userSession.profile.address)
                ProfileFieldItem(icon = Icons.Default.Phone, label = "Mobile Number", value = userSession.profile.mobileNumber)
                ProfileFieldItem(icon = Icons.Default.CalendarToday, label = "Date of Birth", value = userSession.profile.dateOfBirth)
                ProfileFieldItem(icon = Icons.Default.Favorite, label = "Civil Status", value = userSession.profile.civilStatus)
                ProfileFieldItem(icon = Icons.Default.Work, label = "Occupation", value = userSession.profile.occupation)
                ProfileFieldItem(icon = Icons.Default.FamilyRestroom, label = "Household ID", value = userSession.profile.householdId)
                ProfileFieldItem(icon = Icons.Default.Emergency, label = "Emergency Contact", value = "${userSession.profile.emergencyContactName} • ${userSession.profile.emergencyContactPhone}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Role Switcher Card (MANDATORY for testing Resident, Staff, Official, Administrator views)
            SoftSkeuomorphicCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SouthernSeaTealContainer.copy(alpha = 0.4f),
                borderColor = SouthernSeaTeal
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Active Access Role",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SouthernSeaTealDark
                        )
                        Text(
                            text = userSession.role.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepOceanBlue
                        )
                    }

                    Button(
                        onClick = { showRoleDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SouthernSeaTealDark)
                    ) {
                        Text("Switch Role")
                    }
                }
            }

            // Admin Dashboard button if role allows
            if (userSession.role != UserRole.RESIDENT) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onNavigateToAdminDashboard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepOceanBlue)
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Staff & Admin Management Console", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Appearance & Theme Mode (Default is Light Mode)
            val themeMode by repository.themeMode.collectAsState()
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Dark Mode / Light Mode",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (themeMode == com.example.data.AppThemeMode.DARK) "Currently: Dark Mode" else "Currently: Light Mode (Default)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (themeMode == com.example.data.AppThemeMode.DARK) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = (themeMode == com.example.data.AppThemeMode.DARK),
                            onCheckedChange = { repository.toggleTheme() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Connectivity Test Toggle
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Simulate Offline Mode",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isOnline) "App is online (Firestore live)" else "App is offline (Using local cache)",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isOnline) NaturalGreen else WarmAmber
                        )
                    }
                    Switch(
                        checked = !isOnline,
                        onCheckedChange = { repository.toggleOnline(!it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RestrainedCoralRed)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        if (showRoleDialog) {
            AlertDialog(
                onDismissRequest = { showRoleDialog = false },
                title = { Text("Select Role Access", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(
                            text = "Switch roles to preview resident vs official vs administrator permissions:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        UserRole.values().forEach { role ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (userSession.role == role),
                                    onClick = {
                                        repository.switchRole(role)
                                        showRoleDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(role.displayName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        when (role) {
                                            UserRole.RESIDENT -> "Standard resident: apply for services, track requests"
                                            UserRole.STAFF -> "Process requests, add remarks, manage announcements"
                                            UserRole.OFFICIAL -> "Council member: publish advisories, review reports"
                                            UserRole.ADMIN -> "Full administrative control, audit logs, resident registry"
                                        },
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showRoleDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileFieldItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DeepOceanBlue,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepNavySecondary
            )
            Text(
                text = value,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
