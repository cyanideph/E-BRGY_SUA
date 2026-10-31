package com.example.ui.resident.services

import androidx.compose.foundation.Form
import androidx.compose.foundation.layout.Arrangement.End
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.R

/** Service Request Form - Resident submits service requests.
 * 
 *  Allows residents to submit requests for Barangay services
 *  (certification, clearance, permit, other).
 *  Follows constitution principles:
 *  - VI: Data integrity (validation at repository layer)
 *  - VII: Accessibility (touch targets, contentDescription)
 *  - IX: Compatibility (min SDK 24)
 */
@Composable
fun ServiceRequestForm(
    viewModel: ServiceRequestViewModel = ServiceRequestViewModel(),
    onRequestSubmitted: ((ServiceRequest) -> Unit)? = null
) {
    var serviceType by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var residentName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Submit Service Request",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Service Type Selection
        OutlinedTextField(
            value = serviceType,
            onValueChange = { serviceType = it },
            label = { Text("Service Type") },
            leadingIcon = { Icon(Icons.BaseIcons.build, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        // Resident Name
        OutlinedTextField(
            value = residentName,
            onValueChange = { residentName = it },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.BaseIcons.person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        // Address
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Barangay Address") },
            leadingIcon = { Icon(Icons.BaseIcons.location_on, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        // Contact Number
        OutlinedTextField(
            value = contactNumber,
            onValueChange = { contactNumber = it },
            label = { Text("Contact Number") },
            leadingIcon = { Icon(Icons.BaseIcons.phone, contentDescription = null) },
            keyboardType = KeyboardType.phone,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description / Details") },
            leadingIcon = { Icon(Icons.BaseIcons.note, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(0.8f),
            keyboardType = KeyboardType.multiline,
            softInputMode = SoftInputMode.alternate,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error message
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = {
                // Validate and submit
                val validationErrors = ServiceRequestFormValidator.validate(
                    serviceType = serviceType,
                    description = description,
                    residentName = residentName,
                    address = address,
                    contactNumber = contactNumber
                )

                if (validationErrors.isEmpty()) {
                    viewModel.submitServiceRequest(
                        serviceType = serviceType,
                        description = description,
                        residentName = residentName,
                        address = address,
                        contactNumber = contactNumber
                    ) { request ->
                        if (request != null) {
                            onRequestSubmitted?.invoke(request)
                        } else {
                            error = "Failed to submit request. Please try again."
                        }
                    }
                } else {
                    error = validationErrors.joinToString(", ")
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                if (viewModel.isLoading.value) {
                    "Submitting..."
                } else {
                    "Submit Request"
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cancel button
        Button(
            onClick = { /* Navigate back or dismiss */ },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text("Cancel", style = MaterialTheme.typography.labelSmall)
        }
    }
}

/** Service Request Form Validator - validates form inputs. */
object ServiceRequestFormValidator {

    /** Validate all form fields */
    fun validate(
        serviceType: String,
        description: String,
        residentName: String,
        address: String,
        contactNumber: String
    ): List<String> {
        val errors = mutableListOf<String>()

        // Validate service type
        if (serviceType.isEmpty()) {
            errors.add("Service type is required")
        }

        // Validate description
        if (description.isEmpty()) {
            errors.add("Description is required")
        } else if (description.length < 10) {
            errors.add("Description must be at least 10 characters")
        }

        // Validate resident name
        if (residentName.isEmpty()) {
            errors.add("Full name is required")
        } else if (residentName.length < 2) {
            errors.add("Full name must be at least 2 characters")
        }

        // Validate address
        if (address.isEmpty()) {
            errors.add("Barangay address is required")
        } else if (address.length < 5) {
            errors.add("Address must be at least 5 characters")
        }

        // Validate contact number (Philippine format)
        if (contactNumber.isEmpty()) {
            errors.add("Contact number is required")
        } else if (contactNumber.length < 10) {
            errors.add("Contact number must be at least 10 digits")
        }

        return errors
    }
}