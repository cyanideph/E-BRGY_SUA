package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BarangayRepository
import com.example.model.RequestStatus
import com.example.model.UserRole
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailsScreen(
    requestId: String,
    onNavigateBack: () -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val userSession by repository.currentUser.collectAsState()
    val requests by repository.requests.collectAsState()

    val request = remember(requestId, requests) {
        requests.firstOrNull { it.id == requestId || it.referenceNumber == requestId }
            ?: requests.first()
    }

    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.US) }

    var showStaffDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var ratingSubmitted by remember { mutableStateOf(false) }
    var newStatus by remember { mutableStateOf(request.status) }
    var staffRemarks by remember { mutableStateOf(request.officialRemarks) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(request.referenceNumber, fontWeight = FontWeight.Bold, fontSize = 17.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (userSession.role != UserRole.RESIDENT) {
                        IconButton(onClick = { showStaffDialog = true }) {
                            Icon(Icons.Default.EditNote, contentDescription = "Update Status")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepOceanBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
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
            // Header Overview Card
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = request.serviceName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    RequestStatusBadge(status = request.status)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SyncStatusBadge(isSynced = request.isSyncedToServer)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Updated: ${dateFormat.format(Date(request.updatedAt))}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Official Remarks:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SouthernSeaTealDark
                )
                Text(
                    text = request.officialRemarks.ifEmpty { "No official remarks yet." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Official Verification QR Presentation Code
            DocumentQrCard(
                referenceNumber = request.referenceNumber,
                title = "Official Releasing QR Code",
                subtitle = "Present this code to the Barangay Sua records officer to release and sign for your document."
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Application Details
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Request Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                DetailRow(label = "Applicant", value = request.residentName)
                DetailRow(label = "Address", value = request.residentAddress)
                DetailRow(label = "Contact", value = request.residentContact)
                DetailRow(label = "Purpose", value = request.purpose)
                DetailRow(label = "Delivery / Pick-up", value = request.deliveryMethod)
                if (request.remarks.isNotEmpty()) {
                    DetailRow(label = "Applicant Notes", value = request.remarks)
                }
                DetailRow(label = "Attachments", value = request.attachmentNames.joinToString(", ").ifEmpty { "None" })
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Complete Timeline
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Application Timeline",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                request.timeline.forEachIndexed { index, event ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = if (index == request.timeline.size - 1) SouthernSeaTeal else DeepOceanContainer,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (index == request.timeline.size - 1) Icons.Default.Check else Icons.Default.Circle,
                                        contentDescription = null,
                                        tint = if (index == request.timeline.size - 1) Color.White else DeepOceanBlue,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            if (index < request.timeline.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(44.dp)
                                        .background(MaterialTheme.colorScheme.outlineVariant)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.padding(bottom = 12.dp)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = event.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${dateFormat.format(Date(event.timestamp))} • By ${event.actorName}",
                                fontSize = 11.sp,
                                color = SouthernSeaTealDark
                            )
                        }
                    }
                }
            }

            // ARTA Citizen Feedback Section (for Ready or Completed documents)
            if (request.status == RequestStatus.READY || request.status == RequestStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(16.dp))
                SoftSkeuomorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = if (ratingSubmitted) NaturalGreenContainer.copy(alpha = 0.4f) else WarmSunGoldContainer.copy(alpha = 0.35f),
                    borderColor = if (ratingSubmitted) NaturalGreen else WarmSunGold
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (ratingSubmitted) "Salamat sa Puna (Rating Submitted)" else "ARTA Citizen's Charter Feedback",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (ratingSubmitted) NaturalGreen else DeepNavy
                            )
                            Text(
                                text = if (ratingSubmitted) "Your feedback helps improve Barangay Sua public services." else "How was your experience requesting this document?",
                                style = MaterialTheme.typography.bodySmall,
                                color = DeepNavySecondary
                            )
                        }

                        if (!ratingSubmitted) {
                            Button(
                                onClick = { showRatingDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepOceanBlue),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Rate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NaturalGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // If staff/official/admin, provide quick status updater button
            if (userSession.role != UserRole.RESIDENT) {
                Button(
                    onClick = { showStaffDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepOceanBlue,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Staff: Update Request Status & Remarks", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Staff Status Update Dialog
        if (showStaffDialog) {
            AlertDialog(
                onDismissRequest = { showStaffDialog = false },
                title = { Text("Update Document Status", fontWeight = FontWeight.Bold) },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Select new official status:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))

                        RequestStatus.values().forEach { status ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                RadioButton(
                                    selected = (newStatus == status),
                                    onClick = { newStatus = status }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(status.label, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = staffRemarks,
                            onValueChange = { staffRemarks = it },
                            label = { Text("Official remarks / release window") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            repository.updateRequestStatus(
                                requestId = request.id,
                                newStatus = newStatus,
                                officialRemarks = staffRemarks
                            )
                            showStaffDialog = false
                        }
                    ) {
                        Text("Save Status")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStaffDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // ARTA Citizen Rating Dialog
        if (showRatingDialog) {
            CitizenRatingDialog(
                documentName = request.serviceName,
                onDismiss = { showRatingDialog = false },
                onSubmit = { rating: Int, feedback: String ->
                    showRatingDialog = false
                    ratingSubmitted = true
                }
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
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
