package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class StatutoryWaiverType(
    val title: String,
    val subtitle: String,
    val discountPercent: Int
) {
    NONE("Regular Processing", "Standard municipal barangay fee applies", 0),
    RA_11261("RA 11261 First-Time Jobseeker", "100% Free under First-Time Jobseekers Assistance Act", 100),
    INDIGENCY("Certificate of Indigency Waiver", "100% Free for registered indigent households", 100),
    SENIOR_PWD("Senior Citizen / PWD Discount", "20% statutory discount under RA 9994 / RA 10754", 20)
}

/**
 * Statutory Fee Exemption & Waiver Calculator Component
 * ensuring compliance with Philippine laws (RA 11261, RA 9994).
 */
@Composable
fun StatutoryWaiverSelector(
    baseFee: Double,
    selectedWaiver: StatutoryWaiverType,
    onWaiverSelected: (StatutoryWaiverType, finalFee: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val finalFee = remember(baseFee, selectedWaiver) {
        val discount = (baseFee * (selectedWaiver.discountPercent / 100.0))
        (baseFee - discount).coerceAtLeast(0.0)
    }

    SoftSkeuomorphicCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Statutory Discounts & Fee Exemptions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Check if you qualify for legal fee waivers under Philippine statutes.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        StatutoryWaiverType.values().forEach { waiver ->
            val isSelected = selectedWaiver == waiver

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        val computedFee = (baseFee - (baseFee * (waiver.discountPercent / 100.0))).coerceAtLeast(0.0)
                        onWaiverSelected(waiver, computedFee)
                    },
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent,
                border = BorderStroke(
                    1.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = {
                            val computedFee = (baseFee - (baseFee * (waiver.discountPercent / 100.0))).coerceAtLeast(0.0)
                            onWaiverSelected(waiver, computedFee)
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = DeepOceanBlue)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = waiver.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = waiver.subtitle,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (waiver.discountPercent > 0) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (waiver.discountPercent == 100) NaturalGreenContainer else WarmSunGoldContainer
                        ) {
                            Text(
                                text = if (waiver.discountPercent == 100) "FREE" else "-20%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (waiver.discountPercent == 100) NaturalGreen else DeepNavy,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Fee Summary Receipt
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DeepOceanContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Base Document Fee:", fontSize = 12.sp, color = DeepOceanBlue)
                    Text("₱${String.format("%.2f", baseFee)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepNavy)
                }

                if (selectedWaiver != StatutoryWaiverType.NONE) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Applied Exemption (${selectedWaiver.title}):", fontSize = 12.sp, color = NaturalGreen)
                        Text(
                            "-₱${String.format("%.2f", baseFee * (selectedWaiver.discountPercent / 100.0))}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = DeepOceanBlue.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TOTAL AMOUNT TO PAY:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeepOceanBlue)
                    Text(
                        text = if (finalFee == 0.0) "₱0.00 (GRATIS / FREE)" else "₱${String.format("%.2f", finalFee)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = if (finalFee == 0.0) NaturalGreen else DeepOceanBlue
                    )
                }
            }
        }
    }
}
