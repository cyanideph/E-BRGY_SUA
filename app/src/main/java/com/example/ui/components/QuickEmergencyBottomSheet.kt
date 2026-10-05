package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BarangayRepository
import com.example.model.EmergencyType
import com.example.ui.theme.*

/**
 * 1-Tap Quick Emergency Bottom Sheet Modal.
 * Designed for immediate life-safety dispatches with pre-populated GPS
 * and fallback phone dialers.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickEmergencyBottomSheet(
    onDismiss: () -> Unit,
    onEmergencyDispatched: (trackingCode: String) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { BarangayRepository.instance }
    val userSession by repository.currentUser.collectAsState()

    var selectedType by remember { mutableStateOf(EmergencyType.MEDICAL) }
    var locationDescription by remember { mutableStateOf("Near Purok 1 Beachfront, Barangay Sua") }
    var incidentNotes by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RestrainedCoralRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "EMERGENCY DISPATCH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RestrainedCoralRed,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Barangay Sua Quick SOS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Immediate Emergency Category Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EmergencyTile(
                    title = "Medical",
                    icon = Icons.Default.LocalHospital,
                    color = RestrainedCoralRed,
                    isSelected = selectedType == EmergencyType.MEDICAL,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedType = EmergencyType.MEDICAL }
                )

                EmergencyTile(
                    title = "Coastal / Sea",
                    icon = Icons.Default.Waves,
                    color = SouthernSeaTeal,
                    isSelected = selectedType == EmergencyType.RESCUE_DISASTER,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedType = EmergencyType.RESCUE_DISASTER }
                )

                EmergencyTile(
                    title = "Fire",
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color(0xFFE65100),
                    isSelected = selectedType == EmergencyType.FIRE,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedType = EmergencyType.FIRE }
                )

                EmergencyTile(
                    title = "Tanod / Police",
                    icon = Icons.Default.Shield,
                    color = DeepOceanBlue,
                    isSelected = selectedType == EmergencyType.BARANGAY_EMERGENCY,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedType = EmergencyType.BARANGAY_EMERGENCY }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location Box
            OutlinedTextField(
                value = locationDescription,
                onValueChange = { locationDescription = it },
                label = { Text("Exact Incident Location / Landmark") },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = RestrainedCoralRed)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = incidentNotes,
                onValueChange = { incidentNotes = it },
                label = { Text("Quick description of situation (optional)") },
                placeholder = { Text("e.g. Fallen banca, injured passenger, high fever") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Direct Call Fallback Hotline
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Direct Hotline (Call if No Internet)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Barangay Tanod: +63 917 800 7821",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:+639178007821")
                            }
                            context.startActivity(intent)
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dial", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Immediate SOS Dispatch Action Button
            Button(
                onClick = {
                    isSending = true
                    val locManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as? android.location.LocationManager
                    val lastLoc = runCatching {
                        listOf(android.location.LocationManager.GPS_PROVIDER, android.location.LocationManager.NETWORK_PROVIDER)
                            .asSequence()
                            .filter { locManager?.isProviderEnabled(it) == true }
                            .mapNotNull { locManager?.getLastKnownLocation(it) }
                            .maxByOrNull { it.time }
                    }.getOrNull()

                    val report = repository.submitEmergency(
                        type = selectedType,
                        description = if (incidentNotes.isNotBlank()) "$locationDescription - $incidentNotes" else locationDescription,
                        latitude = lastLoc?.latitude,
                        longitude = lastLoc?.longitude,
                        locationDescription = if (lastLoc != null) {
                            "$locationDescription (GPS: %.5f, %.5f)".format(java.util.Locale.US, lastLoc.latitude, lastLoc.longitude)
                        } else locationDescription
                    )
                    Toast.makeText(context, "SOS Sent! Tanod & Responders dispatched.", Toast.LENGTH_LONG).show()
                    isSending = false
                    onDismiss()
                    onEmergencyDispatched(report.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RestrainedCoralRed)
            ) {
                if (isSending) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DISPATCH EMERGENCY RESPONDERS",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EmergencyTile(
    title: String,
    icon: ImageVector,
    color: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) color else Color.Gray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ),
        color = if (isSelected) color.copy(alpha = 0.15f) else Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
