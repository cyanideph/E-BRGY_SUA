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
import com.example.model.BarangayOfficial
import com.example.model.BarangayProfile
import com.example.services.GisPhService
import com.example.ui.components.SoftSkeuomorphicCard
import com.example.ui.components.SuaWaveDivider
import com.example.ui.components.SuaWaveHeader
import com.example.ui.theme.*

@Composable
fun SuaCommunityHubScreen(
    onNavigateToEmergency: () -> Unit,
    onNavigateToAnnouncements: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val officials by repository.officials.collectAsState()
    var selectedOfficialTab by remember { mutableStateOf("All") }
    var barangayProfile by remember { mutableStateOf<BarangayProfile?>(null) }
    var profileLoading by remember { mutableStateOf(true) }
    var profileError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        GisPhService.fetchSuaProfile()
            .onSuccess {
                barangayProfile = it
                profileError = null
            }
            .onFailure {
                profileError = it.message ?: "Unable to load GIS.PH barangay data."
            }
        profileLoading = false
    }

    val roleTabs = listOf("All", "Executive", "Council", "Administration", "Health & Security")

    val filteredOfficials = remember(officials, selectedOfficialTab) {
        when (selectedOfficialTab) {
            "Executive" -> officials.filter { it.roleCategory == "Executive" }
            "Council" -> officials.filter { it.roleCategory == "Council" || it.roleCategory == "Youth Council" }
            "Administration" -> officials.filter { it.roleCategory == "Administration" || it.roleCategory == "Treasury" }
            "Health & Security" -> officials.filter { it.roleCategory == "Health" || it.roleCategory == "Security" }
            else -> officials
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Wave Header
        SuaWaveHeader(
            greeting = "Community",
            name = "Barangay Sua",
            location = "Municipality of San Juan, Southern Leyte"
        )

        Column(modifier = Modifier.padding(16.dp)) {
            // Local Identity & Coastal Heritage Card
            SoftSkeuomorphicCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CoastalSurface,
                elevation = 4.dp
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = DeepOceanContainer,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_sua_logo),
                                contentDescription = "Sua Emblem",
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Barangay Sua at a Glance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepOceanBlue
                        )
                        Text(
                            text = listOf(
                                barangayProfile?.municipality ?: "San Juan",
                                barangayProfile?.province ?: "Southern Leyte",
                                "Postal Code 6611"
                            ).joinToString(" • "),
                            style = MaterialTheme.typography.bodySmall,
                            color = SouthernSeaTealDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Barangay Sua is a barangay of San Juan, Southern Leyte, on the island of Leyte. e-Barangay Sua brings local public services, community information, and barangay officials together in one digital hub.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                SoftSkeuomorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = DeepOceanContainer,
                    elevation = 2.dp
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = DeepOceanBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Barangay Sua identity",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepOceanBlue
                            )
                            when {
                                profileLoading -> Text(
                                    text = "Checking live GIS.PH data…",
                                    fontSize = 11.sp,
                                    color = DeepNavySecondary
                                )
                                barangayProfile?.source == "GIS.PH" -> Text(
                                    text = "Live GIS.PH • PSGC " + barangayProfile!!.psgcCode + " • " + barangayProfile!!.region,
                                    fontSize = 11.sp,
                                    color = DeepNavySecondary
                                )
                                barangayProfile != null -> Text(
                                    text = "Configured service area • PSGC " + barangayProfile!!.psgcCode,
                                    fontSize = 11.sp,
                                    color = DeepNavySecondary
                                )
                                else -> Text(
                                    text = "Configured service area",
                                    fontSize = 11.sp,
                                    color = DeepNavySecondary
                                )
                            }
                        }
                        if (profileLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (barangayProfile?.source == "GIS.PH") Icons.Default.Verified else Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = if (barangayProfile?.source == "GIS.PH") SouthernSeaTealDark else WarmSunGoldDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    if (profileError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Live lookup unavailable; using the configured Barangay Sua service-area identity.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                SuaWaveDivider()

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    InfoStat(label = "Population (2020)", value = "659")
                    InfoStat(label = "Households (2015)", value = "161")
                    InfoStat(label = "Elevation", value = "178.3 m")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Community Pillars & Facilities
            Text(
                text = "Community Facilities & Programs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FacilityItem(
                    title = "Barangay Sua Multipurpose Hall",
                    desc = "Secretariat, document issuance windows, and regular council session hall.",
                    hours = "Monday to Friday: 8:00 AM - 5:00 PM",
                    icon = Icons.Default.AccountBalance
                )
                FacilityItem(
                    title = "Barangay Health Center & Birthing Station",
                    desc = "Primary care, prenatal check-ups, infant immunizations, and medicine distribution.",
                    hours = "Monday to Saturday: 8:00 AM - 4:00 PM (Emergency on-call)",
                    icon = Icons.Default.LocalHospital
                )
                FacilityItem(
                    title = "Marine Sanctuary & Coastal Buffer Zone",
                    desc = "Protected fish sanctuary co-managed with San Juan MENRO to preserve biodiversity.",
                    hours = "Continuous conservation surveillance by Marine Tanod",
                    icon = Icons.Default.Water
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // OFFICIALS DIRECTORY
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Barangay Officials Directory",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Current Barangay Sua Officials",
                        fontSize = 11.sp,
                        color = WarmSunGoldDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Tabs for Directory
            ScrollableTabRow(
                selectedTabIndex = roleTabs.indexOf(selectedOfficialTab).coerceAtLeast(0),
                edgePadding = 0.dp,
                divider = {},
                containerColor = Color.Transparent
            ) {
                roleTabs.forEach { tab ->
                    val isSelected = tab == selectedOfficialTab
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedOfficialTab = tab },
                        label = { Text(tab, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepOceanBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredOfficials.forEach { official ->
                    OfficialCard(official = official)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun InfoStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DeepOceanBlue
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = DeepNavySecondary
        )
    }
}

@Composable
private fun FacilityItem(
    title: String,
    desc: String,
    hours: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SouthernSeaTealContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = SouthernSeaTealDark, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = hours, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = SouthernSeaTealDark)
            }
        }
    }
}

@Composable
private fun OfficialCard(official: BarangayOfficial) {
    SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = DeepOceanContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = DeepOceanBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = official.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = official.position,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SouthernSeaTealDark
                )
                Text(
                    text = official.committee,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                if (official.contactNumber.isNotBlank() || official.officeHours.isNotBlank()) {
                    Text(
                        text = listOf(official.contactNumber, official.officeHours)
                            .filter { it.isNotBlank() }
                            .joinToString(" • "),
                        fontSize = 11.sp,
                        color = DeepNavySecondary
                    )
                }
                if (official.email.isNotBlank()) {
                    Text(
                        text = official.email,
                        fontSize = 11.sp,
                        color = DeepOceanBlue
                    )
                }
            }

            if (official.isDemoRecord) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = WarmSunGoldContainer.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = "Demo",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnWarmSunGoldContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
