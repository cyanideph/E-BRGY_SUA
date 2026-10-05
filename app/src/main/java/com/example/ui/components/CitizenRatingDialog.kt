package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

/**
 * ARTA / Citizen's Charter Service Satisfaction Survey Dialog
 * for completed Barangay Sua document requests and transactions.
 */
@Composable
fun CitizenRatingDialog(
    documentName: String,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, feedback: String) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var feedback by remember { mutableStateOf("") }
    var isPromptService by remember { mutableStateOf(true) }
    var isCourteousStaff by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Seal / Header
                Text(
                    text = "CITIZEN'S CHARTER SATISFACTION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepOceanBlue,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rate Your Service Experience",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Document: $documentName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Star Rating
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { starIndex ->
                        val isFilled = starIndex <= rating
                        IconButton(onClick = { rating = starIndex }) {
                            Icon(
                                imageVector = if (isFilled) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = "$starIndex Stars",
                                tint = if (isFilled) WarmSunGold else CoastalBorderSoft,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }

                Text(
                    text = when (rating) {
                        5 -> "Lubhang Kasiya-siya (Outstanding)"
                        4 -> "Kasiya-siya (Very Good)"
                        3 -> "Katamtaman (Satisfactory)"
                        2 -> "Hindi Kasiya-siya (Needs Improvement)"
                        else -> "Labis na Hindi Kasiya-siya (Poor)"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepOceanBlue
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick checklist tags
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPromptService = !isPromptService }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isPromptService,
                            onCheckedChange = { isPromptService = it },
                            colors = CheckboxDefaults.colors(checkedColor = DeepOceanBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mabilis at maagap ang pagproseso (Prompt processing)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isCourteousStaff = !isCourteousStaff }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isCourteousStaff,
                            onCheckedChange = { isCourteousStaff = it },
                            colors = CheckboxDefaults.colors(checkedColor = DeepOceanBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Magalang at matulungin ang tauhan (Courteous staff)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    placeholder = { Text("Mungkahi o puna para mapabuti ang serbisyo (Optional comments)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Later")
                    }

                    Button(
                        onClick = { onSubmit(rating, feedback) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepOceanBlue)
                    ) {
                        Text("Submit Rating", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
