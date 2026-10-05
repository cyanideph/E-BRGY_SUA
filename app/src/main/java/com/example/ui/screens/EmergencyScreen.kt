package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.location.LocationManager
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BarangayRepository
import com.example.model.EmergencyReport
import com.example.model.EmergencyType
import com.example.ui.components.SoftSkeuomorphicCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { BarangayRepository.instance }
    val userSession by repository.currentUser.collectAsState()
    val hotlines = remember { repository.hotlines }

    var selectedType by remember { mutableStateOf(EmergencyType.BARANGAY_EMERGENCY) }
    var description by remember { mutableStateOf("") }
    var locationNote by remember { mutableStateOf("Barangay Sua, San Juan, Southern Leyte") }
    var locationCoordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var isLocationPermissionGranted by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var submittedReport by remember { mutableStateOf<EmergencyReport?>(null) }
    var showPermissionRationale by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            isLocationPermissionGranted = true
            // Read a real device location. Never present a fixed coordinate as GPS.
            val manager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
            val lastLocation = runCatching {
                listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
                    .asSequence()
                    .filter { manager.isProviderEnabled(it) }
                    .mapNotNull { provider -> manager.getLastKnownLocation(provider) }
                    .maxByOrNull { it.time }
            }.getOrNull()

            if (lastLocation != null) {
                locationCoordinates = Pair(lastLocation.latitude, lastLocation.longitude)
                locationNote = "GPS Captured: %.6f, %.6f".format(
                    Locale.US, lastLocation.latitude, lastLocation.longitude
                )
            } else {
                locationCoordinates = null
                locationNote = "Permission granted, but no recent GPS fix is available. Sua identity remains verified."
            }
        } else {
            showPermissionRationale = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Response & SOS", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RestrainedCoralRed,
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
                .padding(16.dp)
        ) {
            if (submittedReport != null) {
                // Post-submission banner
                SoftSkeuomorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NaturalGreenContainer,
                    borderColor = NaturalGreen
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NaturalGreen, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Emergency Alert Dispatched!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NaturalGreen
                            )
                            Text(
                                text = "Report ID: ${submittedReport?.id}. Barangay Tanods & San Juan MDRRMO notified.",
                                style = MaterialTheme.typography.bodySmall,
                                color = DeepNavy
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { submittedReport = null },
                        colors = ButtonDefaults.buttonColors(containerColor = NaturalGreen)
                    ) {
                        Text("Send Another Alert or Return")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // SOS Alert Trigger Form
            SoftSkeuomorphicCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = RestrainedCoralRedContainer.copy(alpha = 0.35f),
                borderColor = RestrainedCoralRed.copy(alpha = 0.5f),
                elevation = 4.dp
            ) {
                Text(
                    text = "Broadcast Emergency SOS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = RestrainedCoralRed
                )
                Text(
                    text = "Select emergency category and alert local emergency first responders immediately.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DeepNavySecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Type of Emergency:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                EmergencyType.values().forEach { type ->
                    val isSelected = (selectedType == type)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { selectedType = type },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) RestrainedCoralRed else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) RestrainedCoralRed else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedType = type },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color.White,
                                    unselectedColor = RestrainedCoralRed
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = type.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = type.iconDescription,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Situation Description (injuries, hazards, urgency)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Explicit Location Request
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Emergency Location:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = locationNote,
                            fontSize = 11.sp,
                            color = if (isLocationPermissionGranted) SouthernSeaTealDark else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isLocationPermissionGranted) "Acquired" else "GPS Tag", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        isSubmitting = true
                        val report = repository.submitEmergency(
                            type = selectedType,
                            description = description.ifEmpty { "Immediate emergency assistance requested in Barangay Sua." },
                            latitude = locationCoordinates?.first,
                            longitude = locationCoordinates?.second,
                            locationDescription = locationNote
                        )
                        submittedReport = report
                        description = ""
                        isSubmitting = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RestrainedCoralRed,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Emergency, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TRANSMIT SOS ALERT NOW",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // OFFICIAL DIRECT HOTLINES
            Text(
                text = "Official Emergency Hotlines",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Tap to place a direct phone call to first responders",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                hotlines.forEach { hotline ->
                    SoftSkeuomorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${hotline.number.replace(" ", "")}"))
                            context.startActivity(intent)
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = DeepOceanContainer,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Call, contentDescription = null, tint = DeepOceanBlue)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = hotline.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = hotline.agency,
                                    fontSize = 11.sp,
                                    color = SouthernSeaTealDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = hotline.number,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepOceanBlue
                                )
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${hotline.number.replace(" ", "")}"))
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SouthernSeaTeal,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
