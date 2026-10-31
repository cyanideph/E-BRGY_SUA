package com.example.ui.resident.emergency

import androidx.compose.foundation.Form
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.R

/** Emergency Report Screen - Resident reports an emergency incident.
 * 
 *  Allows residents to report emergencies (fire, medical, disaster, accident, crime)
 *  with geolocation and severity selection. Triggers realtime notification to officials.
 *  Follows constitution principles:
 *  - VII: Accessibility (contentDescription, 48dp touch targets, contrast)
 *  - VIII: Testing (tests required before merge per constitution VIII)
 *  - IX: Compatibility (min SDK 24, works on entry-level devices)
 *  - IV: Environment-based secrets (no hardcoded credentials)
 */
@Composable
fun EmergencyReportScreen(
    viewModel: EmergencyReportViewModel = EmergencyReportViewModel(),
    onEmergencyReported: ((EmergencyReport) -> Unit)? = null
) {
    var emergencyType by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isOnDuty by remember { mutableStateOf(false) }


    // Severity color-coding badge
    @Composable
    fun severityBadge(severity: String) = when(severity) {
        "CRITICAL" -> {
            OutlinedChip(
                label = {
                    Text(
                        text = "CRITICAL",
                        contentDescription = "Severity: critical red indicator"
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.error,
                onClick = {}
            )
        }
        "HIGH" -> {
            OutlinedChip(
                label = {
                    Text(
                        text = "HIGH",
                        contentDescription = "Severity: high orange indicator"
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.warning,
                onClick = {}
            )
        }
        "MEDIUM" -> {
            OutlinedChip(
                label = {
                    Text(
                        text = "MEDIUM",
                        contentDescription = "Severity: medium yellow indicator"
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.secondary,
                onClick = {}
            )
        }
        "LOW" -> {
            OutlinedChip(
                label = {
                    Text(
                        text = "LOW",
                        contentDescription = "Severity: low green indicator"
                    )
                },
                backgroundColor = MaterialTheme.colorScheme.surface,
                onClick = {}
            )
        }
    }

    var error by remember { mutableStateOf<String?>(null) }
    var loadedLocation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Report Emergency",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Emergency Type Selector
        OutlinedTextField(
            value = emergencyType,
            onValueChange = { emergencyType = it },
            label = { Text("Emergency Type", contentDescription = "Emergency type selection") },
            leadingIcon = { Icon(Icons.BaseIcons.warning_amber, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        // Severity Selector
        OutlinedTextField(
            value = severity,
            onValueChange = { severity = it },
            label = { Text("Severity", contentDescription = "Emergency severity selection") },
            leadingIcon = { Icon(Icons.BaseIcons.flag, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        // Location
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location / Description") },
            leadingIcon = { Icon(Icons.BaseIcons.map, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(0.8f),
            keyboardType = KeyboardType.multiline,
            softInputMode = SoftInputMode.alt,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Additional Details") },
            leadingIcon = { Icon(Icons.BaseIcons.note, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(0.8f),
            keyboardType = KeyboardType.multiline,
            softInputMode = SoftInputMode.always,
            maxLines = 4
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

        // Report button
        Button(
            onClick = {
                // Validate and report
                val validationErrors = EmergencyFormValidator.validate(
                    emergencyType = emergencyType,
                    severity = severity,
                    location = location,
                    description = description
                )

                if (validationErrors.isEmpty()) {
                    viewModel.reportEmergency(
                        emergencyType = emergencyType,
                        severity = severity,
                        location = location,
                        description = description
                    ) { emergencyReport ->
                        if (emergencyReport != null) {
                            onEmergencyReported?.invoke(emergencyReport)
                        } else {
                            error = "Failed to report emergency. Please try again."
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
                    "Reporting Emergency..."
                } else {
                    "Report Emergency"
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cancel button
        OutlinedButton(
            onClick = { /* Navigate back or dismiss */ },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Cancel", style = MaterialTheme.typography.labelSmall)
        }
    }
}

/** Emergency Form Validator - validates emergency report form inputs. */
object EmergencyFormValidator {

    /** Validate emergency type */
    fun validateEmergencyType(type: String): String {
        if (type.isEmpty()) {
            return "Emergency type is required"
        }
        val validTypes = listOf("FIRE", "MEDIUM", "DISASTER", "ACCIDENT", "CRIME", "OTHER")
        if (!validTypes.contains(type.uppercase())) {
            return "Invalid emergency type. Valid: ${validTypes.joinToString(", ")}"
        }
        return "" // valid
    }

    /** Validate severity */
    fun validateSeverity(severity: String): String {
        if (severity.isEmpty()) {
            return "Severity level is required"
        }
        val validSeverities = listOf("LOW", "MEDIUM", "HIGH", "CRITICAL")
        if (!validSeverities.contains(severity.uppercase())) {
            return "Invalid severity. Valid: ${validSeverities.joinToString(", ")}"
        }
        return "" // valid
    }

    /** Validate location */
    fun validateLocation(location: String): String {
        if (location.isEmpty()) {
            return "Location is required"
        }
        if (location.length < 5) {
            return "Location must be at least 5 characters"
        }
        return "" // valid
    }

    /** Validate description */
    fun validateDescription(description: String): String {
        if (description.isEmpty()) {
            return "Description is required"
        }
        if (description.length < 10) {
            return "Description must be at least 10 characters"
        }
        return "" // valid
    }

    /** Validate all fields - returns list of error messages */
    fun validate(
        emergencyType: String,
        severity: String,
        location: String,
        description: String
    ): List<String> {
        val errors = mutableListOf<String>()

        val et = validateEmergencyType(emergencyType)
        if (et.isNotEmpty()) errors.add(et)

        val sev = validateSeverity(severity)
        if (sev.isNotEmpty()) errors.add(sev)

        val loc = validateLocation(location)
        if (loc.isNotEmpty()) errors.add(loc)

        val desc = validateDescription(description)
        if (desc.isNotEmpty()) errors.add(desc)

        return errors
    }
}