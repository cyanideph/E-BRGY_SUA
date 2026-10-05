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
