package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BarangayRepository
import com.example.ui.theme.*

/**
 * Read-only connection state. Connectivity is owned by the repository;
 * the UI must never let a user toggle a fake online/offline state.
 */
@Composable
fun GlobalOfflineSyncPill(modifier: Modifier = Modifier) {
    val repository = remember { BarangayRepository.instance }
    val isOnline by repository.isOnline.collectAsState()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = if (isOnline) NaturalGreenContainer.copy(alpha = 0.85f) else WarmAmberContainer.copy(alpha = 0.95f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isOnline) NaturalGreen.copy(alpha = 0.5f) else WarmAmber
        )
    ) {
        Row(
            modifier = Modifier.defaultMinSize(minHeight = 40.dp).padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(8.dp).clip(CircleShape)
                    .background(if (isOnline) NaturalGreen else WarmAmber)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = if (isOnline) Icons.Default.CloudDone else Icons.Default.CloudOff,
                contentDescription = if (isOnline) "Connected to server" else "Offline; showing cached data",
                tint = if (isOnline) NaturalGreen else Color(0xFF7A5900),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isOnline) "Server live" else "Offline",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOnline) NaturalGreen else Color(0xFF7A5900)
            )
        }
    }
}
