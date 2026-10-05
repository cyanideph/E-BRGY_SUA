package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ResidentProfile
import com.example.ui.theme.*

/**
 * Official Digital Resident ID Card for Barangay Sua residents.
 * Designed with soft-skeuomorphic card depth, official municipal headers,
 * security hologram badge, and scannable verification QR.
 */
@Composable
fun DigitalResidentIdCard(
    profile: ResidentProfile,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = DeepOceanBlue.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        DeepOceanBlue,
                        Color(0xFF0A406F),
                        SouthernSeaTealDark
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        WarmSunGold.copy(alpha = 0.7f),
                        SouthernSeaTeal.copy(alpha = 0.3f),
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { isExpanded = !isExpanded }
            .padding(18.dp)
    ) {
        // Official Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Barangay Seal Badge Placeholder
            Surface(
                shape = CircleShape,
                color = WarmSunGold,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Barangay Official Seal",
                        tint = DeepOceanBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "REPUBLIC OF THE PHILIPPINES",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "MUNICIPALITY OF SAN JUAN • SOUTHERN LEYTE",
                    fontSize = 8.sp,
                    color = WarmSunGoldLight,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "BARANGAY SUA DIGITAL RESIDENT ID",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
            }

            // Status chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NaturalGreen.copy(alpha = 0.25f),
                border = androidx.compose.foundation.BorderStroke(1.dp, NaturalGreen)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF69F0AE))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "VERIFIED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF69F0AE)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Resident Photo & Primary Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo Avatar Box
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(1.5.dp, WarmSunGoldLight, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.fullName.ifEmpty { "Resident Name" },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = profile.address.ifEmpty { "Purok 1, Barangay Sua" },
                    fontSize = 12.sp,
                    color = WarmSunGoldContainer,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )

                Text(
                    text = "Household: ${profile.householdId.ifEmpty { "HH-SUA-001" }}",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Text(
                    text = "ID: ${profile.residentId.ifEmpty { profile.id.ifEmpty { "SUA-RES-2026-0042" } }}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Metadata Grid (Sex, Civil Status, Occupation, Status)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black.copy(alpha = 0.2f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("SEX", fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f))
                Text(profile.sex.ifEmpty { "Female" }, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column {
                Text("CIVIL STATUS", fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f))
                Text(profile.civilStatus.ifEmpty { "Single" }, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column {
                Text("OCCUPATION", fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f))
                Text(profile.occupation.ifEmpty { "Fisherfolk" }.take(12), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column {
                Text("REGISTRATION", fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f))
                Text("Active Resident", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF69F0AE))
            }
        }

        // Expandable QR Verification Code & Hologram
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Simulated QR matrix pattern
                        DocumentQrPattern(code = profile.residentId.ifEmpty { profile.id.ifEmpty { "SUA-RES-2026-0042" } })
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "SCAN AT BARANGAY HALL FOR RAPID CHECK-IN",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepNavy,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Emergency: ${profile.emergencyContactName} (${profile.emergencyContactPhone})",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tap to toggle prompt
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.QrCode,
                contentDescription = null,
                tint = WarmSunGoldLight,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isExpanded) "Tap to collapse card" else "Tap to show official QR verification code",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = WarmSunGoldLight
            )
        }
    }
}
