package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BarangayRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestFormScreen(
    serviceId: String,
    onNavigateBack: () -> Unit,
    onRequestSubmitted: (String) -> Unit
) {
    val repository = remember { BarangayRepository.instance }
    val scope = rememberCoroutineScope()
    val userSession by repository.currentUser.collectAsState()
    val service = remember(serviceId) {
        repository.services.value.firstOrNull { it.id == serviceId }
            ?: repository.services.value.first()
    }

    val baseFee: Double = remember(service.feeDescription) {
        val regex = Regex("""[0-9]+(\.[0-9]+)?""")
        val match = regex.find(service.feeDescription)
        match?.value?.toDoubleOrNull() ?: 0.0
    }

    var purpose by remember { mutableStateOf(service.purposeExamples.firstOrNull() ?: "") }
    var customPurpose by remember { mutableStateOf("") }
    var deliveryMethod by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf(emptyList<String>()) }
    var selectedWaiver by remember { mutableStateOf(StatutoryWaiverType.NONE) }
    var calculatedFee by remember { mutableStateOf(baseFee) }
    var agreedToTerms by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "resident_document_${System.currentTimeMillis()}.jpg"
            attachments = attachments + fileName
        }
    }

    val deliveryOptions = listOf("Pick-up at Barangay Hall", "Digital Copy via Email", "Purok Tanod Assistance Delivery")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Application Form", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepOceanBlue,
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
            // Form Step Progress Wizard
            FormStepProgressIndicator(
                currentStep = 3,
                steps = listOf("Resident", "Purpose", "Fee & Submit")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Service Summary Card
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DeepOceanContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = DeepOceanBlue)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = service.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Barangay Sua, San Juan, Southern Leyte",
                            style = MaterialTheme.typography.bodySmall,
                            color = SouthernSeaTealDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Applicant Information (Auto-filled from verified resident profile)
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Applicant Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NaturalGreenContainer
                    ) {
                        Text(
                            text = "Verified Resident",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Full Name: ${userSession.profile.fullName}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Resident ID: ${userSession.profile.residentId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Address: ${userSession.profile.address}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Mobile: ${userSession.profile.mobileNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Purpose Selection
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Purpose of Request",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                service.purposeExamples.forEach { example ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { purpose = example }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (purpose == example),
                            onClick = { purpose = example },
                            colors = RadioButtonDefaults.colors(selectedColor = DeepOceanBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = example,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customPurpose,
                    onValueChange = {
                        customPurpose = it
                        purpose = it
                    },
                    label = { Text("Or specify other custom purpose") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Delivery / Pick-up Method
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Receiving Method",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                deliveryOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { deliveryMethod = option }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (deliveryMethod == option),
                            onClick = { deliveryMethod = option },
                            colors = RadioButtonDefaults.colors(selectedColor = DeepOceanBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Additional Remarks & Attachments
            SoftSkeuomorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Attachments & Notes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Special notes or instructions (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Attached Documents:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                attachments.forEach { file ->
                    Surface(
                        color = DeepOceanContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AttachFile, contentDescription = null, tint = DeepOceanBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = file, fontSize = 12.sp, color = DeepNavy)
                            }
                            Icon(Icons.Default.Check, contentDescription = null, tint = NaturalGreen, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Supporting ID / Document Photo")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Statutory Fee Waiver & Exemption Calculator
            StatutoryWaiverSelector(
                baseFee = baseFee,
                selectedWaiver = selectedWaiver,
                onWaiverSelected = { waiver, fee ->
                    selectedWaiver = waiver
                    calculatedFee = fee
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Terms Agreement
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { agreedToTerms = !agreedToTerms }
            ) {
                Checkbox(
                    checked = agreedToTerms,
                    onCheckedChange = { agreedToTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = DeepOceanBlue)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "I certify that all submitted information is accurate and verified under the jurisdiction of Barangay Sua.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = {
                    if (purpose.isBlank()) {
                        errorMessage = "Please select or enter the purpose for this document request."
                    } else if (!agreedToTerms) {
                        errorMessage = "You must agree to the truthfulness certification."
                    } else {
                        isSubmitting = true
                        val finalRemarks = if (selectedWaiver != StatutoryWaiverType.NONE) {
                            if (remarks.isNotBlank()) "$remarks | [Exemption: ${selectedWaiver.title} - Net: ₱${String.format("%.2f", calculatedFee)}]"
                            else "[Exemption: ${selectedWaiver.title} - Net: ₱${String.format("%.2f", calculatedFee)}]"
                        } else remarks

                        scope.launch {
                            runCatching {
                                repository.submitRequest(
                                    service = service,
                                    purpose = purpose,
                                    deliveryMethod = deliveryMethod,
                                    remarks = finalRemarks,
                                    attachmentNames = attachments
                                )
                            }.onSuccess {
                                isSubmitting = false
                                onRequestSubmitted(it.referenceNumber)
                            }.onFailure {
                                isSubmitting = false
                                errorMessage = it.message ?: "Unable to submit the request. Please try again."
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepOceanBlue,
                    contentColor = Color.White
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Submit request",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
